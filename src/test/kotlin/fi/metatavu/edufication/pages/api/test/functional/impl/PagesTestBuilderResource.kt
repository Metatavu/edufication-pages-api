package fi.metatavu.edufication.pages.api.test.functional.impl

import fi.metatavu.edufication.pages.api.client.apis.PagesApi
import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.client.models.ContentBlock
import fi.metatavu.edufication.pages.api.client.models.ContentBlockLayout
import fi.metatavu.edufication.pages.api.client.models.Page
import fi.metatavu.edufication.pages.api.client.models.PageStatus
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

    override fun getApi(): PagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return PagesApi(testBuilder.settings.apiBasePath)
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
    fun deletePage(pageId: UUID?) {
        if (pageId != null) {
            api.deletePage(pageId = pageId)
        }
    }

    /**
     * Creates a page
     *
     * @return Created page
     */
    fun createPage(): Page {
        val pageContent1 = ContentBlock (
            textContent = "Lorem ipsum dolor sit amet",
            link = "www.kissa.fi",
            layout = ContentBlockLayout.mEDIALEFT,
            title = "Tämä on sivuston sisältöä"
        )

        val pageContent2 = ContentBlock (
            textContent = "Lorem ipsum dolor sit amet as simo ber",
            link = "www.kissa.com",
            layout = ContentBlockLayout.mEDIALEFTARTICLE,
            title = "Tämä on lisää sivuston sisältöä"
        )

        val page = Page(
            path = "/",
            contentBlocks = arrayOf(pageContent1, pageContent2),
            status = PageStatus.dRAFT,
            private = true
        )

        return api.createPage(page = page)
    }

    /**
     * Updates a page
     */
    fun updatePage(pageId: UUID, page: Page): Page {
        return api.updatePage(pageId = pageId, page = page)
    }
}
