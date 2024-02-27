import { FoodType } from "../enum/FoodType.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class FoodSchemaValidator {
    static #allowedKeys = ['fat', 'cholesterol', 'sodium', 'sugar', 'protein', 'carbohydrates']

    static validateCreate(foodData) {
        const { nutrients, calories, name, type } = foodData
        const { fat, cholesterol, sodium, sugar, protein, carbohydrates } = nutrients

        const keyValidationResponse = CoreSchemaValidator.validateKeys(nutrients, this.#allowedKeys)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if (typeof fat !== 'number' || typeof cholesterol !== 'number' ||
            typeof sodium !== 'number' || typeof sugar !== 'number' || typeof calories !== 'number' ||
            typeof protein !== 'number' || typeof carbohydrates !== 'number' || typeof name !== 'string' ||
            ![FoodType.BREAKFAST, FoodType.LUNCH, FoodType.DINNER].includes(type))
            return ResponseBuilder.createErrorResponse('Invalid food data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static validatePatch(foodData) {
        const { nutrients, calories, quantity, name } = foodData

        if (!nutrients)
            return ResponseBuilder.createSuccessResponse()

        const { fat, cholesterol, sodium, sugar, protein, carbohydrates } = nutrients

        const keyValidationResponse = CoreSchemaValidator.validateKeys(nutrients, this.#allowedKeys)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if (!nutrients)
            return ResponseBuilder.createSuccessResponse()

        if ((calories != null && typeof calories !== 'number') ||
            (name != null && typeof name !== 'string') ||
            (fat != null && typeof fat !== 'number') ||
            (cholesterol != null && typeof cholesterol !== 'number') ||
            (sodium != null && typeof sodium !== 'number') ||
            (sugar != null && typeof sugar !== 'number') ||
            (protein != null && typeof protein !== 'number') ||
            (carbohydrates != null && typeof carbohydrates !== 'number'))
            return ResponseBuilder.createErrorResponse('Invalid food data.')

        return ResponseBuilder.createSuccessResponse()
    }
}
