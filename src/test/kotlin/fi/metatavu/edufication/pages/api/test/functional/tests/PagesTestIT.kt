package fi.metatavu.edufication.pages.api.test.functional.tests

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fi.metatavu.edufication.pages.api.client.models.*
import fi.metatavu.edufication.pages.api.test.common.moshi.adapters.OffsetDateTimeAdapter
import fi.metatavu.edufication.pages.api.test.common.moshi.adapters.UUIDJsonAdapter
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.edufication.pages.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.edufication.pages.api.test.functional.resources.LocalTestProfile
import fi.metatavu.edufication.pages.api.test.functional.resources.MysqlTestResource
import fi.metatavu.edufication.pages.api.test.functional.resources.S3TestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.Okio
import org.junit.Assert
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests for Pages
 *
 * @author Antti Leinonen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(S3TestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
@TestProfile(LocalTestProfile::class)
class PagesTestIT {

    /**
     * Tests list pages
     */
    @Test
    fun listPages() {
        TestBuilder().use {
            val emptyList = it.manager().pages.listPages(null)
            assertEquals(0, emptyList.size)

            val firstPage = it.manager().pages.createDefaultPage("path1")
            val secondPage = it.manager().pages.createDefaultPage("path2")

            val listWithTwoEntries = it.manager().pages.listPages(null)
            assertEquals(2, listWithTwoEntries.size)

            it.manager().pages.deletePage(firstPage.id!!)
            it.manager().pages.deletePage(secondPage.id!!)

            val emptyListAfterDelete = it.manager().pages.listPages(null)
            assertEquals(0, emptyListAfterDelete.size)
        }
    }

    /**
     * Tests creating page
     */
    @Test
    fun createPage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createDefaultPage("path1")
            assertNotNull(createdPage)
            assertNotNull(createdPage.uri)
            val quiz = createdPage.contentBlocks.find { block -> block.orderInPage == 0 }?.quiz
            assertEquals(2, quiz?.options?.count())
            assertEquals(quiz?.text, "Onko hauki kala?")
            assertEquals(quiz?.correctIndex, 1)
        }
    }

    /**
     * Tests page hierarchy
     */
    @Test
    fun testPageHierarchy() {
        TestBuilder().use {
            val paths = arrayOf("/root", "/root/child1", "/root/child2", "/root/child1/subpage1", "/root/child1/subpage2")

            val createdPages = paths.map { path ->
                it.manager().pages.createDefaultPage(path)
            }

            val ( rootPage, childPage1, childPage2, subPage1, subPage2 ) = createdPages.map { createdPage ->
                it.manager().pages.findPage(createdPage.id!!)
            }

            assertNull(rootPage.parentPage)
            assertEquals(rootPage.id, childPage1.parentPage?.id)
            assertEquals(childPage1.id, subPage1.parentPage?.id)
            assertEquals(childPage1.id, subPage2.parentPage?.id)

            assertEquals(2, rootPage.childPages?.size)
            assertEquals(2, childPage1.childPages?.size)
            assertEquals(0, childPage2.childPages?.size)

            assertNotNull(childPage1.childPages?.map(PageReference::id)?.find { i -> i == subPage1.id })
            assertNotNull(childPage1.childPages?.map(PageReference::id)?.find { i -> i == subPage2.id })

            val updatedSubpage2 = it.manager().pages.updatePage(pageId = subPage2.id!!, page = subPage2.copy(path = "/root/child2/subpage2"))
            assertEquals(childPage2.id, updatedSubpage2.parentPage?.id)

            val updatedChildPage1 = it.manager().pages.findPage(pageId = childPage1.id!! )
            val updatedChildPage2 = it.manager().pages.findPage(pageId = childPage2.id!! )

            assertEquals(1, updatedChildPage1.childPages?.size)
            assertEquals(subPage1.id, updatedChildPage1.childPages?.get(0)?.id)

            assertEquals(1, updatedChildPage2.childPages?.size)
            assertEquals(subPage2.id, updatedChildPage2.childPages?.get(0)?.id)
        }
    }

    /**
     * Tests finding page
     */
    @Test
    fun findPage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createDefaultPage("path1")
            assertNotNull(createdPage)
            val foundPage = it.manager().pages.findPage(createdPage.id!!)
            assertNotNull(foundPage)
        }
    }

    /**
     * Tests updating page
     */
    @Test
    fun updatePage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createDefaultPage("path1")
            assertNotNull(createdPage)

            assertEquals(PageTemplate.eDUFICATION, createdPage.template)

            val updatedContent = ContentBlock(
                title = "Title 123123",
                textContent = "Text text text 123 123 123 ÄÄÄ ÖÖÖ",
                media = "media.fi",
                link = Link(
                    title = "Updated title",
                    url = "www.updatedlink.com",
                ),
                layout = ContentBlockLayout.mEDIALEFT,
                orderInPage = 0
            )

            val updatedPage = createdPage.copy(
                status = PageStatus.pUBLIC,
                template = PageTemplate.rOBOCOAST,
                uri = "",
                path = "/kurssit",
                contentBlocks = arrayOf(updatedContent),
                private = false,
                language = "fi"
            )

            val update = it.manager().pages.updatePage(createdPage.id!!, updatedPage)
            assertEquals(updatedPage.template, update.template)
            assertEquals(updatedPage.status, update.status)
            assertEquals(updatedPage.path, update.path)
            assertEquals(updatedPage.private, update.private)
            assertEquals(updatedPage.language, update.language)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.title, update.contentBlocks.getOrNull(0)?.title)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.layout, update.contentBlocks.getOrNull(0)?.layout)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.link, update.contentBlocks.getOrNull(0)?.link)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.media, update.contentBlocks.getOrNull(0)?.media)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.textContent, update.contentBlocks.getOrNull(0)?.textContent)
        }
    }

    /**
     * Tests deleting page
     */
    @Test
    fun deletePage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createDefaultPage("path1")
            assertNotNull(createdPage)
            download(createdPage.uri!!).toString()
            it.manager().pages.deletePage(createdPage.id!!)
            downloadFail(createdPage.uri).toString()
        }
    }

    @Test
    fun testStoredPageTemplate() {
        TestBuilder().use {
            it.manager().pages.createDefaultPage(path = "/edufication", template = PageTemplate.eDUFICATION)
            it.manager().pages.createDefaultPage(path = "/robocoast", template = PageTemplate.rOBOCOAST)
            val createdInheritedPage = it.manager().pages.createDefaultPage("/edufication/inherit", template = PageTemplate.iNHERIT)

            assertEquals(PageTemplate.iNHERIT, createdInheritedPage.template)
            val createdStoredPage = getStoredPage(createdInheritedPage.uri)
            assertNotNull(createdStoredPage)
            assertEquals(PageTemplate.eDUFICATION, createdStoredPage!!.template)

            val updatedInheritedPage = it.manager().pages.updatePage(createdInheritedPage.id!!, createdInheritedPage.copy(path = "/robocoast/inherit"))

            assertEquals(PageTemplate.iNHERIT, updatedInheritedPage.template)
            val updatedStoredPage = getStoredPage(updatedInheritedPage.uri)
            assertNotNull(updatedStoredPage)
            assertEquals(PageTemplate.rOBOCOAST, updatedStoredPage!!.template)
        }
    }

    /**
     * Downloads stored file from URI
     *
     * @param uri URI
     * @return stored file or null if not found
     */
    private fun getStoredPage(uri: String?): Page? {
        uri ?: return null
        val data = download(uri) ?: return null
        return data.use(this::readStoredPage)
    }

    /**
     * Reads stored file from input stream
     *
     * @param data data
     * @return stored file
     */
    private fun readStoredPage(data: InputStream): Page? {
        val moshi = Moshi.Builder()
            .add(UUIDJsonAdapter())
            .add(OffsetDateTimeAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter: JsonAdapter<Page> = moshi.adapter(Page::class.java)

        return Okio.source(data)
            .use(Okio::buffer)
            .use(jsonAdapter::fromJson)
    }

    /**
     * Downloads a file and asserts that the download succeeds
     *
     * @param uri path to download from
     * @return file input stream
     */
    private fun download(uri: String): InputStream? {
        val request: Request = Request.Builder()
            .url(uri)
            .get()
            .build()

        val response: Response = OkHttpClient().newCall(request).execute()
        Assert.assertTrue(response.isSuccessful)

        val body = response.body()
        Assert.assertNotNull(body)

        return body?.byteStream()
    }

    /**
     * Attempts to download a file but asserts that the download fails
     *
     * @param uri path to attempt download from
     */
    private fun downloadFail(uri: String) {
        val request: Request = Request.Builder()
            .url(uri)
            .get()
            .build()

        val response: Response = OkHttpClient().newCall(request).execute()
        Assert.assertTrue(!response.isSuccessful)
    }
}
