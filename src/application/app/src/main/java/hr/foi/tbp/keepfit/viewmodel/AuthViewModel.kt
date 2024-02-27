package hr.foi.tbp.keepfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.auth.AuthUser
import hr.foi.tbp.keepfit.model.request.UserLoginRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.service.KeepFitService.authService
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun tryAuthenticate(token: String, onSuccessfulLogin: () -> Unit, onFailedLogin: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = authService.login(UserLoginRequest(token))
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        handleResponse(apiResponse.data.token, onSuccessfulLogin, onFailedLogin)
                    } else {
                        reportError(apiResponse?.message ?: "Unknown error")
                        onFailedLogin()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportError(errorResponse.message)
                    onFailedLogin()
                }
            } catch (exception: Exception) {
                reportError("Service currently not available")
                onFailedLogin()
            }
        }
    }

    private fun handleResponse(
        jwtToken: String?,
        onSuccessfulLogin: () -> Unit,
        onFailedLogin: () -> Unit
    ) {
        jwtToken?.let { token ->
            parseJwt(token)?.let {
                Auth.authUserData = it
                onSuccessfulLogin()
            } ?: onFailedLogin()
        } ?: onFailedLogin()
    }

    private fun parseJwt(jwtToken: String): AuthUser? {
        val jwt = JWT(jwtToken)
        val userId = jwt.getClaim("userId").asInt() ?: return null
        val expiresAt = jwt.expiresAt?.time ?: return null
        return AuthUser(userId, jwtToken, expiresAt)
    }

    private fun reportError(message: String) {
        _errorMessage.value = message
    }
}