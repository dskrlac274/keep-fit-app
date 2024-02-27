package hr.foi.tbp.keepfit.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import hr.foi.tbp.keepfit.model.response.Exercise
import hr.foi.tbp.keepfit.model.response.ExerciseCardioDetails
import hr.foi.tbp.keepfit.model.response.ExerciseStrengthDetails
import java.lang.reflect.Type

class ExerciseDeserializer : JsonDeserializer<Exercise> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Exercise {
        json.asJsonObject.let { jsonObject ->
            return when {
                jsonObject.has("distance") -> context.deserialize<ExerciseCardioDetails>(
                    json,
                    ExerciseCardioDetails::class.java
                )

                jsonObject.has("sets") -> context.deserialize<ExerciseStrengthDetails>(
                    json,
                    ExerciseStrengthDetails::class.java
                )

                else -> throw JsonParseException("Unknown type of goal")
            }
        }
    }
}