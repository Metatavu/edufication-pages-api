package fi.metatavu.edufication.pages.api.controller

import fi.metatavu.edufication.pages.api.impl.translate.ContentBlockTranslator
import fi.metatavu.edufication.pages.api.impl.translate.PageTranslator
import fi.metatavu.edufication.pages.api.model.ContentBlock
import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.persistence.dao.ContentBlockDAO
import fi.metatavu.edufication.pages.api.persistence.dao.PageDAO
import fi.metatavu.edufication.pages.api.model.Page
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

/**
 * Controller class for pages
 */
@ApplicationScoped
class PagesController {

    @Inject
    private lateinit var pageDAO: PageDAO

    @Inject
    private lateinit var contentBlockDAO: ContentBlockDAO

    /**
     * Creates a new Page
     *
     * @param status Page status
     * @param path Path to page
     * @param uri Full uri path to page
     * @param creatorId
     * @param contentBlocks
     *
     * @return created counter frame
     */
    @Transactional
    fun create (status: PageStatus, path: String, uri: String?, creatorId: UUID, contentBlocks: List<ContentBlock>): Page {
        val createdPage = pageDAO.create(id = UUID.randomUUID(), status = status, path = path, uri = uri, creatorId = creatorId)

        val createdContentBlocks = contentBlocks.map {
            contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = createdPage,
                layout = it.layout,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = it.link
            )
        }

        val page = PageTranslator().translate(createdPage)
        val content = ContentBlockTranslator().translate(createdContentBlocks)
        page.contentBlocks = content

        return page
    }

    /**
     * Finds a page from the database
     *
     * @param pageId page id to find
     *
     * @return Page or null if not found
     */
    fun findPage(pageId: UUID): Page? {
        val page = pageDAO.findById(pageId) ?: return null
        val translated = PageTranslator().translate(page)
        translated.contentBlocks = getPageContent(page)

        return translated
    }

    /**
     * Lists pages and filters with path if given
     *
     * @param path path that must be contained in results path
     *
     * @return List of Pages
     */
    fun listPages(path: String?): List<Page> {
        var pages = pageDAO.listAll()

        if (path != null) {
            pages = pages.filter { it.path?.contains(path) == true }
        }

        return PageTranslator().translate(pages)
    }

    /**
     * Updates page
     *
     * @param pageId Page id
     * @param status status
     * @param path page path
     * @param uri page uri
     * @param modifierId modifierId
     * @param contentBlocks contentBlocks
     *
     * @return Updated Page
     */
    @Transactional
    fun update(pageId: UUID, status: PageStatus, path: String, uri: String?, modifierId: UUID, contentBlocks: List<ContentBlock>): Page? {
        val pageToUpdate = pageDAO.findById(pageId) ?: return null

        pageToUpdate.status = status
        pageToUpdate.uri = uri
        pageToUpdate.path = path
        pageToUpdate.lastModifierId = modifierId
        val updatedPage = pageDAO.updatePage(pageToUpdate)

        contentBlockDAO.listAll().forEach {
            contentBlockDAO.delete(it)
        }

        val createdContentBlocks = contentBlocks.map {
            contentBlockDAO.create(
                id = UUID.randomUUID(),
                page = pageToUpdate,
                layout = it.layout,
                title = it.title,
                textContent = it.textContent,
                media = it.media,
                link = it.link
            )
        }

        val page = PageTranslator().translate(updatedPage)
        val content = ContentBlockTranslator().translate(createdContentBlocks)
        page.contentBlocks = content

        return page
    }

    /**
     * Deletes a page from the database
     *
     * @param pageId page id to delete
     */
    fun deletePage(pageId: UUID) {
        val pageToDelete = pageDAO.findById(pageId) ?: return

        return pageDAO.delete(pageToDelete)
    }

    /**
     * Returns all content pages that belong to the given page
     *
     * @param page Page to find content for
     *
     * @return List of ContentBlocks
     */
    private fun getPageContent(page: fi.metatavu.edufication.pages.api.persistence.model.Page): List<ContentBlock> {
        val pageContent = contentBlockDAO.listAll(page)
        return ContentBlockTranslator().translate(pageContent)
    }
}