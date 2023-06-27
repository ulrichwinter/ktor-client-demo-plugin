import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

val DemoPlugin = createClientPlugin("Demo") {

    // The test fails, if one of response.readBytes() or response.bodyAsText() is called in this hook.
    onResponse { response ->
        val body = String(response.readBytes(), response.charset() ?: Charsets.UTF_8)
//        failure reason, if response.readBytes() is called:
//        expected:<[Hello, world!]> but was:<[]>

//        val body = response.bodyAsText(Charsets.UTF_8) // using bodyAsText() consumes the response content in a way, that the
//        failure reason, if response.bodyAsText() is called:
//        Parent job is Completed
//                kotlinx.coroutines.JobCancellationException: Parent job is Completed; job=JobImpl{Completed}@1782e8fc
    }
}

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val client = createClient {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(DemoPlugin)
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }
}
