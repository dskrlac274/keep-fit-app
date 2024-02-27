package hr.foi.tbp.keepfit.service

import com.google.gson.GsonBuilder
import hr.foi.tbp.keepfit.deserializer.ExerciseDeserializer
import hr.foi.tbp.keepfit.deserializer.ExerciseSerializer
import hr.foi.tbp.keepfit.deserializer.GoalDeserializer
import hr.foi.tbp.keepfit.deserializer.GoalSerializer
import hr.foi.tbp.keepfit.interceptor.AuthInterceptor
import hr.foi.tbp.keepfit.model.response.Exercise
import hr.foi.tbp.keepfit.model.response.Goal
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

object KeepFitService {
    private const val BASE_URL = "http://10.0.2.2:3000/api/v1/"

    private val gson = GsonBuilder()
        .registerTypeAdapter(Goal::class.java, GoalSerializer())
        .registerTypeAdapter(Goal::class.java, GoalDeserializer())
        .registerTypeAdapter(Exercise::class.java, ExerciseSerializer())
        .registerTypeAdapter(Exercise::class.java, ExerciseDeserializer())
        .create()
    private val instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .cookieJar(JavaNetCookieJar(CookieManager()))
                .build()
        )
        .build()

    val authService: AuthService = instance.create(AuthService::class.java)
    val userService: UserService = instance.create(UserService::class.java)
    val goalService: GoalService = instance.create(GoalService::class.java)
    val healthService: HealthService = instance.create(HealthService::class.java)
    val noteService: NoteService = instance.create(NoteService::class.java)
    val exerciseService: ExerciseService = instance.create(ExerciseService::class.java)
    val foodService: FoodService = instance.create(FoodService::class.java)
}