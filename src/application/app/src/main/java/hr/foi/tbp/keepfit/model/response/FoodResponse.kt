package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName

interface Food

data class FoodResponse(
    var id: Int,
    @SerializedName("user_id") val userId: Int,
    var type: String,
    var name: String,
    var calories: Double,
    var nutrients : FoodDetails
) : Food

data class FoodLogResponse(
    var id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("food_id") val foodId: Int,
    var type: String,
    var name: String,
    var calories: Double,
    var quantity: Int,
    var nutrients : FoodDetails
) : Food


data class FoodDetails(
    var fat: Double?,
    var cholesterol: Double?,
    var sodium: Double?,
    var sugar: Double?,
    var protein: Double?,
    var carbohydrates: Double?,
)
