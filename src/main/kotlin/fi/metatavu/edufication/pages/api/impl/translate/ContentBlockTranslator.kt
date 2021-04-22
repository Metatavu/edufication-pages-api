package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Page
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ContentBlockTranslator: AbstractTranslator<ContentBlock, fi.metatavu.edufication.pages.api.model.ContentBlock>() {
    override fun translate(entity: ContentBlock): fi.metatavu.edufication.pages.api.model.ContentBlock {
        val translated = fi.metatavu.edufication.pages.api.model.ContentBlock()
        translated.layout = entity.layout
        translated.link = entity.link
        translated.media = entity.media
        translated.textContent = entity.textContent
        translated.title = entity.title

        return translated
    }
}