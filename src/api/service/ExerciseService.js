import ExerciseRepository from "../repository/ExerciseRepository.js"
import GoalRepository from "../repository/GoalRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import ExerciseSchemaValidator from "../validator/ExerciseSchemaValidator.js"

export default class ExerciseService {
    #exerciseRepository

    constructor() {
        this.#exerciseRepository = new ExerciseRepository()
    }

    async create(exerciseData) {
        const validationError = ExerciseSchemaValidator.validateCreate(exerciseData)
        if (!validationError.success)
            return validationError

        const createdExerciseData = await this.#exerciseRepository.insert(exerciseData)

        return ResponseBuilder.createSuccessResponse(createdExerciseData, 'Exercise successfully added.')
    }

    async get(userId) {
        const userExercises = await this.#exerciseRepository.getAllByUserId(userId)

        if (userExercises.length == 0)
            return ResponseBuilder.createSuccessResponse([])

        return ResponseBuilder.createSuccessResponse(userExercises)
    }

    async patch(id, exerciseData) {
        const { userId } = exerciseData

        const userExercise = await this.#exerciseRepository.getById(id)
        if (!userExercise)
            return ResponseBuilder.createErrorResponse('Exercise does not exist.', 404)

        if (userExercise.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        exerciseData.type = userExercise.type
        const validationResponse = ExerciseSchemaValidator.validatePatch(exerciseData)
        if (!validationResponse.success)
            return validationResponse

        const updatedExercise = await this.#exerciseRepository.update(id, exerciseData)
        return ResponseBuilder.createSuccessResponse(updatedExercise, 'Resource successfully updated.')
    }

    async delete(userId, id) {
        const userExercise = await this.#exerciseRepository.getById(id)

        if (!userExercise)
            return ResponseBuilder.createErrorResponse('User exercise not found.', 404)

        if (userExercise.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        const deletedExercise = await this.#exerciseRepository.delete(id)

        return ResponseBuilder.createSuccessResponse(deletedExercise, 'Exercise successfully deleted.')
    }
}
