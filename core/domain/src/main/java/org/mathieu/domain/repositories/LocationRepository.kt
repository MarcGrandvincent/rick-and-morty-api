package org.mathieu.domain.repositories

import org.mathieu.domain.models.location.Location

/**
 * Repository of the locations
 */
interface LocationRepository {
    /**
     * Fetches the details of a specific location based on the provided ID.
     *
     * @param id The unique identifier of the location.
     * @return The location.
     */
    suspend fun getLocation(id: Int): Location
}