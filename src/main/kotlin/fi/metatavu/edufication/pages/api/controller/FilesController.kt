package fi.metatavu.edufication.pages.api.controller

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.edufication.pages.api.controller.adapters.OffsetDateTimeAdapter
import fi.metatavu.edufication.pages.api.controller.adapters.UUIDJsonAdapter
import fi.metatavu.edufication.pages.api.files.storage.S3FileStorageProvider
import fi.metatavu.edufication.pages.api.persistence.model.Page
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for files
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class FilesController {

    @Inject
    private lateinit var s3StorageProvider: S3FileStorageProvider

    /**
     * Stores a page into s3
     *
     * @param page page to store
     * @return object url
     */
    fun storeJsonPage(page: Page): URL {
        val file = getTempPageFile(page)
        return s3StorageProvider.uploadObject("${page.language!!}/${page.path!!}.json", "application/json; charset=utf-8", "", file)
    }

    /**
     * Removes a stored json page from s3
     *
     * @param path path of page to remove
     * @param language language of page to remove
     */
    fun removeJsonPage(path: String, language: String) {
        return s3StorageProvider.delete("${language}/${path}.json")
    }

    /**
     * Returns a stored file Url
     *
     * @param path page path to find
     * @param language page language to find
     * @return Page url or null if not found
     */
    fun getPageUrl(path: String, language: String): URL? {
        return s3StorageProvider.getObjectFullPath("${language}/${path}.json")
    }

    /**
     * Gets temporary page file
     *
     * @param page Page to get temporary file for
     * @return Temporary file
     */
    private fun getTempPageFile(page: Page): File {
        val file = File.createTempFile("pending-upload", ".json")
        file.deleteOnExit()
        val writer = OutputStreamWriter(FileOutputStream(file), "UTF-8")
        val moshi = Moshi.Builder()
            .add(UUIDJsonAdapter())
            .add(OffsetDateTimeAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter: JsonAdapter<Page> = moshi.adapter(Page::class.java)

        val json: String = jsonAdapter.toJson(page)
        writer.write(json)
        writer.close()

        return file
    }
}
