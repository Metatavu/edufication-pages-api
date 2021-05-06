package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.persistence.model.Language
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LanguageDAO: AbstractDAO<Language>() {

    /**
     * Creates a new quiz
     *
     * @param id id
     * @param name name
     * @return created Quiz
     */
    fun create(id: UUID, name: String): Language {
        val result = Language()
        result.id = id
        result.name = name
        return persist(result)
    }
}
