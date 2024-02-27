package hr.foi.tbp.keepfit.auth

data class AuthUser(
    val id: Int,
    val jwt: String,
    val jwtDateTo: Long
)
