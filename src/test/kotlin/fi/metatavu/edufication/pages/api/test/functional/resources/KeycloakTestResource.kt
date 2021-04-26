package fi.metatavu.edufication.pages.api.test.functional.resources

import dasniko.testcontainers.keycloak.KeycloakContainer
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

/**
 * Class for Keycloak test resource.
 *
 * @author Antti Leinonen
 */
class KeycloakTestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        keycloak.start()
        val config: MutableMap<String, String> = HashMap()
        config["quarkus.oidc.auth-server-url"] = java.lang.String.format("%s/realms/Edufication", keycloak.authServerUrl)
        config["quarkus.oidc.client-id"] = "api"
        config["quarkus.oidc.credentials.secret"] = "bb6a066e-3728-460f-a97c-aa73b0318475"

        config["keycloak.url"] = keycloak.authServerUrl
        config["keycloak.realm"] = "Edufication"

        return config
    }

    override fun stop() {
        keycloak.stop()
    }

    companion object {
        val keycloak: KeycloakContainer = KeycloakContainer()
            .withRealmImportFile("kc.json")
    }
}