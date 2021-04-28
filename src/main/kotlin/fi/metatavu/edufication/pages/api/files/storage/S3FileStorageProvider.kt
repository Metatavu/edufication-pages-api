package fi.metatavu.edufication.pages.api.files.storage

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.amazonaws.util.IOUtils
import fi.metatavu.edufication.pages.api.files.InputFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * File storage provider for storing files in S3.
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class S3FileStorageProvider {

    private var region: String? = null
    private var bucket: String? = null
    private var prefix: String? = null

    @Throws(FileStorageException::class)
    fun init() {
        region = System.getenv("S3_FILE_STORAGE_REGION")
        bucket = System.getenv("S3_FILE_STORAGE_BUCKET")
        prefix = System.getenv("S3_FILE_STORAGE_PREFIX")
        if (region.isNullOrBlank()) {
            throw FileStorageException("S3_FILE_STORAGE_REGION is not set")
        }
        if (bucket.isNullOrBlank()) {
            throw FileStorageException("S3_FILE_STORAGE_BUCKET is not set")
        }
        if (prefix.isNullOrBlank()) {
            throw FileStorageException("S3_FILE_STORAGE_PREFIX is not set")
        }
        val client: AmazonS3 = client
        if (!client.doesBucketExistV2(bucket)) {
            throw FileStorageException(String.format("bucket '%s' does not exist", bucket))
        }
    }

    /**
     * Returns initialized S3 client
     *
     * @return initialized S3 client
     */
    private val client: AmazonS3 get() = AmazonS3ClientBuilder.standard().withRegion(region).build()

    /**
     * Uploads object into the storage
     *
     * @param key object key
     * @param contentType content type of object to be uploaded
     * @param filename object filename
     * @param tempFile object data in temp file
     * @return uploaded object
     */
    fun uploadObject(key: String, contentType: String, filename: String, tempFile: File): ObjectMetadata {
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

                return objectMeta
            } catch (e: SdkClientException) {
                throw FileStorageException(e)
            }
        } catch (e: IOException) {
            throw FileStorageException(e)
        }
    }

    /**
     * Converts stored file id into S3 key
     *
     * @param storedFileId stored file id
     * @return S3 key
     */
    private fun getKey(storedFileId: String): String {
        return URLDecoder.decode(storedFileId, StandardCharsets.UTF_8)
    }

    /**
     * Converts S3 key into stored file id
     *
     * @param key S3 key
     * @return stored file id
     */
    private fun getStoredFileId(key: String): String {
        return URLEncoder.encode(key, StandardCharsets.UTF_8)
    }


    companion object {
        const val X_FILE_NAME = "x-file-name"
    }
}