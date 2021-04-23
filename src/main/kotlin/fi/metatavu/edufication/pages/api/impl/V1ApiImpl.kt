package fi.metatavu.edufication.pages.api.impl

import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.model.Page
import fi.metatavu.edufication.pages.api.spec.V1Api
import org.hibernate.annotations.NotFound
import org.jboss.resteasy.util.NoContent
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@RequestScoped
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    private lateinit var pagesController: PagesController

    override fun createPage(page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val createdPage = pagesController.create(
            status = page.status,
            path = page.path,
            userId,
            page.contentBlocks
        )

        return createOk(createdPage)
    }

    override fun deletePage(pageId: UUID): Response {
        val page = pagesController.findPage(pageId) ?: return createNotFound()

        pagesController.deletePage(page.id)
        return createNoContent()
    }

    override fun findPage(pageId: UUID): Response {
        val foundPage = pagesController.findPage(pageId) ?: return createNotFound(PAGE_NOT_FOUND_MESSAGE)

        return createOk(foundPage)
    }

    override fun listPages(path: String?): Response {
        return createOk(pagesController.listPages(path))
    }

    override fun updatePage(pageId: UUID, page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val updatedPage = pagesController.update(
            pageId = pageId,
            status = page.status,
            path = page.path,
            modifierId = userId,
            contentBlocks = page.contentBlocks
        )

        return createOk(updatedPage)
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
        const val PAGE_NOT_FOUND_MESSAGE = "Page not found!"
    }
}