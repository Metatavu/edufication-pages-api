package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.persistence.model.Page
import fi.metatavu.edufication.pages.api.persistence.model.Page_
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for pages
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class PageDAO: AbstractDAO<Page>() {

    /**
     * Creates a new Page
     *
     * @param id id
     * @param status page status
     * @param path page path
     * @param creatorId creator id
     * @param private page private
     * @param language page language
     * @return created page
     */
    fun create(
        id: UUID,
        status: PageStatus,
        path: String,
        creatorId: UUID,
        private: Boolean,
        language: String
    ): Page {
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
     * Lists all pages with given filters
     *
     * @param path path
     * @return list of pages
     */
    fun list(path: String?): List<Page> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Page::class.java)
        val root = criteria.from(Page::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        if (path != null) {
            restrictions.add(criteriaBuilder.like(root.get(Page_.path), "%$path%" ))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates page path
     *
     * @param page page to update
     * @param page new page path
     * @param modifierId modifier id
     * @return updated page
     */
    fun updatePath(page: Page, path: String?, modifierId: UUID): Page {
        page.path = path
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates page private status
     *
     * @param page page to update
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
     * Updates page language
     *
     * @param page page to update
     * @param language page language
     * @param modifierId modifier id
     * @return updated page
     */
    fun updateLanguage(page: Page, language: String, modifierId: UUID): Page {
        page.language = language
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates page status
     *
     * @param page page to update
     * @param status new page status
     * @param modifierId modifier id
     * @return updated page
     */
    fun updateStatus(page: Page, status: PageStatus?, modifierId: UUID): Page {
        page.status = status
        page.lastModifierId = modifierId
        return persist(page)
    }
}
