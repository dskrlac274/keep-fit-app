package hr.foi.tbp.keepfit.model.request

import hr.foi.tbp.keepfit.model.response.HealthIndicators

data class HealthCreateRequest(
    val indicators: HealthIndicators
)