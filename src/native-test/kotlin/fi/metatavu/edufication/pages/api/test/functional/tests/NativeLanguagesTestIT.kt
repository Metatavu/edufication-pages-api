package fi.metatavu.edufication.pages.api.test.functional.tests

import fi.metatavu.edufication.pages.api.test.functional.resources.KeycloakTestResource
import fi.metatavu.edufication.pages.api.test.functional.resources.LocalTestProfile
import fi.metatavu.edufication.pages.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.NativeImageTest
import io.quarkus.test.junit.TestProfile

/**
 * Native tests for Languages
 *
 * @author Antti Leppä
 */
@NativeImageTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlTestResource::class)
)
@TestProfile(LocalTestProfile::class)
class NativeLanguagesTestIT: LanguagesTestIT() {

}
