package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.model.PageTemplate
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
     * @param title title
     * @param template template
     * @param status page status
     * @param path page path
     * @param creatorId creator id
     * @param private page private
     * @param language page language
     * @return created page
     */
    fun create(
        id: UUID,
        title: String?,
        template: PageTemplate,
        status: PageStatus,
        path: String,
        creatorId: UUID,
        private: Boolean,
        parent: Page?,
        language: String
    ): Page {
        val result = Page()
        result.id = id
        result.title = title
        result.template = template
        result.path = path
        result.status = status
        result.private = private
        result.parent = parent
        result.creatorId = creatorId
        result.lastModifierId = creatorId
        result.language = language
        return persist(result)
    }

    /**
     * Lists all pages with given filters
     *
     * @param parentPage parent page
     * @return list of pages
     */
    fun list(parentPage: Page?): List<Page> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Page::class.java)
        val root = criteria.from(Page::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        if (parentPage != null) {
            restrictions.add(criteriaBuilder.equal(root.get(Page_.parent), parentPage ))
        }

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Lists child pages
     *
     * @param parent parent page
     * @return list of child pages
     */
    fun listByParent(parent: Page): List<Page> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Page::class.java)
        val root = criteria.from(Page::class.java)

        criteria.select(root)
        criteria.where(criteriaBuilder.equal(root.get(Page_.parent), parent))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Finds page by given language and path
     *
     * @param language language
     * @param path path
     * @return page or null if not found
     */
    fun findByLanguageAndPath(language: String, path: String): Page? {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(Page::class.java)
        val root = criteria.from(Page::class.java)

        criteria.select(root)
        criteria.where(
            criteriaBuilder.and(
                criteriaBuilder.equal(root.get(Page_.language), language),
                criteriaBuilder.equal(root.get(Page_.path), path)
            )
        )

        return getSingleResult(entityManager.createQuery(criteria))
    }

    /**
     * Updates page title
     *
     * @param page page to update
     * @param title title
     * @param modifierId modifier id
     * @return updated page
     */
    fun updateTitle(page: Page, title: String?, modifierId: UUID): Page {
        page.title = title
        page.lastModifierId = modifierId
        return persist(page)
    }

    /**
     * Updates page template
     *
     * @param page page to update
     * @param template template
     * @param modifierId modifier id
     * @return updated page
     */
    fun updateTemplate(page: Page, template: PageTemplate, modifierId: UUID): Page {
        page.template = template
        page.lastModifierId = modifierId
        return persist(page)
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
     * Updates page parent
     *
     * @param page page to update
     * @param parent page parent
     * @param modifierId modifiers id
     * @return updated page
     */
    fun updateParent(page: Page, parent: Page?, modifierId: UUID): Page {
        page.parent = parent
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
