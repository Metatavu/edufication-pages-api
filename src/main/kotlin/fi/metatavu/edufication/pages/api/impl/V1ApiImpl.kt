package fi.metatavu.edufication.pages.api.impl

import fi.metatavu.edufication.pages.api.controller.FilesController
import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.impl.translate.PageTranslator
import fi.metatavu.edufication.pages.api.model.Page
import fi.metatavu.edufication.pages.api.spec.V1Api
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

@RequestScoped
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    private lateinit var pagesController: PagesController

    @Inject
    private lateinit var filesController: FilesController

    @Inject
    private lateinit var pageTranslator: PageTranslator

    @Transactional
    override fun createPage(page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val createdPage = pagesController.create(
            status = page.status,
            path = page.path,
            creatorId = userId,
            contentBlocks = page.contentBlocks
        )

        filesController.storeJsonPage(page)
        val translated = pageTranslator.translate(createdPage)

        return createOk(translated)
    }

    @Transactional
    override fun deletePage(pageId: UUID): Response {
        val page = pagesController.findPage(pageId) ?: return createNotFound()

        pagesController.deletePage(page)
        filesController.removeJsonPage(page.path.toString())
        return createNoContent()
    }

    override fun findPage(pageId: UUID): Response {
        val foundPage = pagesController.findPage(pageId) ?: return createNotFound(PAGE_NOT_FOUND_MESSAGE)

        return createOk(pageTranslator.translate(foundPage))
    }

    override fun listPages(path: String?): Response {
        val pages = pagesController.listPages(path)

        return createOk(pageTranslator.translate(pages))
    }

    @Transactional
    override fun updatePage(pageId: UUID, page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val updatedPage = pagesController.update(
            pageId = pageId,
            status = page.status,
            path = page.path,
            modifierId = userId,
            contentBlocks = page.contentBlocks
        )

        return if (updatedPage == null) {
            createInternalServerError(PAGE_UPDATE_FAILED)
        } else {
            val translated = pageTranslator.translate(updatedPage)
            filesController.storeJsonPage(page)
            createOk(translated)
        }
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
        const val PAGE_NOT_FOUND_MESSAGE = "Page not found!"
        const val PAGE_UPDATE_FAILED = "Page update failed!"
    }
}