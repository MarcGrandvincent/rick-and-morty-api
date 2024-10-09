package org.mathieu.characters.details

import android.app.Application
import android.webkit.ConsoleMessage
import org.koin.core.component.inject
import org.mathieu.domain.models.location.LocationPreview
import org.mathieu.ui.Destination

sealed interface CharactersDetailsAction {
    data class SelectedLocation(val locationId: Int):
        CharactersDetailsAction
}


class CharacterDetailsViewModel(application: Application) :
    org.mathieu.ui.ViewModel<CharacterDetailsState>(
        CharacterDetailsState(), application
    ) {

    private val characterRepository: org.mathieu.domain.repositories.CharacterRepository by inject()

    fun init(characterId: Int) {
        fetchData(
            source = { characterRepository.getCharacter(id = characterId) }
        ) {

            onSuccess {
                updateState {
                    copy(
                        avatarUrl = it.avatarUrl,
                        name = it.name,
                        error = null,
                        locationId = it.localisationPreview.id,
                        locationName = it.localisationPreview.name,
                        locationType = it.localisationPreview.type
                    )
                }
            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }

    fun handleAction(action: CharactersDetailsAction) {
        when(action) {
            is CharactersDetailsAction.SelectedLocation -> selectedLocation(action.locationId)
        }
    }

    private fun selectedLocation(locationId: Int) =
        sendEvent(Destination.LocationDetails(locationId.toString()))
}


data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val avatarUrl: String = "",
    val name: String = "",
    val error: String? = null,
    val locationName: String = "",
    val locationType: String = "",
    val locationId: Int = -1,
)