package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.persistence.model.Page
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PageDAO: AbstractDAO<Page>() {

    /**
     * Creates a new Page
     *
     * @param id id
     * @param status status
     * @param path path
     * @param uri uri
     * @param creatorId creatorId
     * @return created VisitorSessionVariable
     */
    fun create(id: UUID, status: PageStatus, path: String, uri: String?, creatorId: UUID): Page {
        val result = Page()
        result.id = id
        result.path = path
        result.uri = uri
        result.creatorId = creatorId
        result.lastModifierId = creatorId
        return persist(result)
    }

    /**
     * Updates Page
     *
     * @param page Page to update
     *
     * @return updated page
     */
    fun updatePage(page: Page): Page {
        return persist(page)
    }
}