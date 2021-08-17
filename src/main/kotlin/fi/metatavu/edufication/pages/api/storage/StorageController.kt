package fi.metatavu.edufication.pages.api.storage

import com.amazonaws.SdkClientException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import fi.metatavu.edufication.pages.api.model.Page
import fi.metatavu.edufication.pages.api.model.PageTemplate
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class StorageController {

    @Inject
    @ConfigProperty(name = "s3.endpoint")
    private lateinit var endpoint: String

    @Inject
    @ConfigProperty(name = "s3.region")
    private lateinit var region: String

    @Inject
    @ConfigProperty(name = "s3.bucket")
    private lateinit var bucket: String

    @Inject
    @ConfigProperty(name = "s3.secret")
    private lateinit var secret: String

    @Inject
    @ConfigProperty(name = "s3.access")
    private lateinit var access: String

    /**
     * Object mapper
     */
    private val objectMapper: ObjectMapper
        get() {
            val objectMapper = ObjectMapper()
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.registerModule(KotlinModule())
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            return objectMapper
        }

    /**
     * Returns S3 client
     */
    private val client: AmazonS3
        get() = AmazonS3ClientBuilder
            .standard()
            .withPathStyleAccessEnabled(true)
            .withCredentials(credentials)
            .withEndpointConfiguration(endpointConfig)
            .build()

    /**
     * AWS endpoint config
     */
    private val endpointConfig: AwsClientBuilder.EndpointConfiguration
      get() = AwsClientBuilder.EndpointConfiguration(endpoint, region)

    /**
     * AWS credentials
     */
    private val credentials: AWSStaticCredentialsProvider
        get() =
            AWSStaticCredentialsProvider(BasicAWSCredentials(access, secret))

    /**
     * Stores a page into s3
     *
     * @param page JPA page
     * @param translatedPage translated page
     * @return object url
     */
    fun storePage(page: fi.metatavu.edufication.pages.api.persistence.model.Page, translatedPage: Page): URL {
        val storedPage = translateToStoredPage(
            page = page,
            translatedPage = translatedPage
        )

        val pageBytes = objectMapper.writeValueAsBytes(storedPage)
        ByteArrayInputStream(pageBytes).use {
            return uploadObject(
                key = "${storedPage.language}/${storedPage.path}.json",
                contentType = "application/json; charset=utf-8",
                contentLength = pageBytes.size.toLong(),
                data = it
            )
        }
    }

    /**
     * Removes a stored page from S3
     *
     * @param page page
     */
    fun deletePage(page: fi.metatavu.edufication.pages.api.persistence.model.Page) {
        val path = page.path ?: return
        val language = page.language ?: return
        return deleteObject("${language}/${path}.json")
    }

    /**
     * Returns a stored file Url
     *
     * @param path page path to find
     * @param language page language to find
     * @return Page url or null if not found
     */
    fun getPageUrl(path: String, language: String): URL? {
        return getObjectFullPath("${language}/${path}.json")
    }

    /**
     * Translates page to stored page format
     *
     * @param page page
     * @param translatedPage page translated for REST
     * @return stored page
     */
    private fun translateToStoredPage(page: fi.metatavu.edufication.pages.api.persistence.model.Page, translatedPage: Page): Page {
        val result = Page()
        result.id = translatedPage.id
        result.template = resolvePageTemplate(page)
        result.parentPage = translatedPage.parentPage
        result.childPages = translatedPage.childPages
        result.status = translatedPage.status
        result.private = translatedPage.private
        result.path = translatedPage.path
        result.language = translatedPage.language
        result.uri = translatedPage.uri
        result.contentBlocks = translatedPage.contentBlocks
        result.creatorId = translatedPage.creatorId
        result.lastModifierId = translatedPage.lastModifierId
        result.createdAt = translatedPage.createdAt
        result.modifiedAt = translatedPage.modifiedAt
        return result
    }

    /**
     * Resolves template for a page.
     *
     * @param page page
     * @return  template for a page.
     */
    private fun resolvePageTemplate(page: fi.metatavu.edufication.pages.api.persistence.model.Page): PageTemplate {
        val template = page.template
        if (template == null || template == PageTemplate.INHERIT) {
            val parentPage = page.parent ?: return PageTemplate.EDUFICATION
            return resolvePageTemplate(parentPage)
        }

        return template
    }

    /**
     * Uploads object into the storage
     *
     * @param key object key
     * @param contentType content type of object to be uploaded
     * @param contentLength content length
     * @param data object data
     * @return uploaded object
     */
    private fun uploadObject(key: String, contentType: String, contentLength: Long, data: InputStream): URL {
        val objectMeta = ObjectMetadata()
        objectMeta.contentType = contentType

        try {
            try {
                objectMeta.contentLength = contentLength
                val obj = PutObjectRequest(bucket, key, data, objectMeta).withCannedAcl(CannedAccessControlList.PublicRead)
                client.putObject(obj)
                return client.getUrl(bucket, key)
            } catch (e: SdkClientException) {
                throw FileStorageException(e)
            }
        } catch (e: IOException) {
            throw FileStorageException(e)
        }
    }

    /**
     * Returns full path to object or null if object does not exist
     *
     * @param key object key
     * @return uploaded object
     */
    private fun getObjectFullPath(key: String): URL? {
        return try {
            client.getUrl(bucket, key)
        } catch (e: AmazonS3Exception) {
            null
        }
    }

    /**
     * Deletes a stored file from S3
     *
     * @param key key to delete from s3
     */
    private fun deleteObject(key: String) {
        try {
            val s3Object = client.getObject(bucket, key)
            if (s3Object != null) {
                client.deleteObject(bucket, key)
            }
        } catch (e: Exception) {
            throw FileStorageException(e)
        }
    }

}