package fi.metatavu.edufication.pages.api.controller

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fi.metatavu.edufication.pages.api.controller.adapters.OffsetDateTimeAdapter
import fi.metatavu.edufication.pages.api.controller.adapters.UUIDJsonAdapter
import fi.metatavu.edufication.pages.api.files.storage.S3FileStorageProvider
import fi.metatavu.edufication.pages.api.model.Page
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for files
 */
@ApplicationScoped
class FilesController {

    @Inject
    private lateinit var s3StorageProvider: S3FileStorageProvider

    /**
     * Stores a page into s3
     *
     * @param page Page to store
     * @return object url
     */
    fun storeJsonPage(page: Page): URL {
        val file = getTempPageFile(page)
        return s3StorageProvider.uploadObject(page.path + ".json", "application/json; charset=utf-8", "", file)
    }

    /**
     * Removes a stored json page from s3
     *
     * @param path page path to remove
     */
    fun removeJsonPage(path: String) {
        return s3StorageProvider.delete("$path.json")
    }

    /**
     * Returns a stored file Url
     *
     * @param path page path to find url for
     * @return Page url or null if not found
     */
    fun getPageUrl(path: String): URL? {
        return s3StorageProvider.getObjectFullPath("$path.json")
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
        val moshi = Moshi.Builder().add(UUIDJsonAdapter()).add(OffsetDateTimeAdapter()).build()
        val jsonAdapter: JsonAdapter<Page> = moshi.adapter(Page::class.java)

        val json: String = jsonAdapter.toJson(page)
        writer.write(json)
        writer.close()

        return file
    }
}
