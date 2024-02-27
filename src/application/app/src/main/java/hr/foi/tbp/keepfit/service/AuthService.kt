package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.UserLoginRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest): Response<ApiResponse<UserLoginResponse>>
}