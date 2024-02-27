import { ExerciseType } from "../enum/ExerciseType.js"
import { IntensityLevel } from "../enum/IntensityLevel.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class ExerciseSchemaValidator {
    static validateCreate(exerciseData) {
        const { type, details, duration, name, calories_burned } = exerciseData

        if (typeof duration !== 'number' || typeof name !== 'string' || typeof calories_burned !== 'number' ||
            ![ExerciseType.CARDIOVASCULAR, ExerciseType.STRENGTH].includes(type))
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        const validationResult = type == ExerciseType.CARDIOVASCULAR ?
            this.#validateCardioCreate(details) : this.#validateStrengthCreate(details)

        return validationResult
    }

    static validatePatch(exerciseData) {
        const { type, details, duration, name, calories_burned } = exerciseData

        if ((duration != null && typeof duration !== 'number') || (name != null && typeof name !== 'string') ||
            (calories_burned != null && typeof calories_burned !== 'number'))
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        if (!details)
            return ResponseBuilder.createSuccessResponse()

        const validationResult = type == ExerciseType.CARDIOVASCULAR ?
            this.#validateCardioPatch(details) : this.#validateStrengthPatch(details)

        return validationResult
    }

    static #validateCardioPatch(details) {
        const { distance, intensity_level } = details

        const keyValidationResponse = this.#validateKeys(details, ExerciseType.CARDIOVASCULAR)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if ((distance != null && typeof distance !== 'number') ||
            (intensity_level != null &&
                ![IntensityLevel.HIGH, IntensityLevel.MODERATE, IntensityLevel.LOW].includes(intensity_level)))
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateStrengthPatch(details) {
        const { sets, reps_per_set, lift_weight } = details

        const keyValidationResponse = this.#validateKeys(details, ExerciseType.STRENGTH)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if ((sets != null && typeof sets !== 'number') || (reps_per_set != null && typeof reps_per_set !== 'number') ||
            (lift_weight != null && typeof lift_weight !== 'number'))
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateCardioCreate(details) {
        const { distance } = details

        const keyValidationResponse = this.#validateKeys(details, ExerciseType.CARDIOVASCULAR)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if (typeof distance !== 'number')
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateStrengthCreate(details) {
        const { sets, reps_per_set, lift_weight } = details

        const keyValidationResponse = this.#validateKeys(details, ExerciseType.STRENGTH)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if (typeof sets !== 'number' || typeof reps_per_set !== 'number' || typeof lift_weight !== 'number')
            return ResponseBuilder.createErrorResponse('Invalid exercise data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateKeys(details, type) {
        const allowedKeysForExerciseType = {
            [ExerciseType.CARDIOVASCULAR]: ['distance'],
            [ExerciseType.STRENGTH]: ['sets', 'reps_per_set', 'lift_weight']
        }

        const allowedKeys = allowedKeysForExerciseType[type] || []
        const goalKeys = Object.keys(details)
        const invalidKeys = goalKeys.filter(key => !allowedKeys.includes(key))

        if (invalidKeys.length > 0)
            return ResponseBuilder.createErrorResponse('Invalid data: contains disallowed keys.')

        return ResponseBuilder.createSuccessResponse()
    }
}
