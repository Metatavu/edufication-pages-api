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
 * @author Jari Nykänen
 */
class LanguageTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Language, ApiClient?>(testBuilder, apiClient) {

    override fun clean(language: Language) {
        return api.deleteLanguage(language.id!!)
    }

    override fun getApi(): LanguagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return LanguagesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Lists languages
     *
     * @return list of languages
     */
    fun listLanguage(): Array<Language> {
        return api.listLanguages()
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
    fun deleteLanguage(languageId: UUID) {
        api.deleteLanguage(languageId = languageId)
        removeCloseable{ closable: Any ->
            if (closable !is Language) {
                return@removeCloseable false
            }

            val closeableLanguage: Language = closable
            closeableLanguage.id!! == languageId
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

        val createdLanguage = api.createLanguage(language)
        addClosable(createdLanguage)

        return createdLanguage
    }
}
