package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName

data class UserDataResponse(
    val id: Int,
    val email: String,
    val name: String,
    val image: String,
    @SerializedName("created_at") val createdAt: String
)