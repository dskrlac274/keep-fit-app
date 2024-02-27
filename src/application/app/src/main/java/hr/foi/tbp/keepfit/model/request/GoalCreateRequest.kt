package hr.foi.tbp.keepfit.model.request

import hr.foi.tbp.keepfit.model.response.WeightGoal

data class GoalCreateRequest(
    val type: String,
    val goal: WeightGoal
)