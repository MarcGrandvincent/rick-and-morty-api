package org.mathieu.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.data.remote.responses.LocationResponse

/**
 * API for the locations information
 *
 * @param client The http client to call the API.
 */
internal class LocationApi(private val client: HttpClient) {

    /**
     * Fetches the details of a location with the given ID.
     *
     * @param @id The location's IDs.
     * @return The [LocationResponse] representing the location.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun getLocation(id: Int): LocationResponse? = client
        .get("location/$id")
        .accept(HttpStatusCode.OK)
        .body()
}