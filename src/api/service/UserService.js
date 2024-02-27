import UserRepository from "../repository/UserRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"

export default class UserService {
    #userRepository

    constructor() {
        this.#userRepository = new UserRepository()
    }

    async get(id) {
        const user = await this.#userRepository.getById(id)

        if (!user)
            ResponseBuilder.createErrorResponse('User not found.', 404)

        return ResponseBuilder.createSuccessResponse(user)
    }
}
