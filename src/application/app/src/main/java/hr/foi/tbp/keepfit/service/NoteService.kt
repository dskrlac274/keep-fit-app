package hr.foi.tbp.keepfit.service

import hr.foi.tbp.keepfit.model.request.NoteCreateRequest
import hr.foi.tbp.keepfit.model.request.NotePatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.NoteResponse
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

interface NoteService {
    @Headers("Accept: application/json")
    @GET("user/current/note")
    suspend fun get(
        @Header("Authorization") auth: String,
        @Query("date") date: String
    ): Response<ApiResponse<List<NoteResponse>>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("user/current/note")
    suspend fun post(
        @Header("Authorization") auth: String,
        @Body noteCreateRequest: NoteCreateRequest
    ): Response<ApiResponse<NoteResponse>>

    @Headers("Accept: application/json", "Content-Type: application/json")
    @PATCH("user/current/note/{id}")
    suspend fun patch(
        @Header("Authorization") auth: String,
        @Path("id") noteId: String,
        @Body notePatchRequest: NotePatchRequest
    ): Response<ApiResponse<NoteResponse>>

    @Headers("Accept: application/json")
    @DELETE("user/current/note/{id}")
    suspend fun delete(
        @Header("Authorization") auth: String,
        @Path("id") noteId: String,
    ): Response<Unit>
}