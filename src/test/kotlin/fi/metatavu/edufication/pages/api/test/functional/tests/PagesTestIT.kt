package fi.metatavu.edufication.pages.api.test.functional.tests

import fi.metatavu.edufication.pages.api.client.models.ContentBlock
import fi.metatavu.edufication.pages.api.client.models.ContentBlockLayout
import fi.metatavu.edufication.pages.api.client.models.Page
import fi.metatavu.edufication.pages.api.client.models.PageStatus
import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.edufication.pages.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.edufication.pages.api.test.functional.resources.LocalTestProfile
import fi.metatavu.edufication.pages.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.Assert
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*


/**
 * Tests for Pages
 *
 * @author Antti Leinonen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
@TestProfile(LocalTestProfile::class)
class PagesTestIT {

    /**
     * Tests creating page
     */
    @Test
    fun createPage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createPage()
            assertNotNull(createdPage)
        }
    }

    /**
     * Tests finding page
     */
    @Test
    fun findPage() {
        TestBuilder().use {
            val createdPage = it.manager().pages.createPage()
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
            val createdPage = it.manager().pages.createPage()
            assertNotNull(createdPage)

            val updatedContent = ContentBlock(
                title = "Title 123123",
                textContent = "Text text text 123 123 123",
                media = "media.fi",
                link = "google.fi",
                layout = ContentBlockLayout.sMALL
            )
            val updatedPage = Page(
                status = PageStatus.pUBLIC,
                uri = "",
                path = "/kurssit",
                contentBlocks = arrayOf(updatedContent)
            )

            val update = it.manager().pages.updatePage(createdPage.id!!, updatedPage)
            assertEquals(updatedPage.status, update.status)
            assertEquals(updatedPage.path, update.path)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.title, update.contentBlocks.getOrNull(0)?.title)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.layout, update.contentBlocks.getOrNull(0)?.layout)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.link, update.contentBlocks.getOrNull(0)?.link)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.media, update.contentBlocks.getOrNull(0)?.media)
            assertEquals(updatedPage.contentBlocks.getOrNull(0)?.textContent, update.contentBlocks.getOrNull(0)?.textContent)
        }
    }
}
