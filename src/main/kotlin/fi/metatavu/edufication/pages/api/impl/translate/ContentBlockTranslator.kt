package fi.metatavu.edufication.pages.api.impl.translate

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.edufication.pages.api.model.Link
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Quiz
import javax.enterprise.context.ApplicationScoped

/**
 * Translator class for content blocks
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ContentBlockTranslator: AbstractTranslator<ContentBlock, fi.metatavu.edufication.pages.api.model.ContentBlock>() {

    override fun translate(entity: ContentBlock): fi.metatavu.edufication.pages.api.model.ContentBlock {
        val translated = fi.metatavu.edufication.pages.api.model.ContentBlock()
        translated.layout = entity.layout
        translated.link = getLink(entity.link)
        translated.media = entity.media
        translated.textContent = entity.textContent
        translated.title = entity.title
        translated.orderInPage = entity.orderInPage
        translated.quiz = entity.quiz?.let { translateQuiz(it) }

        return translated
    }

    /**
     * Translates quiz
     *
     * @param quiz quiz
     * @return Api model quiz
     */
    private fun translateQuiz(quiz: Quiz): fi.metatavu.edufication.pages.api.model.Quiz {
        val result = fi.metatavu.edufication.pages.api.model.Quiz()
        result.text = quiz.text
        result.correctIndex = quiz.correctIndex
        result.options = parseOptions(quiz.options)
        return result
    }

    /**
     * Parses event triggers string as list of event triggers objects
     *
     * @param options event triggers string
     * @return list of event triggers objects
     */
    private fun parseOptions(options: String?): List<String?>? {
        options ?: return listOf()
        return ObjectMapper().readValue(options, object : TypeReference<List<String?>?>() {})
    }

    /**
     * Deserialize entity link string to spec Link object
     *
     * @param link entity link object string
     * @return deserialized Link object
     */
    private fun getLink(link: String?): Link? {
        link ?: return null

        try {
            return ObjectMapper().readValue(link, object : TypeReference<Link>() {})
        } catch(Exception e) {
            return Link(
                title = "",
                url: link
            )
        }
    }
}
