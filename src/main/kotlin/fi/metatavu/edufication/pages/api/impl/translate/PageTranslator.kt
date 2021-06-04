package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.controller.FilesController
import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.persistence.model.Page
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PageTranslator: AbstractTranslator<Page, fi.metatavu.edufication.pages.api.model.Page>() {

    @Inject
    private lateinit var pagesController: PagesController

    @Inject
    private lateinit var filesController: FilesController

    @Inject
    private lateinit var contentBlockTranslator: ContentBlockTranslator

    override fun translate(entity: Page): fi.metatavu.edufication.pages.api.model.Page {
        val translated = fi.metatavu.edufication.pages.api.model.Page()
        translated.id = entity.id
        translated.contentBlocks = contentBlockTranslator.translate(pagesController.getPageContent(entity)).sortedBy { it.orderInPage }
        translated.createdAt = entity.createdAt
        translated.creatorId = entity.creatorId
        translated.language = entity.language
        translated.path = entity.path
        translated.uri = entity.path?.let { filesController.getPageUrl(language = entity.language!!, path = entity.path!!)?.toExternalForm() }
        translated.status = entity.status
        translated.private = entity.private
        translated.lastModifierId = entity.lastModifierId
        translated.modifiedAt = entity.modifiedAt

        return translated
    }
}