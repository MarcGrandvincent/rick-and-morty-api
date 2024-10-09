package org.mathieu.data.repositories

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import org.mathieu.data.local.LocationLocal
import org.mathieu.data.local.objects.toModel
import org.mathieu.data.local.objects.toRealmObject
import org.mathieu.data.remote.LocationApi
import org.mathieu.domain.models.location.Location
import org.mathieu.domain.repositories.LocationRepository

private const val LOCATION_PREFS = "location_repository_preferences"

private val Context.dataStore by preferencesDataStore(
    name = LOCATION_PREFS
)

internal class LocationRepositoryImpl(
    private val context: Context,
    private val locationApi: LocationApi,
    private val locationLocal: LocationLocal
) : LocationRepository {
    override suspend fun getLocation(id: Int): Location {
        return locationLocal.getLocation(id)?.toModel()
            ?: locationApi.getLocation(id = id)?.let { response ->
                val obj = response.toRealmObject()
                locationLocal.insert(obj)
                obj.toModel()
            }
            ?: throw Exception("Location not found.")
    }
}



