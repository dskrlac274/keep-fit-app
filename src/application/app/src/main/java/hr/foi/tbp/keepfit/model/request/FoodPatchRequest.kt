package hr.foi.tbp.keepfit.model.request

import hr.foi.tbp.keepfit.model.response.FoodDetails

data class FoodPatchRequest(
    val id: Int,
    var name: String?,
    var calories: Double?,
    var nutrients : FoodDetails?
)
