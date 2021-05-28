package fi.metatavu.edufication.pages.api.impl.translate

import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.persistence.model.ContentBlock
import fi.metatavu.edufication.pages.api.persistence.model.Quiz
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator class for content blocks
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class ContentBlockTranslator: AbstractTranslator<ContentBlock, fi.metatavu.edufication.pages.api.model.ContentBlock>() {

    @Inject
    private lateinit var pagesController: PagesController

    override fun translate(entity: ContentBlock): fi.metatavu.edufication.pages.api.model.ContentBlock {
        val translated = fi.metatavu.edufication.pages.api.model.ContentBlock()
        translated.layout = entity.layout
        translated.link = entity.link
        translated.media = entity.media
        translated.textContent = entity.textContent
        translated.title = entity.title
        translated.orderInPage = entity.orderInPage
        translated.quiz = entity.quiz?.let { translateQuiz(it) }

        return translated
    }

    /**
     * Translates quiz
     *
     * @param quiz quiz
     * @return Api model quiz
     */
    private fun translateQuiz(quiz: Quiz): fi.metatavu.edufication.pages.api.model.Quiz {
        val result = fi.metatavu.edufication.pages.api.model.Quiz()
        result.text = quiz.text
        result.correctIndex = quiz.correctIndex
        result.options = pagesController.parseOptions(quiz.options)
        return result
    }
}