package fi.metatavu.edufication.pages.api.persistence.dao

import fi.metatavu.edufication.pages.api.model.ContentBlockLayout
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock_
import fi.metatavu.edufication.pages.api.persistence.model.Page
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.criteria.Predicate

@ApplicationScoped
class ContentBlockDAO: AbstractDAO<ContentBlock>() {

    /**
     * Creates a new ContentBlock
     *
     * @param id id
     * @param page page
     * @param layout layout
     * @param title title
     * @param textContent textContent
     * @param media textContent
     * @param link textContent
     * @return created ContentBlock
     */
    fun create(id: UUID, page: Page, layout: ContentBlockLayout, title: String?, textContent: String?, media: String?, link: String?): ContentBlock {
        val result = ContentBlock()
        result.id = id
        result.page = page
        result.layout = layout
        result.title = title
        result.textContent = textContent
        result.media = media
        result.link = link
        return persist(result)
    }

    /**
     * Lists all content for a given page
     *
     * @param page Page to find content for
     *
     * @return Content Block List
     */
    fun listByPage(page: Page): List<ContentBlock> {
        val entityManager = getEntityManager()
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteria = criteriaBuilder.createQuery(ContentBlock::class.java)
        val root = criteria.from(ContentBlock::class.java)

        criteria.select(root)
        val restrictions = ArrayList<Predicate>()

        restrictions.add(criteriaBuilder.equal(root.get(ContentBlock_.page), page))

        criteria.where(criteriaBuilder.and(*restrictions.toTypedArray()))

        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}
