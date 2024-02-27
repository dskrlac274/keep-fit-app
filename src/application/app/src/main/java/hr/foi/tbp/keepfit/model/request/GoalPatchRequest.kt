package hr.foi.tbp.keepfit.model.request

data class GoalPatchRequest<T>(
    val id: Int,
    val goal: T
)