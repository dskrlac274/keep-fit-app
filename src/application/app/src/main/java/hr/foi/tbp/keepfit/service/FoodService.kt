package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.FoodCreateRequest
import hr.foi.tbp.keepfit.model.request.FoodPatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.request.FoodLogCreateRequest
import hr.foi.tbp.keepfit.model.response.FoodLogResponse
import hr.foi.tbp.keepfit.model.response.FoodResponse
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

interface FoodService {
    @Headers("Accept: application/json")
    @DELETE("user/current/food-log/{id}")
    suspend fun deleteLog(
        @Header("Authorization") auth: String,
        @Path("id") foodLogId: String,
        @Query("quantity") quantity: String
    ): Response<Unit>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/food-log")
    suspend fun postLog(
        @Header("Authorization") auth: String,
        @Body foodLogCreateRequest: FoodLogCreateRequest
    ): Response<ApiResponse<FoodLogResponse>>

    @Headers("Accept: application/json")
    @GET("user/current/food")
    suspend fun get(
        @Header("Authorization") auth: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<FoodResponse>>>

    @Headers("Accept: application/json")
    @GET("user/current/food")
    suspend fun getByAll(
        @Header("Authorization") auth: String
    ): Response<ApiResponse<List<FoodResponse>>>

    @Headers("Accept: application/json")
    @GET("user/current/food-log")
    suspend fun getLogs(
        @Header("Authorization") auth: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<FoodLogResponse>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/food")
    suspend fun post(
        @Header("Authorization") auth: String,
        @Body foodCreateRequest: FoodCreateRequest
    ): Response<ApiResponse<FoodResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PATCH("user/current/food/{id}")
    suspend fun patch(
        @Header("Authorization") auth: String,
        @Path("id") foodId: String,
        @Body foodPatchRequest: FoodPatchRequest
    ): Response<ApiResponse<FoodResponse>>

    @Headers("Accept: application/json")
    @DELETE("user/current/food/{id}")
    suspend fun delete(
        @Header("Authorization") auth: String,
        @Path("id") foodId: String,
    ): Response<Unit>
}