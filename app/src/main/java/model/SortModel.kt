package model

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class SortModel() :Parcelable {
    var name: String? = null
    var locatie: String? = null
    var id: Long? = null
    var straat: String? = null
    var nummer: String? = null
    var postCode: String? = null
    var gemeente: String? = null
    var land: String? = null
    var omschrijving: String? = null
    var wikiLink: String? = null
    var website: String? = null
    var telefoonNummer: String? = null
    var email: String? = null
    var prijs: Double? = null
    var persoon: String? = null
    var aanmaakDatum: Date? = null
    var wijzigDatum: Date? = null
    var lat: Double? = null
    var lon: Double? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        locatie = parcel.readString()
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        straat = parcel.readString()
        nummer = parcel.readString()
        postCode = parcel.readString()
        gemeente = parcel.readString()
        land = parcel.readString()
        omschrijving = parcel.readString()
        wikiLink = parcel.readString()
        website = parcel.readString()
        telefoonNummer = parcel.readString()
        email = parcel.readString()
        prijs = parcel.readValue(Double::class.java.classLoader) as? Double
        persoon = parcel.readString()
        lat = parcel.readValue(Double::class.java.classLoader) as? Double
        lon = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(locatie)
        parcel.writeValue(id)
        parcel.writeString(straat)
        parcel.writeString(nummer)
        parcel.writeString(postCode)
        parcel.writeString(gemeente)
        parcel.writeString(land)
        parcel.writeString(omschrijving)
        parcel.writeString(wikiLink)
        parcel.writeString(website)
        parcel.writeString(telefoonNummer)
        parcel.writeString(email)
        parcel.writeValue(prijs)
        parcel.writeString(persoon)
        parcel.writeValue(lat)
        parcel.writeValue(lon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SortModel> {
        override fun createFromParcel(parcel: Parcel): SortModel {
            return SortModel(parcel)
        }

        override fun newArray(size: Int): Array<SortModel?> {
            return arrayOfNulls(size)
        }
    }
}