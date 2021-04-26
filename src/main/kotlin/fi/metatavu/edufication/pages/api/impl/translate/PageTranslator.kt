package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.persistence.model.Page
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PageTranslator: AbstractTranslator<Page, fi.metatavu.edufication.pages.api.model.Page>() {

    @Inject
    private lateinit var pagesController: PagesController

    @Inject
    private lateinit var contentBlockTranslator: ContentBlockTranslator

    override fun translate(entity: Page): fi.metatavu.edufication.pages.api.model.Page {
        val translated = fi.metatavu.edufication.pages.api.model.Page()
        translated.id = entity.id
        translated.contentBlocks = contentBlockTranslator.translate(pagesController.getPageContent(entity))
        translated.createdAt = entity.createdAt
        translated.creatorId = entity.creatorId
        translated.path = entity.path
        translated.uri = ""
        translated.status = entity.status
        translated.lastModifierId = entity.lastModifierId
        translated.modifiedAt = entity.modifiedAt

        return translated
    }
}