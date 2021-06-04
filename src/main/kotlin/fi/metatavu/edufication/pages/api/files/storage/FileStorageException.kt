package fi.metatavu.edufication.pages.api.files.storage

/**
 * Exception for file storage operation failures
 *
 * @author Antti Leppä
 */
open class FileStorageException: Exception {

    /**
     * Constructor
     *
     * @param reason exception reason
     */
    constructor(reason: String?) : super(reason) {}

    /**
     * Constructor
     *
     * @param cause exception cause
     */
    constructor(cause: Throwable?) : super(cause) {}

}
