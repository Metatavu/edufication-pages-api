package fi.metatavu.edufication.pages.api.impl.translate

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.edufication.pages.api.model.Link
import fi.metatavu.edufication.pages.api.model.NavigationItem
import fi.metatavu.edufication.pages.api.persistence.dao.ContentBlockNavigationItemDAO
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlockNavigationItem
import fi.metatavu.edufication.pages.api.persistence.model.Quiz
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator class for content blocks
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 */
@ApplicationScoped
class ContentBlockTranslator: AbstractTranslator<ContentBlock, fi.metatavu.edufication.pages.api.model.ContentBlock>() {

    @Inject
    lateinit var contentBlockNavigationItemDAO: ContentBlockNavigationItemDAO

    override fun translate(entity: ContentBlock): fi.metatavu.edufication.pages.api.model.ContentBlock {
        val translated = fi.metatavu.edufication.pages.api.model.ContentBlock()
        translated.id = entity.id
        translated.layout = entity.layout
        translated.link = getLink(entity.link)
        translated.media = entity.media
        translated.textContent = entity.textContent
        translated.title = entity.title
        translated.orderInPage = entity.orderInPage
        translated.quiz = entity.quiz?.let { translateQuiz(it) }
        translated.navigationItems = contentBlockNavigationItemDAO.listByContentBlock(entity).map(this::translateNavigationItem)
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
     * Translates single navigation item to REST object
     *
     * @param navigationItem navigation item
     * @return link
     */
    private fun translateNavigationItem(navigationItem: ContentBlockNavigationItem): NavigationItem {
        val result = NavigationItem()
        result.id = navigationItem.id
        result.title = navigationItem.title
        result.url = navigationItem.url
        result.imageUrl = navigationItem.imageUrl
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
     * @param linkString entity link object string
     * @return deserialized Link object
     */
    private fun getLink(linkString: String?): Link? {
        linkString ?: return null

        return try {
            ObjectMapper().readValue(linkString, object : TypeReference<Link>() {})
        } catch (e: Exception) {
            val link = fi.metatavu.edufication.pages.api.model.Link()
            link.title = ""
            link.url = linkString

            link
        }
    }
}
