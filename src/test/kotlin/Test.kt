import com.google.gson.annotations.SerializedName
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClients
import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.SslSettings
import org.bson.UuidRepresentation
import java.util.UUID

class Test {

    val credential = MongoCredential.createCredential("ikari", "admin", "1234".toCharArray())
    val settings = MongoClientSettings.builder()
        .uuidRepresentation(UuidRepresentation.STANDARD)
        .credential(credential)
        .applyToSslSettings { builder: SslSettings.Builder -> builder.enabled(false) }
        .applyToClusterSettings { builder: ClusterSettings.Builder ->
            builder.hosts(listOf(ServerAddress("localhost", 27017)))
        }
        .build()
    val client = MongoClients.create(settings).getDatabase("core")
    val collection = client.getCollection("entites")
    val uuid = UUID.fromString("8a5d2f2d-3940-4e56-b3ab-18022dc21e69")

    @org.junit.jupiter.api.Test
    fun tesThisNiggerShit() {
        /*
        val profile = SimpleEntity(uuid, "nigger")
        collection.save(profile)

        val document = collection.findById<SimpleEntity>(uuid)
        println(document)
         */
    }

    class SimpleEntity(@SerializedName("_id") val uuid: UUID, val name: String)
}