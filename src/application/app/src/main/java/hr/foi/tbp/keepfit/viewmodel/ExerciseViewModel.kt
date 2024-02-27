package hr.foi.tbp.keepfit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.request.ExerciseCreateRequest
import hr.foi.tbp.keepfit.model.request.ExerciseLogCreateRequest
import hr.foi.tbp.keepfit.model.request.ExercisePatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.ExerciseLogResponse
import hr.foi.tbp.keepfit.model.response.ExerciseResponse
import hr.foi.tbp.keepfit.service.KeepFitService.exerciseService
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {
    private val _apiMessage: MutableLiveData<String> = MutableLiveData("")
    val apiMessage: LiveData<String> = _apiMessage
    private val _exerciseResponse = MutableLiveData<List<ExerciseLogResponse>>()
    val exerciseResponse: LiveData<List<ExerciseLogResponse>> = _exerciseResponse
    private val _exerciseHistoryResponse = MutableLiveData<List<ExerciseResponse>>()
    val exerciseHistoryResponse: LiveData<List<ExerciseResponse>> = _exerciseHistoryResponse

    fun tryGetExercisesLog(date: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = exerciseService.getLogs(Auth.authUserData!!.jwt, date)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _exerciseResponse.value = apiResponse?.data
                    reportApiMessage(apiResponse!!.message)
                    onSucceed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryGetAllExercises(type: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = exerciseService.getByAll(Auth.authUserData!!.jwt)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _exerciseHistoryResponse.value = apiResponse?.data?.filter { it.type == type }
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
        exerciseLog: ExerciseLogCreateRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = exerciseService.postLog(Auth.authUserData!!.jwt, exerciseLog)
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    val currentList = _exerciseResponse.value?.toMutableList() ?: mutableListOf()
                    val existingExercise =
                        currentList.find { it.exerciseId == apiResponse?.data?.exerciseId }

                    if (existingExercise != null) {
                        existingExercise.quantity += exerciseLog.quantity
                    } else {
                        currentList.add(apiResponse?.data!!)
                    }

                    _exerciseResponse.value = currentList

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
        exercise: ExerciseCreateRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = exerciseService.post(Auth.authUserData!!.jwt, exercise)
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
        exercise: ExercisePatchRequest,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response =
                    exerciseService.patch(Auth.authUserData!!.jwt, exercise.id.toString(), exercise)
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    val currentHistoryList =
                        _exerciseHistoryResponse.value?.toMutableList() ?: mutableListOf()
                    val indexOfElementHistory =
                        currentHistoryList.indexOfFirst { it.id == exercise.id }

                    if (indexOfElementHistory != -1) {
                        currentHistoryList.removeAt(indexOfElementHistory)
                        currentHistoryList.add(indexOfElementHistory, response.body()!!.data)
                        _exerciseHistoryResponse.value = currentHistoryList

                        val currentResponseList =
                            _exerciseResponse.value?.toMutableList() ?: mutableListOf()
                        val indexOfElementResponse =
                            currentResponseList.indexOfFirst { it.exerciseId == exercise.id }

                        val exerciseLogResponse = ExerciseLogResponse(
                            id = currentResponseList[indexOfElementResponse].id,
                            exerciseId = apiResponse!!.data.id,
                            userId = apiResponse.data.userId,
                            type = apiResponse.data.type,
                            name = apiResponse.data.name,
                            caloriesBurned = apiResponse.data.caloriesBurned,
                            details = apiResponse.data.details,
                            duration = apiResponse.data.duration,
                            quantity = currentResponseList[indexOfElementResponse].quantity
                        )
                        currentResponseList.removeAt(indexOfElementResponse)
                        currentResponseList.add(indexOfElementResponse, exerciseLogResponse)
                        _exerciseResponse.value = currentResponseList
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

    fun tryDeleteExercise(id: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = exerciseService.delete(Auth.authUserData!!.jwt, id)
                if (response.isSuccessful) {
                    val list = _exerciseHistoryResponse.value?.toMutableList()
                    list?.removeIf { noteResponse -> noteResponse.id == id.toInt() }
                    _exerciseHistoryResponse.value = list

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

    fun tryDeleteExerciseLog(
        id: Int,
        quantity: Int,
        onFailed: () -> Unit,
        onSucceed: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = exerciseService.deleteLog(
                    Auth.authUserData!!.jwt,
                    id.toString(),
                    quantity.toString()
                )
                if (response.isSuccessful) {
                    val list = _exerciseResponse.value?.toMutableList()
                    val exercise = list?.find { noteResponse -> noteResponse.id == id }

                    exercise?.let {
                        it.quantity -= quantity

                        if (it.quantity <= 0)
                            list.remove(it)

                        val updatedList = list.toList()
                        _exerciseResponse.value = updatedList
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