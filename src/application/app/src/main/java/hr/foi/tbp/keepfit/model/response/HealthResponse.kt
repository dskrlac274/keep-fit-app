package hr.foi.tbp.keepfit.model.response

import com.google.gson.annotations.SerializedName


data class HealthResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    var indicators: HealthIndicators
)

data class HealthIndicators(
    @SerializedName("blood_glucose") var bloodGlucose: Double,
    @SerializedName("heart_rate") var heartRate: Int,
    @SerializedName("blood_pressure") var bloodPressure: String,
    @SerializedName("respiration_rate") var respirationRate: Int,
    @SerializedName("body_temperature") var bodyTemperature: Double
)

data class HealthIndicatorsGraphResponse(
    @SerializedName("blood_glucose") var bloodGlucose: List<HealthGraphValue>,
    @SerializedName("heart_rate") var heartRate: List<HealthGraphValue>,
    @SerializedName("blood_pressure") var bloodPressure: List<HealthGraphValueString>,
    @SerializedName("respiration_rate") var respirationRate: List<HealthGraphValue>,
    @SerializedName("body_temperature") var bodyTemperature: List<HealthGraphValue>,
    @SerializedName("current_weight") var currentWeight: List<HealthGraphValue>
)

data class HealthGraphValue(
    val date: String,
    val value: Number
)

data class HealthGraphValueString(
    val date: String,
    val value: String
)