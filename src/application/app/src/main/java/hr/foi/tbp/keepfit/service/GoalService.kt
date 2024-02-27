package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.GoalCreateRequest
import hr.foi.tbp.keepfit.model.request.GoalPatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.Goal
import hr.foi.tbp.keepfit.model.response.GoalCoreResponse
import hr.foi.tbp.keepfit.model.response.GoalGetPostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GoalService {
    @Headers("Accept: application/json")
    @GET("user/current/goal")
    suspend fun get(@Header("Authorization") auth: String): Response<ApiResponse<GoalGetPostResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/goal")
    suspend fun post(
        @Header("Authorization") auth: String,
        @Body goalCreateRequest: GoalCreateRequest
    ): Response<ApiResponse<GoalGetPostResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PATCH("user/current/goal/{id}")
    @JvmSuppressWildcards
    suspend fun patch(
        @Header("Authorization") auth: String,
        @Path("id") goalId: String,
        @Body goalCreateRequest: GoalPatchRequest<Goal>
    ): Response<ApiResponse<GoalCoreResponse<Goal>>>
}