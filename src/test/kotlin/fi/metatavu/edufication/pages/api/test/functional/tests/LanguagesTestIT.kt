package fi.metatavu.edufication.pages.api.test.functional.tests

import fi.metatavu.edufication.pages.api.test.functional.TestBuilder
import fi.metatavu.edufication.pages.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.edufication.pages.api.test.functional.resources.LocalTestProfile
import fi.metatavu.edufication.pages.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Tests for Languages
 *
 * @author Antti Leinonen
 * @author Jari Nykänen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
@TestProfile(LocalTestProfile::class)
class LanguagesTestIT {

    /**
     * Tests listing language
     */
    @Test
    fun listLanguage() {
        TestBuilder().use {

            val emptyLanguageList = it.manager().languages.listLanguage()
            assertEquals(0, emptyLanguageList.size)

            val createdLanguage = it.manager().languages.createLanguage()
            assertNotNull(createdLanguage.id)
            assertNotNull(createdLanguage.name)

            val languages = it.manager().languages.listLanguage()
            assertEquals(1, languages.size)
        }
    }

    /**
     * Tests creating language
     */
    @Test
    fun createLanguage() {
        TestBuilder().use {
            val createdLanguage = it.manager().languages.createLanguage()
            assertNotNull(createdLanguage.id)
            assertNotNull(createdLanguage.name)
        }
    }

    /**
     * Tests finding language
     */
    @Test
    fun findLanguage() {
        TestBuilder().use {
            val createdLanguage = it.manager().languages.createLanguage()
            val foundLanguage = it.manager().languages.findLanguage(createdLanguage.id!!)
            assertNotNull(foundLanguage)
        }
    }

    /**
     * Tests deleting language
     */
    @Test
    fun deleteLanguage() {
        TestBuilder().use {
            val createdLanguage = it.manager().languages.createLanguage()
            val foundLanguage = it.manager().languages.findLanguage(createdLanguage.id!!)
            assertNotNull(foundLanguage)
            it.manager().languages.deleteLanguage(foundLanguage.id!!)
        }
    }
}
