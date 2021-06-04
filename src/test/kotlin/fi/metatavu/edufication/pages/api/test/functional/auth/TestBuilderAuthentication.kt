package fi.metatavu.edufication.pages.api.test.functional.auth

import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.edufication.pages.api.test.functional.impl.LanguageTestBuilderResource
import fi.metatavu.edufication.pages.api.test.functional.impl.PagesTestBuilderResource
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication


/**
 * Test builder authentication
 *
 * @author Antti Leppä
 * @author Jari Nykänen
 *
 * Constructor
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(
    private val testBuilder: TestBuilder,
    accessTokenProvider: AccessTokenProvider
): AuthorizedTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider

    var pages: PagesTestBuilderResource = PagesTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    var languages: LanguageTestBuilderResource = LanguageTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())

    /**
     * Creates a API client
     *
     * @param accessToken access token
     * @return API client
     */
    override fun createClient(accessToken: String): ApiClient {
        val result = ApiClient(testBuilder.settings.apiBasePath)
        ApiClient.accessToken = accessToken
        return result
    }

}