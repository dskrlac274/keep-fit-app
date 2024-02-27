import GoalRepository from "../repository/GoalRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import { GoalType } from '../enum/GoalType.js'
import GoalSchemaValidator from "../validator/GoalSchemaValidator.js"

export default class GoalService {
    #goalRepository

    constructor() {
        this.#goalRepository = new GoalRepository()
    }

    async create(goalData) {
        const { goal, type, userId } = goalData

        const validationInsertResponse = GoalSchemaValidator.validateCreate(goalData)
        if (!validationInsertResponse.success)
            return validationInsertResponse

        if (await this.#goalRepository.checkTypesUniqueness(userId, type) > 0)
            return ResponseBuilder.createErrorResponse('Given type already exists.')

        const { daily_calories_intake } = goal
        const nutrientsGoalData = {
            proteins: ((daily_calories_intake * 0.15) / 4),
            carbohydrates: ((daily_calories_intake * 0.55) / 4),
            fats: ((daily_calories_intake * 0.30) / 9)
        }

        const exerciseGoalData = {
            daily_burned_calories_goal: 0,
        }

        const createdWeightGoal = await this.#goalRepository.insert(goalData)
        goalData.goal = nutrientsGoalData
        goalData.type = GoalType.NUTRITION
        const createdNutirentsGoal = await this.#goalRepository.insert(goalData)
        goalData.goal = exerciseGoalData
        goalData.type = GoalType.FITNESS
        const createdFitnessGoal = await this.#goalRepository.insert(goalData)

        const createdGoals = {
            weight_goal: createdWeightGoal,
            nutrients_goal: createdNutirentsGoal,
            fitness_goal: createdFitnessGoal
        }

        return ResponseBuilder.createSuccessResponse(createdGoals, 'Goal successfully created.')
    }

    async get(userId) {
        const userGoals = await this.#goalRepository.getAllByUserId(userId)

        if (userGoals.length == 0)
            return ResponseBuilder.createErrorResponse({})

            const fetchedGoals = {
                weight_goal: null,
                nutrients_goal: null,
                fitness_goal: null
            }
            
            userGoals.forEach(goal => {
                if (goal.type == GoalType.WEIGHT) {
                    fetchedGoals.weight_goal = goal
                } else if (goal.type == GoalType.NUTRITION) {
                    fetchedGoals.nutrients_goal = goal
                } else if (goal.type == GoalType.FITNESS) {
                    fetchedGoals.fitness_goal = goal
                }
            })

        return ResponseBuilder.createSuccessResponse(fetchedGoals)
    }

    async patch(id, goalData) {
        const { userId, goal } = goalData

        const userGoal = await this.#goalRepository.getById(id)
        if (!userGoal)
            return ResponseBuilder.createErrorResponse('Goal does not exist.', 404)

        if (userGoal.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        goalData.type = userGoal.type
        const validationResponse = GoalSchemaValidator.validatePatch(goalData)
        if (!validationResponse.success)
            return validationResponse

        userGoal.goal = await this.#goalRepository.update(id, goal)
        return ResponseBuilder.createSuccessResponse(userGoal, 'Resource successfully updated.')
    }
}
