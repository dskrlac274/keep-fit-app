package hr.foi.tbp.keepfit.helper

import hr.foi.tbp.keepfit.auth.AuthUser

class JwtHelper {
    companion object {
        fun isTokenValid(authUser: AuthUser?): Boolean {
            return authUser?.let {
                val currentTime = System.currentTimeMillis()
                it.jwtDateTo > currentTime
            } ?: false
        }
    }
}
