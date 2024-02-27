package hr.foi.tbp.keepfit.model.response

data class ApiResponse<T>(
    val data: T,
    val message: String,
    val success: Boolean,
    val statusCode: Int
)