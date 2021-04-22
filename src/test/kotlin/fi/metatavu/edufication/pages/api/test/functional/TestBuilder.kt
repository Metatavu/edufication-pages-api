package fi.metatavu.edufication.pages.api.test.functional

import fi.metatavu.edufication.pages.api.client.infrastructure.ApiClient
import fi.metatavu.edufication.pages.api.test.functional.auth.TestBuilderAuthentication
import fi.metatavu.edufication.pages.api.test.functional.settings.ApiTestSettings
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider
import org.eclipse.microprofile.config.ConfigProvider
import java.io.IOException

class TestBuilder: AbstractTestBuilder<ApiClient>() {

    val settings = ApiTestSettings()

    private var manager: TestBuilderAuthentication? = null

    override fun createTestBuilderAuthentication(testBuilder: AbstractTestBuilder<ApiClient>, accessTokenProvider: AccessTokenProvider): AuthorizedTestBuilderAuthentication<ApiClient> {
        return TestBuilderAuthentication(this, accessTokenProvider)
    }

    /**
     * Returns authentication resource authenticated as manager
     *
     * @return authentication resource authenticated as manager
     * @throws IOException
     */
    @kotlin.jvm.Throws(IOException::class)
    fun manager(): TestBuilderAuthentication {
        if (manager == null) {
            val authServerUrl: String = ConfigProvider.getConfig().getValue("keycloak.url", String::class.java)
            val realm: String = ConfigProvider.getConfig().getValue("keycloak.realm", String::class.java)
            val clientId = "test"
            val username = "manager"
            val password = "test"
            manager = TestBuilderAuthentication(this, KeycloakAccessTokenProvider(authServerUrl, realm, clientId, username, password, null))
        }

        return manager!!
    }
}