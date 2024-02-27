import ExerciseService from "../service/ExerciseService.js"

export default class ExerciseController {
    #exerciseService

    constructor() {
        this.#exerciseService = new ExerciseService()
    }

    async get(req, res) {
        const response = await this.#exerciseService.get(req.body.userId)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#exerciseService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async patch(req, res) {
        const response = await this.#exerciseService.patch(req.params.id, req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async delete(req, res) {
        const response = await this.#exerciseService.delete(req.body.userId, req.params.id, req.query.quantity)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
