package fi.metatavu.edufication.pages.api.controller

import com.amazonaws.services.s3.model.ObjectMetadata
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import fi.metatavu.edufication.pages.api.files.FileMeta
import fi.metatavu.edufication.pages.api.files.InputFile
import fi.metatavu.edufication.pages.api.files.storage.S3FileStorageProvider
import fi.metatavu.edufication.pages.api.model.Page
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
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
     *
     * @return created counter frame
     */
    fun storeJsonPage(page: Page): ObjectMetadata {
        val file = getJsonStringPageFile(page)
        return s3StorageProvider.uploadObject(page.path + "page.json", "application/json", "", file)
    }

    private fun getJsonStringPageFile(page: Page): File {
        val file = File.createTempFile("s3", ".json")
        file.deleteOnExit()
        val writer = OutputStreamWriter(FileOutputStream(file))
        val moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Page> = moshi.adapter(Page::class.java)

        val json: String = jsonAdapter.toJson(page)

        writer.write(json)
        writer.close()

        return file
    }
}