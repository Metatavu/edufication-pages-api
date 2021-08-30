package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.persistence.model.*
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

/**
 * DAO class for content block navigation item entity
 */
@ApplicationScoped
class ContentBlockNavigationItemDAO: AbstractDAO<ContentBlockNavigationItem>() {

    /**
     * Creates a content block navigation item
     *
     * @param id id
     * @param contentBlock contentBlock
     * @param orderNumber order number
     * @param title title
     * @param url URL
     * @param imageUrl image URL
     * @return created content block navigation item
     */
    fun create(id: UUID, contentBlock: ContentBlock, orderNumber: Int, title: String, url: String, imageUrl: String?): ContentBlockNavigationItem {
        val result = ContentBlockNavigationItem()
        result.id = id
        result.contentBlock = contentBlock
        result.title = title
        result.url = url
        result.imageUrl = imageUrl
        result.orderNumber = orderNumber
        return persist(result)
    }

    /**
     * Lists content block navigation items for a content block
     *
     * @param contentBlock content block
     * @return list of content block navigation items
     */
    fun listByContentBlock(contentBlock: ContentBlock): List<ContentBlockNavigationItem> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(ContentBlockNavigationItem::class.java)
        val root = criteria.from(ContentBlockNavigationItem::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(ContentBlockNavigationItem_.contentBlock), contentBlock))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))
        criteria.orderBy(criteriaBuilder.asc(root.get(ContentBlockNavigationItem_.orderNumber)))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    /**
     * Updates content block navigation item title
     *
     * @param contentBlockNavigationItem content block navigation item
     * @param title title
     * @return updated content block navigation item
     */
    fun updateTitle(contentBlockNavigationItem: ContentBlockNavigationItem, title: String): ContentBlockNavigationItem {
        contentBlockNavigationItem.title = title
        return persist(contentBlockNavigationItem)
    }

    /**
     * Updates content block navigation item URL
     *
     * @param contentBlockNavigationItem content block navigation item
     * @param url URL
     * @return updated content block navigation item
     */
    fun updateUrl(contentBlockNavigationItem: ContentBlockNavigationItem, url: String): ContentBlockNavigationItem {
        contentBlockNavigationItem.url = url
        return persist(contentBlockNavigationItem)
    }

    /**
     * Updates content block navigation item image URL
     *
     * @param contentBlockNavigationItem content block navigation item
     * @param imageUrl URL
     * @return updated content block navigation item
     */
    fun updateImageUrl(contentBlockNavigationItem: ContentBlockNavigationItem, imageUrl: String?): ContentBlockNavigationItem {
        contentBlockNavigationItem.imageUrl = imageUrl
        return persist(contentBlockNavigationItem)
    }

    /**
     * Updates content block navigation item order number
     *
     * @param contentBlockNavigationItem content block navigation item
     * @param orderNumber order number
     * @return updated content block navigation item
     */
    fun updateOrderNumber(contentBlockNavigationItem: ContentBlockNavigationItem, orderNumber: Int): ContentBlockNavigationItem {
        contentBlockNavigationItem.orderNumber = orderNumber
        return persist(contentBlockNavigationItem)
    }

}
