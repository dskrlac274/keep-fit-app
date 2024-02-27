package hr.foi.tbp.keepfit.auth

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUserManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AuthUserSharedPreferences", Context.MODE_PRIVATE)

    fun saveAuthUserData() {
        sharedPreferences.edit().apply {
            putString("id", Auth.authUserData!!.id.toString())
            putString("jwt", Auth.authUserData!!.jwt)
            putString("jwtDateTo", Auth.authUserData!!.jwtDateTo.toString())
            apply()
        }
    }

    fun getAuthUserData(): AuthUser? {
        val id = sharedPreferences.getString("id", null)?.toIntOrNull()
        val jwt = sharedPreferences.getString("jwt", null)
        val jwtDateTo = sharedPreferences.getString("jwtDateTo", null)?.toLongOrNull()

        return if (id != null && jwt != null && jwtDateTo != null) {
            AuthUser(id, jwt, jwtDateTo)
        } else {
            null
        }
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}