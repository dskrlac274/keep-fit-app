package hr.foi.tbp.keepfit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.request.FoodCreateRequest
import hr.foi.tbp.keepfit.model.request.FoodLogCreateRequest
import hr.foi.tbp.keepfit.model.request.FoodPatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.FoodLogResponse
import hr.foi.tbp.keepfit.model.response.FoodResponse
import hr.foi.tbp.keepfit.service.KeepFitService.foodService
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {
    private val _apiMessage: MutableLiveData<String> = MutableLiveData("")
    val apiMessage: LiveData<String> = _apiMessage
    private val _foodResponse = MutableLiveData<List<FoodLogResponse>>()
    val foodResponse: LiveData<List<FoodLogResponse>> = _foodResponse
    private val _foodHistoryResponse = MutableLiveData<List<FoodResponse>>()
    val foodHistoryResponse: LiveData<List<FoodResponse>> = _foodHistoryResponse

    fun tryGetFoodLog(date: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = foodService.getLogs(Auth.authUserData!!.jwt, date)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _foodResponse.value = apiResponse?.data
                    reportApiMessage(apiResponse!!.message)
                    onSucceed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryGetAllFoods(type: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = foodService.getByAll(Auth.authUserData!!.jwt)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _foodHistoryResponse.value = apiResponse?.data?.filter { it.type == type }
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

    fun tryPostLog(
        foodLog: FoodLogCreateRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = foodService.postLog(Auth.authUserData!!.jwt, foodLog)
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    val currentList = _foodResponse.value?.toMutableList() ?: mutableListOf()
                    val existingExercise =
                        currentList.find { it.foodId == apiResponse?.data?.foodId }

                    if (existingExercise != null) {
                        existingExercise.quantity += foodLog.quantity
                    } else {
                        currentList.add(apiResponse?.data!!)
                    }

                    _foodResponse.value = currentList

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

    fun tryPost(
        food: FoodCreateRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = foodService.post(Auth.authUserData!!.jwt, food)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
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

    fun tryPatch(
        food: FoodPatchRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response =
                    foodService.patch(Auth.authUserData!!.jwt, food.id.toString(), food)
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    val currentHistoryList =
                        _foodHistoryResponse.value?.toMutableList() ?: mutableListOf()
                    val indexOfElementHistory = currentHistoryList.indexOfFirst { it.id == food.id }

                    if (indexOfElementHistory != -1) {
                        currentHistoryList.removeAt(indexOfElementHistory)
                        currentHistoryList.add(indexOfElementHistory, response.body()!!.data)
                        _foodHistoryResponse.value = currentHistoryList

                        val currentResponseList =
                            _foodResponse.value?.toMutableList() ?: mutableListOf()
                        val indexOfElementResponse =
                            currentResponseList.indexOfFirst { it.foodId == food.id }

                        val foodLogResponse = FoodLogResponse(
                            id = currentResponseList[indexOfElementResponse].id,
                            foodId = apiResponse!!.data.id,
                            userId = apiResponse.data.userId,
                            type = apiResponse.data.type,
                            name = apiResponse.data.name,
                            calories = apiResponse.data.calories,
                            nutrients = apiResponse.data.nutrients,
                            quantity = currentResponseList[indexOfElementResponse].quantity
                        )
                        currentResponseList.removeAt(indexOfElementResponse)
                        currentResponseList.add(indexOfElementResponse, foodLogResponse)
                        _foodResponse.value = currentResponseList
                    }

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

    fun tryDeleteFood(id: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = foodService.delete(Auth.authUserData!!.jwt, id)
                if (response.isSuccessful) {
                    val mainList = _foodResponse.value ?: listOf()
                    val historyList = _foodHistoryResponse.value ?: listOf()

                    val updatedMainList = mainList.toMutableList()
                    updatedMainList.removeIf { foodLogResponse -> foodLogResponse.foodId == id.toInt() }
                    _foodResponse.value = updatedMainList

                    val updatedHistoryList = historyList.toMutableList()
                    updatedHistoryList.removeIf { foodResponse -> foodResponse.id == id.toInt() }
                    _foodHistoryResponse.value = updatedHistoryList
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

    fun tryDeleteFoodLog(
        id: Int,
        quantity: Int,
        onFailed: () -> Unit,
        onSucceed: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = foodService.deleteLog(
                    Auth.authUserData!!.jwt,
                    id.toString(),
                    quantity.toString()
                )
                if (response.isSuccessful) {
                    val list = _foodResponse.value?.toMutableList()
                    val exercise = list?.find { noteResponse -> noteResponse.id == id }

                    exercise?.let {
                        it.quantity -= quantity

                        if (it.quantity <= 0)
                            list.remove(it)

                        val updatedList = list.toList()
                        _foodResponse.value = updatedList
                        onSucceed()
                    }
                } else {
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

    private fun reportApiMessage(message: String) {
        _apiMessage.value = message
    }
}