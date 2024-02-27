package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.HealthCreateRequest
import hr.foi.tbp.keepfit.model.request.HealthPatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.HealthIndicatorsGraphResponse
import hr.foi.tbp.keepfit.model.response.HealthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HealthService {
    @Headers("Accept: application/json")
    @GET("user/current/health-indicator")
    suspend fun getAllByFilter(
        @Header("Authorization") auth: String,
        @Query("filters") filters: String? = null
    ): Response<ApiResponse<HealthIndicatorsGraphResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/health-indicator")
    suspend fun post(
        @Header("Authorization") auth: String,
        @Body healthCreateRequest: HealthCreateRequest
    ): Response<ApiResponse<HealthResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PATCH("user/current/health-indicator/{id}")
    suspend fun patch(
        @Header("Authorization") auth: String,
        @Path("id") goalId: String,
        @Body healthPatchRequest: HealthPatchRequest
    ): Response<ApiResponse<HealthResponse>>
}