package fi.metatavu.edufication.pages.api.test.functional.settings
import java.util.*

/**
 * Settings implementation for test builder
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 */
class ApiTestSettings {

    /**
     * Returns API service base path
     */
    val apiBasePath: String
        get() = "http://localhost:8081"

    val teroId: UUID
        get() = UUID.fromString("b8a6cedd-dea3-4e59-a60b-29ac449246c2")

    val onniId: UUID
        get() = UUID.fromString("2d42e574-2670-4855-8169-da642e0ef067")

    val teroNotRegistered: UUID
        get() = UUID.fromString("088b5e6d-adc9-4e8e-8d7f-540f1f785371")

}