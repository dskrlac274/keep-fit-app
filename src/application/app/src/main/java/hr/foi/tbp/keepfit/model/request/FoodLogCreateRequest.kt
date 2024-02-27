package hr.foi.tbp.keepfit.model.request

import com.google.gson.annotations.SerializedName

data class FoodLogCreateRequest(
    @SerializedName("food_id") val foodId: Int,
    @SerializedName("created_at") val createdAt: String,
    var quantity: Int
)