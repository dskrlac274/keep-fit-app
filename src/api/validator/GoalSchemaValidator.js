import { GoalType } from '../enum/GoalType.js'
import { ActivityLevel } from '../enum/ActivityLevel.js'
import { SexType } from '../enum/SexType.js'
import ResponseBuilder from '../response/ResponseBuilder.js'
import CoreSchemaValidator from './CoreSchemaValidator.js'

export default class GoalSchemaValidator {
    static validateCreate(goalData) {
        const { type, goal } = goalData
        const { current_weight, goal_weight, weekly_goal, height, age, sex, activity_level, daily_calories_intake } = goal

        const keyValidationResponse = this.#validateKeys(goal, type)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        if (typeof current_weight !== 'number' || typeof goal_weight !== 'number' ||
            typeof height !== 'number' || typeof age !== 'number' ||
            typeof daily_calories_intake !== 'number' || ![-1, -0.5, -0.25, 0, 0.25, 0.5, 1].includes(weekly_goal) ||
            ![SexType.MALE, SexType.FEMALE].includes(sex) ||
            ![ActivityLevel.VERY_ACTIVE, ActivityLevel.ACTIVE, ActivityLevel.LIGHTLY_ACTIVE, ActivityLevel.NOT_VERY_ACTIVE].includes(activity_level) ||
            (current_weight < goal_weight && weekly_goal < 0) || (current_weight > goal_weight && weekly_goal > 0))
            return ResponseBuilder.createErrorResponse('Invalid weight goal data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static validatePatch(goalData) {
        const { type, goal } = goalData
        
        if (!goal || Object.keys(goal).length == 0)
            return ResponseBuilder.createErrorResponse('Invalid weight goal data.')

        const keyValidationResponse = this.#validateKeys(goal, type)
        if (!keyValidationResponse.success)
            return keyValidationResponse

        let validationResult
        switch (type) {
            case GoalType.WEIGHT:
                validationResult = this.#validateWeightPatchGoal(goal)
                break
            case GoalType.FITNESS:
                validationResult = this.#validateFitnessPatchGoal(goal)
                break
            case GoalType.NUTRITION:
                validationResult = this.#validateNutritionPatchGoal(goal)
                break
            default:
                return ResponseBuilder.createErrorResponse('Invalid goal type.')
        }

        return validationResult
    }

    static #validateWeightPatchGoal(goal) {
        const { current_weight, goal_weight, weekly_goal, height, age, sex, activity_level, daily_calories_intake } = goal

        if ((current_weight != null && typeof current_weight !== 'number') ||
            (goal_weight != null && typeof goal_weight !== 'number') ||
            (height != null && typeof height !== 'number') ||
            (age != null && typeof age !== 'number') ||
            (daily_calories_intake != null && typeof daily_calories_intake !== 'number') ||
            (weekly_goal != null && ![-1, -0.5, -0.25, 0, 0.25, 0.5, 1].includes(weekly_goal)) ||
            (sex != null && ![SexType.MALE, SexType.FEMALE].includes(sex)) ||
            (activity_level != null &&
                ![ActivityLevel.VERY_ACTIVE, ActivityLevel.ACTIVE, ActivityLevel.LIGHTLY_ACTIVE, ActivityLevel.NOT_VERY_ACTIVE].includes(activity_level)) ||
            (current_weight < goal_weight && weekly_goal < 0) || (current_weight > goal_weight && weekly_goal > 0))
            return ResponseBuilder.createErrorResponse('Invalid weight goal data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateFitnessPatchGoal(goal) {
        const { daily_burned_calories_goal } = goal

        if ((daily_burned_calories_goal != null && typeof daily_burned_calories_goal !== 'number'))
            return ResponseBuilder.createErrorResponse('Invalid fitnes goals data')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateNutritionPatchGoal(goal) {
        const { proteins, carbohydrates, fats } = goal

        if ((proteins != null && typeof proteins !== 'number') ||
            (carbohydrates != null && typeof carbohydrates !== 'number') ||
            (fats != null && typeof fats !== 'number'))
            return ResponseBuilder.createErrorResponse('Invalid nutrition goal data')

        return ResponseBuilder.createSuccessResponse()
    }

    static #validateKeys(goal, type) {
        const allowedKeysForGoalType = {
            [GoalType.WEIGHT]: ["age", "sex", "height", "goal_weight", "weekly_goal", "activity_level", "current_weight", "daily_calories_intake"],
            [GoalType.NUTRITION]: ["fats", "proteins", "carbohydrates"],
            [GoalType.FITNESS]: ["daily_burned_calories_goal"]
        }

        const allowedKeys = allowedKeysForGoalType[type] || []
        return CoreSchemaValidator.validateKeys(goal, allowedKeys)
    }
}
