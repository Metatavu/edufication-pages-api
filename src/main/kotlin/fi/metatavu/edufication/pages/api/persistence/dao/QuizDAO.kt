package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Quiz
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class QuizDAO: AbstractDAO<Quiz>() {

    /**
     * Creates a new quiz
     *
     * @param id id
     * @param contentBlock contentBlock
     * @param text layout
     * @param options options
     * @param correctIndex correctIndex
     * @return created Quiz
     */
    fun create(id: UUID, contentBlock: ContentBlock, text: String?, options: String?, correctIndex: Int): Quiz {
        val result = Quiz()
        result.id = id
        result.contentBlock = contentBlock
        result.text = text
        result.options = options
        result.correctIndex = correctIndex
        return persist(result)
    }
}
