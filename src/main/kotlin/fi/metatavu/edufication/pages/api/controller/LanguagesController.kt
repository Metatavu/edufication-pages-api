package fi.metatavu.edufication.pages.api.controller

import fi.metatavu.edufication.pages.api.persistence.dao.LanguageDAO
import fi.metatavu.edufication.pages.api.persistence.model.Language
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for languages
 */
@ApplicationScoped
class LanguagesController {

    @Inject
    private lateinit var languageDAO: LanguageDAO

    /**
     * Creates a new language
     *
     * @param name Language name
     *
     * @return Created language
     */
    fun create (name: String): Language {
        return languageDAO.create(id = UUID.randomUUID(), name = name)
    }

    /**
     * Finds a language from the database
     *
     * @param languageId language id to find
     *
     * @return Language or null if not found
     */
    fun findLanguage(languageId: UUID): Language? {
        return languageDAO.findById(languageId)
    }

    /**
     * List Languages
     *
     * @return list of languages
     */
    fun listLanguages(): List<Language> {
        return languageDAO.listAll()
    }


    /**
     * Deletes a language from the database
     *
     * @param language language to delete
     */
    fun deleteLanguage(language: Language) {
        return languageDAO.delete(language)
    }
}
