package org.mathieu.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.data.local.CharacterLocal
import org.mathieu.data.local.objects.CharacterObject
import org.mathieu.data.local.objects.toModel
import org.mathieu.data.local.objects.toRealmObject
import org.mathieu.data.remote.CharacterApi
import org.mathieu.data.remote.responses.CharacterResponse
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.domain.repositories.CharacterRepository
import org.mathieu.domain.repositories.LocationRepository

private const val CHARACTER_PREFS = "character_repository_preferences"
private val nextPage = intPreferencesKey("next_characters_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = CHARACTER_PREFS
)

internal class CharacterRepositoryImpl(
    private val context: Context,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal,
    private val locationRepository: LocationRepository
) : CharacterRepository {

    override suspend fun getCharacters(): Flow<List<Character>> =
        characterLocal
            .getCharacters()
            .mapElement(transform = CharacterObject::toModel)
            .also { if (it.first().isEmpty()) fetchNext() }


    override suspend fun getCharactersByIds(ids: List<Int>): List<Character> {
        return characterApi.getCharactersById(ids).map { c -> c.toModel() };
    }

    /**
     * Fetches the next batch of characters and saves them to local storage.
     *
     * This function works as follows:
     * 1. Reads the next page number from the data store.
     * 2. If there's a valid next page (i.e., page is not -1), it fetches characters from the API for that page.
     * 3. Extracts the next page number from the API response and updates the data store with it.
     * 4. Transforms the fetched character data into their corresponding realm objects.
     * 5. Saves the transformed realm objects to the local database.
     *
     * Note: If the `next` attribute from the API response is null or missing, the page number is set to -1, indicating there's no more data to fetch.
     */
    private suspend fun fetchNext() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {
            val response = characterApi.getCharacters(page)

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            val objects = response.results.map(transform = CharacterResponse::toRealmObject)

            characterLocal.saveCharacters(objects)
        }

    }


    override suspend fun loadMore() = fetchNext()


    /**
     * Retrieves the character with the specified ID.
     *
     * The function follows these steps:
     * 1. Tries to fetch the character from the local storage.
     * 2. If not found locally, it fetches the character from the API.
     * 3. Upon successful API retrieval, it saves the character to local storage.
     * 4. If the character is still not found, it throws an exception.
     *
     * @param id The unique identifier of the character to retrieve.
     * @return The [Character] object representing the character details.
     * @throws Exception If the character cannot be found both locally and via the API.
     */
    override suspend fun getCharacter(id: Int): Character {
        var result: Character? = null;

        // Fetches the character from local storage
        val localCharacter = characterLocal.getCharacter(id)

        if (localCharacter == null) {
            // Otherwise, fetches the character from the API
            val apiResponse = characterApi.getCharacter(id)
            apiResponse?.let { response ->
                val realmObject = response.toRealmObject()

                characterLocal.insert(realmObject)

                result = realmObject.toModel()
            }
        } else {
            result = localCharacter.toModel();
        }

        if (result == null)
            throw Exception("Character not found.")

        // Fetches the corresponding location using the ID
        val locationPreview = getLocationPreview(localCharacter?.locationId)

        result!!.localisationPreview = locationPreview;

        return result as Character;
    }

    /**
     * Fetches location previews from the location ID.
     *
     * @param locationId The ID of the location.
     * @return the locations preview.
     */
    private suspend fun getLocationPreview(locationId: Int?): LocationPreview {
        return if (locationId != null && locationId != -1) {
            val location = locationRepository.getLocation(locationId)
            LocationPreview(
                id = location.id,
                name = location.name,
                type = location.type,
                dimension = location.dimension
            )
        } else throw Exception("Location not found")
    }
}


fun <T> tryOrNull(block: () -> T) = try {
    block()
} catch (_: Exception) {
    null
}

inline fun <T, R> Flow<List<T>>.mapElement(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list ->
        list.map { element -> transform(element) }
    }