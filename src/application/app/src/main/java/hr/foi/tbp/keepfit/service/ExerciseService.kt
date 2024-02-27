package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.ExerciseCreateRequest
import hr.foi.tbp.keepfit.model.request.ExerciseLogCreateRequest
import hr.foi.tbp.keepfit.model.request.ExercisePatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.ExerciseLogResponse
import hr.foi.tbp.keepfit.model.response.ExerciseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseService {
    @Headers("Accept: application/json")
    @DELETE("user/current/exercise-log/{id}")
    suspend fun deleteLog(
        @Header("Authorization") auth: String,
        @Path("id") exerciseLogId: String,
        @Query("quantity") quantity: String
    ): Response<Unit>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/exercise-log")
    suspend fun postLog(
        @Header("Authorization") auth: String,
        @Body exerciseLogCreateRequest: ExerciseLogCreateRequest
    ): Response<ApiResponse<ExerciseLogResponse>>

    @Headers("Accept: application/json")
    @GET("user/current/exercise")
    suspend fun get(
        @Header("Authorization") auth: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<ExerciseResponse>>>

    @Headers("Accept: application/json")
    @GET("user/current/exercise")
    suspend fun getByAll(
        @Header("Authorization") auth: String
    ): Response<ApiResponse<List<ExerciseResponse>>>

    @Headers("Accept: application/json")
    @GET("user/current/exercise-log")
    suspend fun getLogs(
        @Header("Authorization") auth: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<ExerciseLogResponse>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/exercise")
    suspend fun post(
        @Header("Authorization") auth: String,
        @Body exerciseCreateRequest: ExerciseCreateRequest
    ): Response<ApiResponse<ExerciseResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PATCH("user/current/exercise/{id}")
    suspend fun patch(
        @Header("Authorization") auth: String,
        @Path("id") exerciseId: String,
        @Body exercisePatchRequest: ExercisePatchRequest
    ): Response<ApiResponse<ExerciseResponse>>

    @Headers("Accept: application/json")
    @DELETE("user/current/exercise/{id}")
    suspend fun delete(
        @Header("Authorization") auth: String,
        @Path("id") exerciseId: String,
    ): Response<Unit>
}