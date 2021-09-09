package fi.metatavu.edufication.pages.api.pages

import com.fasterxml.jackson.databind.ObjectMapper
import fi.metatavu.edufication.pages.api.model.NavigationItem
import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.model.PageTemplate
import fi.metatavu.edufication.pages.api.persistence.dao.ContentBlockDAO
import fi.metatavu.edufication.pages.api.persistence.dao.ContentBlockNavigationItemDAO
import fi.metatavu.edufication.pages.api.persistence.dao.PageDAO
import fi.metatavu.edufication.pages.api.persistence.dao.QuizDAO
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Page
import org.apache.commons.lang3.StringUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller class for pages
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class PagesController {

    @Inject
    lateinit var pageDAO: PageDAO

    @Inject
    lateinit var contentBlockDAO: ContentBlockDAO

    @Inject
    lateinit var quizDAO: QuizDAO

    @Inject
    lateinit var contentBlockNavigationItemDAO: ContentBlockNavigationItemDAO

    /**
     * Creates a new Page
     *
     * @param title title
     * @param template template
     * @param status Page status
     * @param path Path to page
     * @param parent Page parent
     * @param creatorId Creator Id
     * @param contentBlocks Content Blocks for page
     * @param private page private
     * @param language page language
     * @return created counter frame
     */
    fun create (
        title: String?,
        template: PageTemplate,
        status: PageStatus,
        path: String,
        parent: Page?,
        creatorId: UUID,
        contentBlocks: List<fi.metatavu.edufication.pages.api.model.ContentBlock>,
        private: Boolean,
        language: String
    ): Page {
        val createdPage = pageDAO.create(
            id = UUID.randomUUID(),
            title = title,
            template = template,
            status = status,
            path = path,
            parent = parent,
            creatorId = creatorId,
            private = private,
            language = language
        )

        contentBlocks.map {
            val contentBlock = contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = createdPage,
                layout = it.layout!!,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = getDataAsString(it.link),
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

            setContentBlockNavigationItems(
                contentBlock = contentBlock,
                items = it.navigationItems
            )
        }

        return createdPage
    }

    /**
     * Finds a page from the database
     *
     * @param pageId page id to find
     * @return page or null if not found
     */
    fun findPage(pageId: UUID): Page? {
        return pageDAO.findById(pageId)
    }

    /**
     * Finds a page by path
     *
     * @param language language
     * @param path path
     * @return page or null if not found
     */
    fun findPageByLanguageAndPath(language: String, path: String): Page? {
        return pageDAO.findByLanguageAndPath(
            language = language,
            path = path
        )
    }

    /**
     * List pages with optional path filter
     *
     * @param parentPage parent page
     * @return list of pages
     */
    fun list(parentPage: Page?): List<Page> {
        return pageDAO.list(parentPage)
    }

    /**
     * Lists child pages
     *
     * @param parent parent page
     * @return list of child pages
     */
    fun listChildPages(parent: Page): List<Page> {
        return pageDAO.listByParent(
            parent = parent
        )
    }

    /**
     * Resolves parent path for given path
     *
     * @param path path
     * @return parent path for given path
     */
    fun getParentPath(path: String): String? {
        val slugs = StringUtils.removeEnd(path, "/").split('/')
        val parentPath = slugs.dropLast(1).joinToString("/")

        return parentPath.ifBlank { null }
    }

    /**
     * Updates page
     *
     * @param page page to update
     * @param title title
     * @param template template
     * @param status status
     * @param path page path
     * @param modifierId modifierId
     * @param contentBlocks contentBlocks
     * @param private page private
     * @param language page language
     * @return Updated Page
     */
    fun update(
      page: Page,
      title: String?,
      template: PageTemplate,
      status: PageStatus,
      path: String,
      modifierId: UUID,
      contentBlocks: List<fi.metatavu.edufication.pages.api.model.ContentBlock>,
      private: Boolean,
      parent: Page?,
      language: String
    ): Page {
        val result = pageDAO.updatePath(page, path, modifierId)
        pageDAO.updateTemplate(result, template, modifierId)
        pageDAO.updateTitle(result, title, modifierId)
        pageDAO.updateLanguage(result, language, modifierId)
        pageDAO.updatePrivate(result, private, modifierId)
        pageDAO.updateParent(result, parent, modifierId)
        pageDAO.updateStatus(result, status, modifierId)

        contentBlockDAO.listByPage(result).forEach(this::deleteContentBlock)

        contentBlocks.map {
            val contentBlock = contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = result,
                layout = it.layout!!,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = getDataAsString(it.link),
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

            setContentBlockNavigationItems(
                contentBlock = contentBlock,
                items = it.navigationItems
            )
        }

        return result
    }

    /**
     * Deletes a page from the database
     *
     * @param page page to delete
     */
    fun deletePage(page: Page) {
        contentBlockDAO.listByPage(page).forEach(this::deleteContentBlock)
        pageDAO.delete(page)
    }

    /**
     * Returns all content blocks that belong to the given page
     *
     * @param page Page to find content for
     * @return List of ContentBlocks
     */
    fun getPageContent(page: Page): List<ContentBlock> {
        return contentBlockDAO.listByPage(page)
    }

    /**
     * Deletes a content block
     *
     * @param contentBlock content block
     */
    private fun deleteContentBlock(contentBlock: ContentBlock) {
        contentBlockNavigationItemDAO.listByContentBlock(contentBlock).forEach(contentBlockNavigationItemDAO::delete)
        contentBlockDAO.delete(contentBlock)
    }

    /**
     * Sets content block navigation items
     *
     * @param contentBlock content block
     * @param items navigation items
     */
    private fun setContentBlockNavigationItems(contentBlock: ContentBlock, items: List<NavigationItem>) {
        val existingItems = contentBlockNavigationItemDAO.listByContentBlock(contentBlock).toMutableList()

        items.forEachIndexed { index, item ->
            val existingItem = existingItems.find { it.id == item.id }
            if (existingItem == null) {
                contentBlockNavigationItemDAO.create(
                    id = UUID.randomUUID(),
                    orderNumber = index,
                    url = item.url,
                    imageUrl = item.imageUrl,
                    title = item.title,
                    contentBlock = contentBlock
                )
            } else {
                contentBlockNavigationItemDAO.updateUrl(
                    contentBlockNavigationItem = existingItem,
                    url = item.url
                )

                contentBlockNavigationItemDAO.updateImageUrl(
                    contentBlockNavigationItem = existingItem,
                    imageUrl = item.imageUrl
                )

                contentBlockNavigationItemDAO.updateTitle(
                    contentBlockNavigationItem = existingItem,
                    title = item.title
                )

                contentBlockNavigationItemDAO.updateOrderNumber(
                    contentBlockNavigationItem = existingItem,
                    orderNumber = index
                )

                existingItems.remove(existingItem)
            }
        }

        existingItems.forEach(contentBlockNavigationItemDAO::delete)
    }

    /**
     * Serializes the object into JSON string
     *
     * @param data object
     * @return JSON string
     */
    private fun <T> getDataAsString(data: T?): String? {
        data ?: return null

        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(data)
    }
}
