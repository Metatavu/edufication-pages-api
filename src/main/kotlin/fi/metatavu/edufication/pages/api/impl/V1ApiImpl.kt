package fi.metatavu.edufication.pages.api.impl

import fi.metatavu.edufication.pages.api.controller.LanguagesController
import fi.metatavu.edufication.pages.api.pages.PagesController
import fi.metatavu.edufication.pages.api.impl.translate.LanguageTranslator
import fi.metatavu.edufication.pages.api.impl.translate.PageTranslator
import fi.metatavu.edufication.pages.api.model.Language
import fi.metatavu.edufication.pages.api.model.Page
import fi.metatavu.edufication.pages.api.spec.V1Api
import fi.metatavu.edufication.pages.api.storage.StorageController
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response

@RequestScoped
@Transactional
class V1ApiImpl: V1Api, AbstractApi()  {

    @Inject
    private lateinit var pagesController: PagesController

    @Inject
    private lateinit var storageController: StorageController

    @Inject
    private lateinit var languagesController: LanguagesController

    @Inject
    private lateinit var pageTranslator: PageTranslator

    @Inject
    private lateinit var languageTranslator: LanguageTranslator

    /** LANGUAGES */

    override fun listLanguages(): Response {
        val languages = languagesController.listLanguages()
        return createOk(languages.map(languageTranslator::translate))
    }

    override fun createLanguage(language: Language): Response {
        val createdLanguage = languagesController.create(name = language.name)

        return createOk(languageTranslator.translate(createdLanguage))
    }

    override fun findLanguage(languageId: UUID): Response {
        val language = languagesController.findLanguage(languageId) ?: return createNotFound(LANGUAGE_NOT_FOUND)
        return createOk(languageTranslator.translate(language))
    }

    override fun deleteLanguage(languageId: UUID): Response {
        val language = languagesController.findLanguage(languageId) ?: return createNotFound(LANGUAGE_NOT_FOUND)
        languagesController.deleteLanguage(language)
        return createNoContent()
    }

    /** PAGES */

    override fun listPages(parentPageId: UUID?): Response {
        var parentPage: fi.metatavu.edufication.pages.api.persistence.model.Page? = null
        if (parentPageId != null) {
            parentPage = pagesController.findPage(pageId = parentPageId) ?: return createNotFound("Page with ID $parentPageId could not be found!")
        }

        val pages = pagesController.list(parentPage)
        return createOk(pages.map(pageTranslator::translate))
    }

    override fun createPage(page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)

        val path = page.path
        val parentPath = pagesController.getParentPath(path = path)
        val parent = if (parentPath != null) {
            pagesController.findPageByPath(path = parentPath) ?: return createBadRequest("Invalid path, parent not found!")
        } else {
            null
        }

        val createdPage = pagesController.create(
            title = page.title,
            template = page.template,
            status = page.status,
            path = path,
            parent = parent,
            creatorId = userId,
            contentBlocks = page.contentBlocks,
            private = page.private,
            language = page.language
        )

        val result = pageTranslator.translate(createdPage)

        storageController.storePage(
            page = createdPage,
            translatedPage = result
        )

        return createOk(result)
    }

    override fun findPage(pageId: UUID): Response {
        val foundPage = pagesController.findPage(pageId) ?: return createNotFound(PAGE_NOT_FOUND_MESSAGE)

        return createOk(pageTranslator.translate(foundPage))
    }

    override fun updatePage(pageId: UUID, page: Page): Response {
        val userId = loggerUserId ?: return createUnauthorized(NO_VALID_USER_MESSAGE)
        val foundPage = pagesController.findPage(pageId) ?: return createNotFound("Page with id $pageId could not be found")

        val path = page.path
        val parentPath = pagesController.getParentPath(path = path)
        val parent = if (parentPath != null) {
            pagesController.findPageByPath(path = parentPath) ?: return createBadRequest("Invalid path, parent not found!")
        } else {
            null
        }

        val updatedPage = pagesController.update(
            title = page.title,
            template = page.template,
            page = foundPage,
            status = page.status,
            path = path,
            parent = parent,
            modifierId = userId,
            contentBlocks = page.contentBlocks,
            private = page.private,
            language = page.language
        )

        val result = pageTranslator.translate(updatedPage)

        storageController.storePage(
            page = updatedPage,
            translatedPage = result
        )

        return createOk(result)
    }

    override fun deletePage(pageId: UUID): Response {
        val page = pagesController.findPage(pageId) ?: return createNotFound()

        storageController.deletePage(page)
        pagesController.deletePage(page)

        return createNoContent()
    }

    override fun ping(): Response {
        return createOk("pong")
    }

    companion object {
        const val NO_VALID_USER_MESSAGE = "No valid user!"
        const val PAGE_NOT_FOUND_MESSAGE = "Page not found!"
        const val LANGUAGE_NOT_FOUND = "Language not found!"
    }
}
