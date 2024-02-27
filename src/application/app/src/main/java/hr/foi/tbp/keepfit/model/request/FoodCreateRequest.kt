package hr.foi.tbp.keepfit.model.request

import hr.foi.tbp.keepfit.model.response.FoodDetails

data class FoodCreateRequest(
    val type: String,
    var name: String,
    var calories: Double,
    var nutrients : FoodDetails
)
