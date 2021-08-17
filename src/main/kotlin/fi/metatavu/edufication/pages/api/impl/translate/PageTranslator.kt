package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.pages.PagesController
import fi.metatavu.edufication.pages.api.model.PageReference
import fi.metatavu.edufication.pages.api.persistence.model.Page
import fi.metatavu.edufication.pages.api.storage.StorageController
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PageTranslator: AbstractTranslator<Page, fi.metatavu.edufication.pages.api.model.Page>() {

    @Inject
    lateinit var pagesController: PagesController

    @Inject
    lateinit var storageController: StorageController

    @Inject
    lateinit var contentBlockTranslator: ContentBlockTranslator

    override fun translate(entity: Page): fi.metatavu.edufication.pages.api.model.Page {
        val translated = fi.metatavu.edufication.pages.api.model.Page()
        translated.id = entity.id
        translated.template = entity.template
        translated.contentBlocks = contentBlockTranslator.translate(pagesController.getPageContent(entity)).sortedBy { it.orderInPage }
        translated.createdAt = entity.createdAt
        translated.creatorId = entity.creatorId
        translated.language = entity.language
        translated.path = entity.path
        translated.uri = getUri(page = entity)
        translated.status = entity.status
        translated.private = entity.private
        translated.parentPage = getPageReference(page = entity.parent)
        translated.childPages = pagesController.listChildPages(parent = entity).map(this::getPageReference)
        translated.lastModifierId = entity.lastModifierId
        translated.modifiedAt = entity.modifiedAt

        return translated
    }

    /**
     * Returns URI for a page
     *
     * @param page page
     * @return page URI
     */
    private fun getUri(page: Page): String? {
        return page.path?.let { storageController.getPageUrl(language = page.language!!, path = page.path!!)?.toExternalForm() }
    }

    /**
     * Returns page reference for a page
     *
     * @param page
     * @return page reference
     */
    private fun getPageReference(page: Page?): PageReference? {
        page ?: return null

        val result = PageReference()
        result.id = page.id
        result.path = page.path
        result.uri = getUri(page = page)

        return result
    }

}