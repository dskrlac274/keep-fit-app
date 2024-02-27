package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.UserDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface UserService {
    @Headers("Accept: application/json")
    @GET("user/current")
    suspend fun get(@Header("Authorization") auth: String): Response<ApiResponse<UserDataResponse>>
}