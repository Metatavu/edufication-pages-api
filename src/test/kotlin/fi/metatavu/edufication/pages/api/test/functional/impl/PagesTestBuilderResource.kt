package fi.metatavu.edufication.pages.api.test.functional.impl

import fi.metatavu.edufication.pages.api.client.apis.PagesApi
import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.client.models.*
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import java.util.*

/**
 * Resource for testings Pages API
 *
 * @author Antti Leinonen
 */
class PagesTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
):ApiTestBuilderResource<Page, ApiClient?>(testBuilder, apiClient) {

    override fun clean(page: Page) {
        return api.deletePage(page.id!!)
    }

    override fun getApi(): PagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return PagesApi(testBuilder.settings.apiBasePath)
    }

    /**
     * Lists pages
     *
     * @param path filter by path
     * @return list of pages
     */
    fun listPages(path: String?): Array<Page> {
        return api.listPages(path)
    }

    /**
     * Finds page from the API
     *
     * @param pageId page id
     * @return Found page or null if not found
     */
    fun findPage(pageId: UUID): Page {
        return api.findPage(pageId = pageId)
    }

    /**
     * Deletes a page from the API
     *
     * @param pageId page id
     */
    fun deletePage(pageId: UUID) {
        api.deletePage(pageId = pageId)
        removeCloseable{ closable: Any ->
            if (closable !is Page) {
                return@removeCloseable false
            }

            val closeablePage: Page = closable
            closeablePage.id!! == pageId
        }
    }

    /**
     * Creates a page with default values
     *
     * @param path page path
     * @param template template
     * @param title title
     * @return Created page
     */
    fun createDefaultPage(path: String, template: PageTemplate = PageTemplate.eDUFICATION, title: String? = null): Page {
        val quiz1 = Quiz(
            text = "Onko hauki kala?",
            options = arrayOf("Ei", "Kyllä"),
            correctIndex = 1
        )

        val pageContent1 = ContentBlock (
            textContent = "Lorem ipsum dolor sit amet",
            link = Link(
                title = "title 1",
                url = "www.kissa.com",
            ),
            layout = ContentBlockLayout.mEDIALEFT,
            title = "Tämä on sivuston sisältöä",
            orderInPage = 0,
            quiz = quiz1
        )

        val pageContent2 = ContentBlock (
            layout = ContentBlockLayout.mEDIALEFTARTICLE,
            orderInPage = 1
        )

        val page = Page(
            title = title,
            path = path,
            contentBlocks = arrayOf(pageContent1, pageContent2),
            status = PageStatus.dRAFT,
            private = true,
            language = "fi",
            template = template
        )

        return createPage(page)
    }

    /**
     * Creates page and adds closable
     *
     * @param page page to create
     * @return created page
     */
    private fun createPage(page: Page): Page {
        val createdPage = api.createPage(page = page)
        addClosable(createdPage)
        return createdPage
    }

    /**
     * Updates a page
     */
    fun updatePage(pageId: UUID, page: Page): Page {
        return api.updatePage(pageId = pageId, page = page)
    }
}
