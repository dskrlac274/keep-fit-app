import UserService from "../service/UserService.js"

export default class UserController {
    #userService

    constructor() {
        this.#userService = new UserService()
    }

    async get(req, res) {
        const response = await this.#userService.get(req.body.userId)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}