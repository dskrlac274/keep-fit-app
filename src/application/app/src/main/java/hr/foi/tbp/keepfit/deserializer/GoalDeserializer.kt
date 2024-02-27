package hr.foi.tbp.keepfit.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import hr.foi.tbp.keepfit.model.response.FitnessGoal
import hr.foi.tbp.keepfit.model.response.Goal
import hr.foi.tbp.keepfit.model.response.NutrientGoal
import hr.foi.tbp.keepfit.model.response.WeightGoal
import java.lang.reflect.Type

class GoalDeserializer : JsonDeserializer<Goal> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Goal {
        json.asJsonObject.let { jsonObject ->
            return when {
                jsonObject.has("age") -> context.deserialize<WeightGoal>(
                    json,
                    WeightGoal::class.java
                )

                jsonObject.has("fats") -> context.deserialize<NutrientGoal>(
                    json,
                    NutrientGoal::class.java
                )

                jsonObject.has("daily_burned_calories_goal") -> context.deserialize<FitnessGoal>(
                    json,
                    FitnessGoal::class.java
                )

                else -> throw JsonParseException("Unknown type of goal")
            }
        }
    }
}