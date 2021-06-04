package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.persistence.model.Language
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class LanguageTranslator: AbstractTranslator<Language, fi.metatavu.edufication.pages.api.model.Language>() {

    override fun translate(entity: Language): fi.metatavu.edufication.pages.api.model.Language {
        val translated = fi.metatavu.edufication.pages.api.model.Language()
        translated.id = entity.id
        translated.name = entity.name

        return translated
    }
}