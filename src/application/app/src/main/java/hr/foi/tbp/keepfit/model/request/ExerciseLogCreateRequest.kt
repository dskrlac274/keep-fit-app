package hr.foi.tbp.keepfit.model.request

import com.google.gson.annotations.SerializedName

data class ExerciseLogCreateRequest(
    @SerializedName("exercise_id") val exerciseId: Int,
    @SerializedName("created_at") val createdAt: String,
    var quantity: Int
)