package fi.metatavu.edufication.pages.api.test.functional.impl

import fi.metatavu.edufication.pages.api.client.apis.LanguagesApi
import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.client.models.Language
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import java.util.*

/**
 * Resource for testings Pages API
 *
 * @author Antti Leinonen
 */
class LanguageTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
):ApiTestBuilderResource<Language, ApiClient?>(testBuilder, apiClient) {

    override fun getApi(): LanguagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return LanguagesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Finds page from the API
     *
     * @param languageId page id
     * @return Found Language or null if not found
     */
    fun findLanguage(languageId: UUID): Language {
        return api.findLanguage(languageId = languageId)
    }

    /**
     * Deletes a Language from the API
     *
     * @param languageId Language id
     */
    fun deleteLanguage(languageId: UUID?) {
        if (languageId != null) {
            api.deleteLanguage(languageId = languageId)
        }
    }

    /**
     * Creates a Language
     *
     * @return Created Language
     */
    fun createLanguage(): Language {
        val language = Language(
            name = "fi"
        )

        return api.createLanguage(language)
    }
}
