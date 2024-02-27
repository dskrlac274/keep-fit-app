import FoodRepository from "../repository/FoodRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import FoodSchemaValidator from "../validator/FoodSchemaValidator.js"

export default class FoodService {
    #foodRepository

    constructor() {
        this.#foodRepository = new FoodRepository()
    }

    async create(foodData) {
        const validationError = FoodSchemaValidator.validateCreate(foodData)
        if (!validationError.success)
            return validationError

        const createdFoodData = await this.#foodRepository.insert(foodData)

        return ResponseBuilder.createSuccessResponse(createdFoodData, 'Food successfully added.')
    }

    async get(userId) {
        const userFood = await this.#foodRepository.getAllByUserId(userId)

        if (userFood.length == 0)
            return ResponseBuilder.createSuccessResponse([])

        return ResponseBuilder.createSuccessResponse(userFood)
    }

    async patch(id, foodData) {
        const { userId } = foodData

        const userFood = await this.#foodRepository.getById(id)
        if (!userFood)
            return ResponseBuilder.createErrorResponse('Food does not exist.', 404)

        if (userFood.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        foodData.type = userFood.type
        const validationResponse = FoodSchemaValidator.validatePatch(foodData)
        if (!validationResponse.success)
            return validationResponse

        const updatedFood = await this.#foodRepository.update(id, foodData)
        return ResponseBuilder.createSuccessResponse(updatedFood, 'Resource successfully updated.')
    }

    async delete(userId, id) {
        const userFood = await this.#foodRepository.getById(id)

        if (!userFood)
            return ResponseBuilder.createSuccessResponse([])

        if (userFood.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        const deletedFood = await this.#foodRepository.delete(id)

        return ResponseBuilder.createSuccessResponse(deletedFood, 'Food successfully deleted.')
    }
}
