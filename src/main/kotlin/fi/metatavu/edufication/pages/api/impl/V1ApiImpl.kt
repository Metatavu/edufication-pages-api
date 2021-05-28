package fi.metatavu.edufication.pages.api.impl

import fi.metatavu.edufication.pages.api.controller.FilesController
import fi.metatavu.edufication.pages.api.controller.LanguagesController
import fi.metatavu.edufication.pages.api.controller.PagesController
import fi.metatavu.edufication.pages.api.impl.translate.LanguageTranslator
import fi.metatavu.edufication.pages.api.impl.translate.PageTranslator
import fi.metatavu.edufication.pages.api.model.Language
import fi.metatavu.edufication.pages.api.model.Page
import fi.metatavu.edufication.pages.api.spec.V1Api
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

@RequestScoped
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    private lateinit var pagesController: PagesController

    @Inject
    private lateinit var filesController: FilesController

    @Inject
    private lateinit var languagesController: LanguagesController

    @Inject
    private lateinit var pageTranslator: PageTranslator

    @Inject
    private lateinit var languageTranslator: LanguageTranslator

    @Transactional
    override fun createLanguage(language: Language): Response {
        val created = languagesController.create(language.name)

        return createOk(languageTranslator.translate(created))
    }

    @Transactional
    override fun createPage(page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val createdPage = pagesController.create(
            status = page.status,
            path = page.path,
            creatorId = userId,
            contentBlocks = page.contentBlocks,
            private = page.private,
            language = page.language
        )

        filesController.storeJsonPage(page)
        return createOk(pageTranslator.translate(createdPage))
    }

    @Transactional
    override fun deleteLanguage(languageId: UUID): Response {
        val language = languagesController.findLanguage(languageId) ?: return createNotFound(LANGUAGE_NOT_FOUND)
        languagesController.deleteLanguage(language)
        return createNoContent()
    }

    @Transactional
    override fun deletePage(pageId: UUID): Response {
        val page = pagesController.findPage(pageId) ?: return createNotFound()

        pagesController.deletePage(page)
        filesController.removeJsonPage(path = page.path!!, language = page.language!!)
        return createNoContent()
    }

    override fun findLanguage(languageId: UUID): Response {
        val language = languagesController.findLanguage(languageId) ?: return createNotFound(LANGUAGE_NOT_FOUND)
        return createOk(languageTranslator.translate(language))
    }

    override fun findPage(pageId: UUID): Response {
        val foundPage = pagesController.findPage(pageId) ?: return createNotFound(PAGE_NOT_FOUND_MESSAGE)

        return createOk(pageTranslator.translate(foundPage))
    }

    override fun listLanguages(): Response {
        val languages = languagesController.listLanguages()
        return createOk(languageTranslator.translate(languages))
    }

    override fun listPages(path: String?): Response {
        val pages = pagesController.listPages(path)

        return createOk(pageTranslator.translate(pages))
    }

    @Transactional
    override fun updatePage(pageId: UUID, page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val updatedPage = pagesController.update(
            pageId = pageId,
            status = page.status,
            path = page.path,
            modifierId = userId,
            contentBlocks = page.contentBlocks,
            private = page.private,
            language = page.language
        )

        return if (updatedPage == null) {
            createInternalServerError(PAGE_UPDATE_FAILED)
        } else {
            val translated = pageTranslator.translate(updatedPage)
            filesController.storeJsonPage(updatedPage)
            createOk(translated)
        }
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
        const val PAGE_NOT_FOUND_MESSAGE = "Page not found!"
        const val PAGE_UPDATE_FAILED = "Page update failed!"
        const val LANGUAGE_NOT_FOUND = "Language not found!"
    }
}
