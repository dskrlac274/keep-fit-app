package hr.foi.tbp.keepfit.deserializer

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import hr.foi.tbp.keepfit.model.response.FitnessGoal
import hr.foi.tbp.keepfit.model.response.Goal
import hr.foi.tbp.keepfit.model.response.NutrientGoal
import hr.foi.tbp.keepfit.model.response.WeightGoal
import java.lang.reflect.Type

class GoalSerializer : JsonSerializer<Goal> {
    override fun serialize(
        src: Goal,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return when (src) {
            is WeightGoal -> context.serialize(src, WeightGoal::class.java)
            is NutrientGoal -> context.serialize(src, NutrientGoal::class.java)
            is FitnessGoal -> context.serialize(src, FitnessGoal::class.java)
            else -> throw JsonParseException("Unknown type of goal")
        }
    }
}