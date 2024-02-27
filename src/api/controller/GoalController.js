import GoalService from "../service/GoalService.js"

export default class GoalController {
    #goalService

    constructor() {
        this.#goalService = new GoalService()
    }

    async get(req, res) {
        const response = await this.#goalService.get(req.body.userId)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#goalService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async patch(req, res) {
        const response = await this.#goalService.patch(req.params.id, req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
