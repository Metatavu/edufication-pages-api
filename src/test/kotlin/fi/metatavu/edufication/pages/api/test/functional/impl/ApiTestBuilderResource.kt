package fi.metatavu.edufication.pages.api.test.functional.impl

import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.client.infrastructure.ClientException
import fi.metatavu.edufication.pages.api.client.infrastructure.ServerException
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import org.junit.Assert

/**
 * Abstract base class for API test resource builders
 */
abstract class ApiTestBuilderResource<T, A>(protected val testBuilder: TestBuilder, private val apiClient: ApiClient):fi.metatavu.jaxrs.test.functional.builder.AbstractApiTestBuilderResource<T, A, ApiClient>(testBuilder) {

    /**
     * Empty implementation of clean method since no resources get saved and need cleaning
     */
    override fun clean(t: T) {
    }

    /**
     * Returns API client
     *
     * @return API client
     */
    override fun getApiClient(): ApiClient {
        return apiClient
    }

    /**
     * Asserts that client exception has expected status code
     *
     * @param expectedStatus expected status code
     * @param e client exception
     */
    protected fun assertClientExceptionStatus(expectedStatus: Int, e: ClientException) {
        Assert.assertEquals(expectedStatus, e.statusCode)
    }

    /**
     * Asserts that server exception has expected status code
     *
     * @param expectedStatus expected status code
     * @param e server exception
     */
    protected fun assertServerExceptionStatus(expectedStatus: Int, e: ServerException) {
        Assert.assertEquals(expectedStatus, e.statusCode)
    }
}