package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName


interface Goal

data class GoalGetPostResponse(
    @SerializedName("weight_goal") var weightGoal: GoalCoreResponse<Goal>,
    @SerializedName("nutrients_goal") var nutrientsGoal: GoalCoreResponse<Goal>,
    @SerializedName("fitness_goal") var fitnessGoal: GoalCoreResponse<Goal>
)

data class GoalCoreResponse<T>(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val type: String,
    var goal: T
)

data class WeightGoal(
    var age: Int,
    var sex: String,
    var height: Int,
    @SerializedName("goal_weight") var goalWeight: Int,
    @SerializedName("weekly_goal") var weeklyGoal: Float,
    @SerializedName("activity_level") var activityLevel: String,
    @SerializedName("current_weight") var currentWeight: Int,
    @SerializedName("daily_calories_intake") var dailyCaloriesIntake: Double
) : Goal

data class NutrientGoal(
    var fats: Double,
    var proteins: Double,
    var carbohydrates: Double
) : Goal

data class FitnessGoal(
    @SerializedName("daily_burned_calories_goal") var dailyBurnedCaloriesGoal: Int
) : Goal