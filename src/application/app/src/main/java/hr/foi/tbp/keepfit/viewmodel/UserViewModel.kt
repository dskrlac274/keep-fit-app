package hr.foi.tbp.keepfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.UserDataResponse
import hr.foi.tbp.keepfit.service.KeepFitService.userService

class UserViewModel : ViewModel() {
    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage
    private val _userDataResponse = MutableLiveData<UserDataResponse>()
    val userDataResponse: LiveData<UserDataResponse> = _userDataResponse

    suspend fun tryGetUserData(onFailedGetUserData: () -> Unit) {
        try {
            val response = userService.get(Auth.authUserData!!.jwt)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.success == true) {
                    _userDataResponse.value = apiResponse.data
                } else {
                    reportError(apiResponse?.message ?: "Unknown error")
                    onFailedGetUserData()
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                reportError(errorResponse.message)
                onFailedGetUserData()
            }
        } catch (exception: Exception) {
            reportError("Service currently not available")
            onFailedGetUserData()
        }
    }

    private fun reportError(message: String) {
        _errorMessage.value = message
    }
}