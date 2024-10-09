package org.mathieu.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.mathieu.data.local.objects.LocationObject

internal class LocationLocal(private val database: RealmDatabase) {

    /**
     * Get a location in the database based on its ID.
     *
     * @param id ID of the location.
     * @return the found location if it exists.
     */
    suspend fun getLocation(id: Int) : LocationObject? = database.use {
        query<LocationObject>("id == $id").first().find()
    }

    /**
     * Insert a location in the database.
     *
     * @param location the location to insert.
     */
    suspend fun insert(location: LocationObject) {
        database.write {
            copyToRealm(location, UpdatePolicy.ALL)
        }
    }
}