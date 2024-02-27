package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName

data class NoteResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val type: String,
    var description: String
)
