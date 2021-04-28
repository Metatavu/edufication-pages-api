package fi.metatavu.edufication.pages.api.files.storage

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
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * File storage provider for storing files in S3.
 *
 * @author Antti Leinonen
 */
@ApplicationScoped
class S3FileStorageProvider {

    @Inject
    @ConfigProperty(name = "s3.endpoint")
    private lateinit var endpoint: String

    @ConfigProperty(name = "s3.region")
    private lateinit var region: String

    @ConfigProperty(name = "s3.bucket")
    private lateinit var bucket: String

    @ConfigProperty(name = "s3.secret")
    private lateinit var secret: String

    @ConfigProperty(name = "s3.access")
    private lateinit var access: String

    private val client: AmazonS3 get() = AmazonS3ClientBuilder
        .standard()
        .withPathStyleAccessEnabled(true)
        .withCredentials(credentials)
        .withEndpointConfiguration(endpointConfig)
        .build()

    private val endpointConfig: AwsClientBuilder.EndpointConfiguration get() =
        AwsClientBuilder.EndpointConfiguration(endpoint, region)

    private val credentials: AWSStaticCredentialsProvider get() =
        AWSStaticCredentialsProvider(BasicAWSCredentials(access, secret))

    /**
     * Uploads object into the storage
     *
     * @param key object key
     * @param contentType content type of object to be uploaded
     * @param filename object filename
     * @param tempFile object data in temp file
     *
     * @return uploaded object
     */
    fun uploadObject(key: String, contentType: String, filename: String, tempFile: File): URL {
        val objectMeta = ObjectMetadata()
        objectMeta.contentType = contentType
        objectMeta.addUserMetadata(X_FILE_NAME, filename)

        try {
            try {
                objectMeta.contentLength = tempFile.length()
                FileInputStream(tempFile).use { fileInputStream ->
                    val obj = PutObjectRequest(bucket, key, fileInputStream, objectMeta).withCannedAcl(CannedAccessControlList.PublicRead)
                    client.putObject(obj)
                }

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
    fun getObjectFullPath(key: String): URL? {
        return try {
            client.getUrl(bucket, key)
        } catch (e: AmazonS3Exception) {
            null
        }
    }

    /**
     * Deletes a stored file
     *
     * @param key key to delete from s3
     */
    fun delete(key: String) {
        try {
            val s3Object = client.getObject(bucket, key)
            if (s3Object != null) {
                client.deleteObject(bucket, key)
            }
        } catch (e: Exception) {
            throw FileStorageException(e)
        }
    }

    companion object {
        const val X_FILE_NAME = "x-file-name"
    }
}
