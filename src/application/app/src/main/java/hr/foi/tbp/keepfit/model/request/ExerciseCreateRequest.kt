package hr.foi.tbp.keepfit.model.request

import com.google.gson.annotations.SerializedName
import hr.foi.tbp.keepfit.model.response.Exercise

data class ExerciseCreateRequest(
    val type: String,
    var name: String,
    var duration: Int,
    @SerializedName("calories_burned") val caloriesBurned: Double,
    var details : Exercise
)