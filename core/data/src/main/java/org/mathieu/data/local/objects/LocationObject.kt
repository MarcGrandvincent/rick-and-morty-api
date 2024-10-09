package org.mathieu.data.local.objects

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.data.remote.responses.LocationResponse
import org.mathieu.domain.models.character.Character
import org.mathieu.domain.models.character.CharacterGender
import org.mathieu.domain.models.character.CharacterStatus
import org.mathieu.domain.models.location.Location
import org.mathieu.domain.models.location.LocationPreview

/**
 * Entity representing a location in the database.
 *
 * @property id Unique identifier of the location.
 * @property name Name of the location.
 * @property type Type of the location.
 * @property dimension Dimension of the location.
 * @property residents Residents of the location. It is a multitude of IDs separated with a ','
 * @property url Url of the location.
 * @property created Creation date of the location.
 */
internal class LocationObject : RealmObject {
    @PrimaryKey
    var id: Int = -1;
    var name: String = "";
    var type: String = ""
    var dimension: String = ""
    var residents: String = ""
    var url: String = ""
    var created: String = ""
}

internal fun LocationResponse.toRealmObject() = LocationObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.type = type
    obj.dimension = dimension
    obj.residents = residents.joinToString(separator = ",") { url -> url.split("/").last() }
    obj.url = url
    obj.created = created
}

internal fun LocationObject.toModel() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residents = residents.split(",").map { r ->
        Character(
            id = r.toInt(),
            name = "",
            type = "",
            status = CharacterStatus.Unknown,
            species = "",
            gender = CharacterGender.Unknown,
            avatarUrl = "",
            localisationPreview = LocationPreview(-1, "", "", ""),
            location = "" to 0,
            origin = "" to 0
        )
    }
)
