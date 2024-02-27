package hr.foi.tbp.keepfit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.request.HealthCreateRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.HealthIndicatorsGraphResponse
import hr.foi.tbp.keepfit.model.response.HealthResponse
import hr.foi.tbp.keepfit.service.KeepFitService.healthService
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {
    private val _apiMessage: MutableLiveData<String> = MutableLiveData("")
    val apiMessage: LiveData<String> = _apiMessage
    private val _healthResponse = MutableLiveData<HealthResponse>()
    private val _healthResponseGraph = MutableLiveData<HealthIndicatorsGraphResponse>()
    val healthResponseGraph: LiveData<HealthIndicatorsGraphResponse> = _healthResponseGraph

    fun tryGetHealthGraphData(filter: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = healthService.getAllByFilter(Auth.authUserData!!.jwt, filter)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _healthResponseGraph.value = apiResponse?.data
                    Log.i("daniel", _healthResponseGraph.value.toString())
                    reportApiMessage(apiResponse!!.message)
                    onSucceed()
                }
                else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportApiMessage(errorResponse.message)
                    onFailed()
                }
            } catch (exception: Exception) {
                Log.i("daniel", exception.toString())
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryPostHealth(health: HealthCreateRequest, onFailed: () -> Unit, onSucceed: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = healthService.post(Auth.authUserData!!.jwt, health)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _healthResponse.value = apiResponse?.data
                    reportApiMessage(apiResponse!!.message)
                    onSucceed()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportApiMessage(errorResponse.message)
                    onFailed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    private fun reportApiMessage(message: String) {
        _apiMessage.value = message
    }
}