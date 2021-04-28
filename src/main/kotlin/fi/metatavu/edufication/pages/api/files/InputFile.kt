package fi.metatavu.edufication.pages.api.files

import java.io.InputStream

/**
 * Class representing a file uploaded into the system but not yet persisted into the database
 *
 * Constructor
 *
 * @param folder folder the file is stored in
 * @param meta file meta
 * @param data file data
 * @author Antti Leppä
 */
open class InputFile (

        /**
         * Returns folder the file is stored in
         *
         * @return folder the file is stored in
         */
        val folder: String,

        /**
         * Returns meta
         *
         * @return meta
         */
        val meta: FileMeta,

        /**
         * Returns data
         *
         * @return data
         */
        val data: InputStream?) : AutoCloseable {

    @Throws(Exception::class)
    override fun close() {
        data?.close()
    }

}