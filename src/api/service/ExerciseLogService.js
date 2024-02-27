import ExerciseLogRepository from "../repository/ExerciseLogRepository.js"
import ExerciseRepository from "../repository/ExerciseRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import ExerciseLogSchemaValidator from "../validator/ExerciseLogSchemaValidator.js"

export default class ExerciseLogService {
    #exerciseLogServiceRepository
    #exerciseServiceRepository

    constructor() {
        this.#exerciseLogServiceRepository = new ExerciseLogRepository()
        this.#exerciseServiceRepository = new ExerciseRepository()
    }

    async create(exerciseLogData) {
        const { exercise_id } = exerciseLogData
        const validationError = ExerciseLogSchemaValidator.validateCreate(exerciseLogData)
        if (!validationError.success)
            return validationError

        const userExerciseLog = await this.#exerciseServiceRepository.getById(exercise_id)

        if (!userExerciseLog)
            return ResponseBuilder.createSuccessResponse([])

        const createdExerciseLog = await this.#exerciseLogServiceRepository.insert(exerciseLogData)

        return ResponseBuilder.createSuccessResponse(createdExerciseLog, 'Exercise log successfully added.')
    }

    async get(userId, date) {
        const userExercises = await this.#exerciseLogServiceRepository.getAllByUserIdAndDate(userId, date)

        if (userExercises.length == 0)
            return ResponseBuilder.createSuccessResponse([])

        return ResponseBuilder.createSuccessResponse(userExercises)
    }

    async delete(userId, id, quantity) {
        const userExerciseLog = await this.#exerciseLogServiceRepository.getById(id)

        if (!userExerciseLog)
            return ResponseBuilder.createSuccessResponse([])

        if (userExerciseLog.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        if (!userExerciseLog)
            return ResponseBuilder.createErrorResponse('User exercise not found.', 404)

        if (quantity) {
            if (quantity > userExerciseLog.quantity)
                return ResponseBuilder.createErrorResponse('Quantity not valid.')
        }
        else {
            quantity = userExerciseLog.quantity
        }

        const deletedExerciseLog = await this.#exerciseLogServiceRepository.delete(id, quantity)

        return ResponseBuilder.createSuccessResponse(deletedExerciseLog, 'Food log successfully deleted.')
    }
}
