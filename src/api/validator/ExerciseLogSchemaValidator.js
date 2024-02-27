import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class ExerciseLogSchemaValidator {
    static validateCreate(foodLogData) {
        const { exercise_id, created_at, quantity } = foodLogData

        if (typeof exercise_id !== 'number' || typeof quantity !== 'number' || !CoreSchemaValidator.isValidDate(created_at))
            return ResponseBuilder.createErrorResponse('Invalid exercise log data.')

        return ResponseBuilder.createSuccessResponse()
    }
}
