package hr.foi.tbp.keepfit.deserializer

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import hr.foi.tbp.keepfit.model.response.Exercise
import hr.foi.tbp.keepfit.model.response.ExerciseCardioDetails
import hr.foi.tbp.keepfit.model.response.ExerciseStrengthDetails
import java.lang.reflect.Type

class ExerciseSerializer: JsonSerializer<Exercise> {
    override fun serialize(
        src: Exercise,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return when (src) {
            is ExerciseCardioDetails -> context.serialize(src, ExerciseCardioDetails::class.java)
            is ExerciseStrengthDetails -> context.serialize(src, ExerciseStrengthDetails::class.java)
            else -> throw JsonParseException("Unknown type of goal")
        }
    }
}