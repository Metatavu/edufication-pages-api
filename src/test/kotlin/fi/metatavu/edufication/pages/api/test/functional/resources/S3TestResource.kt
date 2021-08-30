package fi.metatavu.edufication.pages.api.test.functional.resources

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

/**
 * Class for S3 test resource.
 *
 * @author Antti Leinonen
 * @author Antti Leppä
 */
class S3TestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        localstack.start()

        val config: MutableMap<String, String> = HashMap()

        val endpointConf = localstack.getEndpointConfiguration(LocalStackContainer.Service.S3)
        val credentials = localstack.defaultCredentialsProvider

        config["quarkus.s3.endpoint-override"] = endpointConf.serviceEndpoint
        config["quarkus.s3.aws.region"] = endpointConf.signingRegion
        config["quarkus.s3.aws.credentials.type"] = "static"
        config["quarkus.s3.aws.credentials.static-provider.access-key-id"] = credentials.credentials.awsAccessKeyId
        config["quarkus.s3.aws.credentials.static-provider.secret-access-key"] = credentials.credentials.awsSecretKey
        config["s3.bucket"] = "edufication"
        config["s3.prefix"] = "files"

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
