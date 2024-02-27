package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName

interface Exercise

data class ExerciseResponse(
    var id: Int,
    @SerializedName("user_id") val userId: Int,
    var type: String,
    var name: String,
    var duration: Int,
    @SerializedName("calories_burned") val caloriesBurned: Double,
    var details : Exercise
)

data class ExerciseLogResponse(
    var id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("exercise_id") val exerciseId: Int,
    var type: String,
    var name: String,
    var duration: Int,
    @SerializedName("calories_burned") val caloriesBurned: Double,
    var quantity: Int,
    var details : Exercise,
)

data class ExerciseCardioDetails(
    var distance: Double
) : Exercise

data class ExerciseStrengthDetails(
    var sets: Int,
    @SerializedName("reps_per_set") var repsPerSet: Int,
    @SerializedName("lift_weight") var liftWeight: Double
) : Exercise