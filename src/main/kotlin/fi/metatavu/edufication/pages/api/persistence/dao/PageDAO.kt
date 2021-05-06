package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.persistence.model.Page
import fi.metatavu.edufication.pages.api.persistence.model.Page_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

@ApplicationScoped
class PageDAO: AbstractDAO<Page>() {

    /**
     * Creates a new Page
     *
     * @param id id
     * @param status status
     * @param path path
     * @param creatorId creatorId
     * @param private page private
     * @return created VisitorSessionVariable
     */
    fun create(id: UUID, status: PageStatus, path: String, creatorId: UUID, private: Boolean, language: String): Page {
        val result = Page()
        result.id = id
        result.path = path
        result.status = status
        result.private = private
        result.creatorId = creatorId
        result.lastModifierId = creatorId
        result.language = language
        return persist(result)
    }

    /**
     * Lists all pages that match the given path
     *
     * @param path Path
     * @return list of pages
     */
    fun listByPath(path: String): List<Page> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Page::class.java)
        val root = criteria.from(Page::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.like(root.get(Page_.path), "%$path%" ))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates Page
     *
     * @param page Page to update
     * @param page new path
     * @param modifierId modifiers id
     * @return updated page
     */
    fun updatePath(page: Page, path: String?, modifierId: UUID): Page {
        page.path = path
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates Page
     *
     * @param page Page to update
     * @param private new private status
     * @param modifierId modifiers id
     * @return updated page
     */
    fun updatePrivate(page: Page, private: Boolean, modifierId: UUID): Page {
        page.private = private
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates Page
     *
     * @param page Page to update
     * @param language Page language
     * @param modifierId modifiers id
     * @return updated page
     */
    fun updateLanguage(page: Page, language: String, modifierId: UUID): Page {
        page.language = language
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates Page
     *
     * @param page Page to update
     * @param status new status
     * @param modifierId modifiers id
     * @return updated page
     */
    fun updateStatus(page: Page, status: PageStatus?, modifierId: UUID): Page {
        page.status = status
        page.lastModifierId = modifierId
        return persist(page)
    }
}
