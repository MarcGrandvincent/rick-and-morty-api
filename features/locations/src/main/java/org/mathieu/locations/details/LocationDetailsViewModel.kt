package org.mathieu.locations.details

import android.app.Application
import android.util.Log
import org.koin.core.component.inject
import org.mathieu.domain.models.character.Character
import org.mathieu.ui.ViewModel

class LocationDetailsViewModel(application: Application) :
    ViewModel<LocationState>(LocationState(), application) {

    private val locationRepository: org.mathieu.domain.repositories.LocationRepository by inject()
    private val characterRepository: org.mathieu.domain.repositories.CharacterRepository by inject()

    private fun fetchCharacters(ids: List<Int>) {
        fetchData(
            source = { characterRepository.getCharactersByIds(ids) }
        ) {
            onSuccess {
                updateState {
                    copy(
                        characters = it
                    )
                }
            }
            onFailure {
                updateState { copy(error = it.toString()) }
            }
        }
    }

    fun init(locationId: Int) {
        fetchData(
            source = { locationRepository.getLocation(id = locationId) },
        ) {
            onSuccess {
                updateState {
                    copy(
                        error = null,
                        locationDimension = it.dimension,
                        locationName = it.name,
                        locationType = it.type
                    )
                }
                fetchCharacters(it.residents.map { r -> r.id })
            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }

}

data class LocationState(
    val isLoading: Boolean = true,
    val locationName: String = "",
    val locationType: String = "",
    val locationDimension: String = "",
    val characters: List<Character> = emptyList(),
    val error: String? = null
)