import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class FoodLogSchemaValidator {
    static validateCreate(foodLogData) {
        const { food_id, created_at, quantity } = foodLogData

        if (typeof food_id !== 'number' || typeof quantity !== 'number' || !CoreSchemaValidator.isValidDate(created_at))
            return ResponseBuilder.createErrorResponse('Invalid food log data.')

        return ResponseBuilder.createSuccessResponse()
    }
}
