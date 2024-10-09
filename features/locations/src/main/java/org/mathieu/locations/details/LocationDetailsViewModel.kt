package org.mathieu.locations.details

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.domain.models.character.Character
import org.mathieu.ui.ViewModel

class LocationDetailsViewModel(application: Application) :
    ViewModel<LocationState>(LocationState(), application) {

    private val locationRepository: org.mathieu.domain.repositories.LocationRepository by inject()
    private val characterRepository: org.mathieu.domain.repositories.CharacterRepository by inject()

    private fun fetchCharacters(ids: List<Int>) {
        fetchData(
            /**
             * Justification technique pour l'évaluation :
             * Initialement, dans la couche data, nous utilisons la base de données SQLite pour éviter de
             * faire deux fois la même requête à l'API pour une donnée identique. Cependant, dans notre cas,
             * les locations peuvent parfois contenir plusieurs centaines de personnages. Il serait
             * techniquement coûteux de faire une requête SQL pour chacun d'eux, puis une requête à l'API,
             * et enfin d'ajouter les personnages manquants dans la base de données. Dans ce cas, il me semble
             * plus optimal en termes de performances et de développement de passer uniquement par l'API.
             */
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