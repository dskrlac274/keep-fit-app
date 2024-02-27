import FoodLogRepository from "../repository/FoodLogRepository.js"
import FoodRepository from "../repository/FoodRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import FoodLogSchemaValidator from "../validator/FoodLogSchemaValidator.js"

export default class FoodLogService {
    #foodLogServiceRepository
    #foodServiceRepository

    constructor() {
        this.#foodLogServiceRepository = new FoodLogRepository()
        this.#foodServiceRepository = new FoodRepository()
    }

    async create(foodLogData) {
        const { food_id } = foodLogData
        const validationError = FoodLogSchemaValidator.validateCreate(foodLogData)
        if (!validationError.success)
            return validationError

        const userFoodLog = await this.#foodServiceRepository.getById(food_id)

        if (!userFoodLog)
            return ResponseBuilder.createSuccessResponse([])

        const createdFoodLog = await this.#foodLogServiceRepository.insert(foodLogData)

        return ResponseBuilder.createSuccessResponse(createdFoodLog, 'Food log successfully added.')
    }

    async get(userId, date) {
        const userExercises = await this.#foodLogServiceRepository.getAllByUserIdAndDate(userId, date)

        if (userExercises.length == 0)
            return ResponseBuilder.createSuccessResponse([])

        return ResponseBuilder.createSuccessResponse(userExercises)
    }

    async delete(userId, id, quantity) {
        const userFoodLog = await this.#foodLogServiceRepository.getById(id)

        if (!userFoodLog)
            return ResponseBuilder.createSuccessResponse([])

        if (userFoodLog.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        if (quantity) {
            if (quantity > userFoodLog.quantity)
                return ResponseBuilder.createErrorResponse('Quantity not valid.')
        }
        else {
            quantity = userFoodLog.quantity
        }

        const deletedFoodLog = await this.#foodLogServiceRepository.delete(id, quantity)

        return ResponseBuilder.createSuccessResponse(deletedFoodLog, 'Food log successfully deleted.')
    }
}
