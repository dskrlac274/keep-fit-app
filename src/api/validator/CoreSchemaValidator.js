import ResponseBuilder from "../response/ResponseBuilder.js"

export default class CoreSchemaValidator {
    static isValidDate(variable) {
        if (typeof variable === 'string') {
            const date = new Date(variable)
            return !isNaN(date.getTime())
        } else if (variable instanceof Date) {
            return !isNaN(variable.getTime())
        } else {
            return false
        }
    }

    static validateKeys(data, allowedKeys) {
        allowedKeys.push("user_id")
        const goalKeys = Object.keys(data)
        const invalidKeys = goalKeys.filter(key => !allowedKeys.includes(key))

        if (invalidKeys.length > 0)
            return ResponseBuilder.createErrorResponse('Invalid data: contains disallowed keys.')

        return ResponseBuilder.createSuccessResponse()
    }
}
