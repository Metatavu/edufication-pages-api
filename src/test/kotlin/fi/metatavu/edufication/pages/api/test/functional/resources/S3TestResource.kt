package fi.metatavu.edufication.pages.api.test.functional.resources

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

/**
 * Class for Keycloak test resource.
 *
 * @author Antti Leinonen
 */
class S3TestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        localstack.start()

        val config: MutableMap<String, String> = HashMap()

        val endpointConf = localstack.getEndpointConfiguration(LocalStackContainer.Service.S3)
        val credentials = localstack.defaultCredentialsProvider

        config["s3.region"] = endpointConf.signingRegion
        config["s3.bucket"] = "edufication"
        config["s3.prefix"] = "files"

        config["s3.access"] = credentials.credentials.awsAccessKeyId
        config["s3.secret"] = credentials.credentials.awsSecretKey

        config["s3.endpoint"] = endpointConf.serviceEndpoint

        val client = AmazonS3ClientBuilder
            .standard()
            .withPathStyleAccessEnabled(true)
            .withEndpointConfiguration(endpointConf)
            .withCredentials(credentials)
            .build()

        client.createBucket("edufication")
        return config
    }

    override fun stop() {
        localstack.stop()
    }

    companion object {
        private val localstackImage: DockerImageName = DockerImageName.parse("localstack/localstack:0.11.3")
        val localstack: LocalStackContainer = LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.S3)
    }
}
