package hr.foi.tbp.keepfit.model.request

import com.google.gson.annotations.SerializedName

data class NoteCreateRequest(
    val type: String,
    val description: String,
    @SerializedName("created_at") val createdAt: String
)
