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

    override fun createPage(page: Page?): Response {
        val pageToCreate = page ?: return createBadRequest("Missing page payload!")

        val userId = loggerUserId ?: return createUnauthorized("No valid user!")

        val createdPage = pagesController.create(
            pageToCreate.status,
            pageToCreate.path,
            pageToCreate.uri,
            userId,
            pageToCreate.contentBlocks
        )

        return createOk(createdPage)
    }

    override fun deletePage(pageId: UUID?): Response {
        val pageIdToFind = pageId ?: return createBadRequest("PageId missing!")
        val page = pagesController.findPage(pageIdToFind) ?: return createNotFound("Page not found")

        pagesController.deletePage(page.id)
        return createNoContent()
    }

    override fun findPage(pageId: UUID?): Response {
        val pageIdToFind = pageId ?: return createBadRequest("PageId missing!")
        val foundPage = pagesController.findPage(pageIdToFind) ?: return createNotFound("Page not found")

        return createOk(foundPage)
    }

    override fun listPages(path: String?): Response {
        return createOk(pagesController.listPages(path))
    }

    override fun updatePage(pageId: UUID?, page: Page?): Response {
        val pageToUpdate = page ?: return createBadRequest("Missing page")
        val pageToUpdateId = pageId ?: return createBadRequest("Missing pageId")

        val userId = loggerUserId ?: return createUnauthorized("No valid user!")

        val updatedPage = pagesController.update(
            pageToUpdateId,
            pageToUpdate.status,
            pageToUpdate.path,
            pageToUpdate.uri,
            userId,
            pageToUpdate.contentBlocks
        )

        return createOk(updatedPage)
    }

    override fun ping(): Response {
        return createOk("pong")
    }
}