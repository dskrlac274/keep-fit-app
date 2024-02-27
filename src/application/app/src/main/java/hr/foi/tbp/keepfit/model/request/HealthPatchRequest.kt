package hr.foi.tbp.keepfit.model.request

import hr.foi.tbp.keepfit.model.response.HealthIndicators

data class HealthPatchRequest(
    val id: Int,
    val indicators: HealthIndicators
)
