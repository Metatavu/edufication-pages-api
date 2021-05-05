package fi.metatavu.edufication.pages.api.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.persistence.dao.ContentBlockDAO
import fi.metatavu.edufication.pages.api.persistence.dao.PageDAO
import fi.metatavu.edufication.pages.api.persistence.dao.QuizDAO
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Page
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for pages
 */
@ApplicationScoped
class PagesController {

    @Inject
    private lateinit var pageDAO: PageDAO

    @Inject
    private lateinit var contentBlockDAO: ContentBlockDAO

    @Inject
    private lateinit var quizDAO: QuizDAO

    /**
     * Creates a new Page
     *
     * @param status Page status
     * @param path Path to page
     * @param creatorId Creator Id
     * @param contentBlocks Content Blocks for page
     * @param private page private
     *
     * @return created counter frame
     */
    fun create (status: PageStatus, path: String, creatorId: UUID, contentBlocks: List<fi.metatavu.edufication.pages.api.model.ContentBlock>, private: Boolean): Page {
        val createdPage = pageDAO.create(id = UUID.randomUUID(), status = status, path = path, creatorId = creatorId, private = private)

        contentBlocks.map {
            val contentBlock = contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = createdPage,
                layout = it.layout!!,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = it.link,
                orderInPage = it.orderInPage
            )
            if (it.quiz != null) {
                val quiz = quizDAO.create(
                    UUID.randomUUID(),
                    contentBlock = contentBlock,
                    text = it.quiz.text,
                    options = getDataAsString(it.quiz.options.toTypedArray()),
                    correctIndex = it.quiz.correctIndex
                )
                contentBlockDAO.updateQuiz(contentBlock, quiz)
            }
        }

        return createdPage
    }

    /**
     * Finds a page from the database
     *
     * @param pageId page id to find
     *
     * @return Page or null if not found
     */
    fun findPage(pageId: UUID): Page? {
        return pageDAO.findById(pageId)
    }

    /**
     * List pages with optional path filter
     *
     * @param path path that must be contained in page path
     *
     * @return list of pages
     */
    fun listPages(path: String?): List<Page> {
        return when (path != null) {
            true -> listPagesByPath(path)
            else -> listAll()
        }
    }

    /**
     * Updates page
     *
     * @param pageId Page id
     * @param status status
     * @param path page path
     * @param modifierId modifierId
     * @param contentBlocks contentBlocks
     * @param private page private
     *
     * @return Updated Page
     */
    fun update(pageId: UUID, status: PageStatus, path: String, modifierId: UUID, contentBlocks: List<fi.metatavu.edufication.pages.api.model.ContentBlock>, private: Boolean): Page? {
        val pageToUpdate = pageDAO.findById(pageId) ?: return null

        pageDAO.updatePath(pageToUpdate, path, modifierId)
        pageDAO.updatePrivate(pageToUpdate, private, modifierId)
        val result = pageDAO.updateStatus(pageToUpdate, status, modifierId)

        contentBlockDAO.listByPage(pageToUpdate).forEach {
            contentBlockDAO.delete(it)
        }

        contentBlocks.map {
            val contentBlock = contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = pageToUpdate,
                layout = it.layout!!,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = it.link,
                orderInPage = it.orderInPage
            )
            if(it.quiz != null) {
                val quiz = quizDAO.create(
                    UUID.randomUUID(),
                    contentBlock = contentBlock,
                    text = it.quiz.text,
                    options = getDataAsString(it.quiz.options.toTypedArray()),
                    correctIndex = it.quiz.correctIndex
                )
                contentBlockDAO.updateQuiz(contentBlock, quiz)
            }
        }

        return result
    }

    /**
     * Parses event triggers string as list of event triggers objects
     *
     * @param options event triggers string
     * @return list of event triggers objects
     */
    fun parseOptions(options: String?): List<String?>? {
        options ?: return listOf()
        val objectMapper = ObjectMapper()

        return objectMapper.readValue(options, object : TypeReference<List<String?>?>() {})
    }

    /**
     * Deletes a page from the database
     *
     * @param page page to delete
     */
    fun deletePage(page: Page) {
        contentBlockDAO.listByPage(page).forEach {
            contentBlockDAO.delete(it)
        }
        return pageDAO.delete(page)
    }

    /**
     * Returns all content blocks that belong to the given page
     *
     * @param page Page to find content for
     *
     * @return List of ContentBlocks
     */
    fun getPageContent(page: Page): List<ContentBlock> {
        return contentBlockDAO.listByPage(page)
    }

    /**
     * Lists pages by path
     *
     * @param path path that must be contained in results path
     *
     * @return List of Pages
     */
    private fun listPagesByPath(path: String): List<Page> {
        return pageDAO.listByPath(path = path)
    }

    /**
     * Lists all pages
     *
     * @return List of Pages
     */
    private fun listAll(): List<Page> {
        return pageDAO.listAll()
    }

    /**
     * Serializes the object into JSON string
     *
     * @param data object
     * @return JSON string
     */
    private fun <T> getDataAsString(data: T): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(data)
    }
}