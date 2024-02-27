package hr.foi.tbp.keepfit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.request.GoalCreateRequest
import hr.foi.tbp.keepfit.model.request.GoalPatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.FitnessGoal
import hr.foi.tbp.keepfit.model.response.Goal
import hr.foi.tbp.keepfit.model.response.GoalGetPostResponse
import hr.foi.tbp.keepfit.model.response.NutrientGoal
import hr.foi.tbp.keepfit.model.response.WeightGoal
import hr.foi.tbp.keepfit.service.KeepFitService.goalService
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {
    private val _apiMessage: MutableLiveData<String> = MutableLiveData("")
    val apiMessage: LiveData<String> = _apiMessage
    private val _goalGetPostResponse = MutableLiveData<GoalGetPostResponse>()
    val goalGetPostResponse: LiveData<GoalGetPostResponse> = _goalGetPostResponse

    suspend fun tryGetGoal(onFailed: () -> Unit, onSucceed: () -> Unit) {
        try {
            val response = goalService.get(Auth.authUserData!!.jwt)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                _goalGetPostResponse.value = apiResponse?.data
            }
            onSucceed()
        } catch (exception: Exception) {
            reportApiMessage("Service currently not available")
            onFailed()
        }
    }

    fun tryPostGoal(goal: GoalCreateRequest, onFailed: () -> Unit, onSucceed: () -> Unit) {
        viewModelScope.launch {
            try {
                goal.goal.dailyCaloriesIntake = calculateDailyCalories(goal.goal)
                val response = goalService.post(Auth.authUserData!!.jwt, goal)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _goalGetPostResponse.value = apiResponse?.data
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

    fun tryPatchGoal(
        goal: GoalPatchRequest<Goal>,
        onFailed: () -> Unit,
        onSucceed: () -> Unit
    ) {
        if (goal.goal is WeightGoal) {
            val weightGoal = goal.goal
            weightGoal.dailyCaloriesIntake = calculateDailyCalories(weightGoal)
        }

        viewModelScope.launch {
            try {
                val response = goalService.patch(Auth.authUserData!!.jwt, goal.id.toString(), goal)
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    when (val goalResponse = apiResponse?.data?.goal) {
                        is WeightGoal -> _goalGetPostResponse.value?.weightGoal?.goal = goalResponse
                        is NutrientGoal -> _goalGetPostResponse.value?.nutrientsGoal?.goal =
                            goalResponse

                        is FitnessGoal -> _goalGetPostResponse.value?.fitnessGoal?.goal =
                            goalResponse
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
                Log.i("daniel", exception.toString())
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    private fun calculateDailyCalories(goalData: WeightGoal): Double {
        val bmr = if (goalData.sex == "M") {
            88.362 + (13.397 * goalData.currentWeight) + (4.799 * goalData.height) - (5.677 * goalData.age)
        } else {
            447.593 + (9.247 * goalData.currentWeight) + (3.098 * goalData.height) - (4.330 * goalData.age)
        }

        val weightChangeCalories = goalData.weeklyGoal * 7700 / 7

        return (bmr * ActivityLevel.fromString(goalData.activityLevel)) - weightChangeCalories
    }

    enum class ActivityLevel(val multiplier: Double, val readableName: String) {
        NOT_VERY_ACTIVE(1.2, "Not very active"),
        LIGHTLY_ACTIVE(1.375, "Lightly active"),
        ACTIVE(1.55, "Active"),
        VERY_ACTIVE(1.725, "Very active");

        companion object {
            fun fromString(level: String): Double {
                return values().find {
                    it.readableName.equals(
                        level,
                        ignoreCase = true
                    )
                }?.multiplier
                    ?: NOT_VERY_ACTIVE.multiplier
            }
        }
    }

    private fun reportApiMessage(message: String) {
        _apiMessage.value = message
    }
}