import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class HealthMetricsSchemaValidator {
    static #allowedKeys = ['heart_rate', 'blood_pressure', 'blood_glucose', 'respiration_rate', 'body_temperature']

    static validateCreate(indicators) {
        const keyValidationResponse = CoreSchemaValidator.validateKeys(indicators, this.#allowedKeys)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        const { heart_rate, blood_pressure, bmi, hours_of_sleep, body_temperature } = indicators

        if (typeof heart_rate !== 'number' || typeof blood_pressure !== 'string' ||
            typeof bmi !== 'number' || typeof hours_of_sleep !== 'number' || typeof body_temperature !== 'number')
            return ResponseBuilder.createErrorResponse('Invalid health metrics data.')

        return ResponseBuilder.createSuccessResponse()
    }
}
