package model

import java.sql.Timestamp


class SortModel {
    var name: String? = null
    var location: String? = null
    var id: Long? = null
    var street: String? = null
    var number: String? = null
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
    var aanmaakDatum: Timestamp? = null
    var wijzigDatum: Timestamp? = null
    var lat: Double? = null
    var lon: Double? = null
}