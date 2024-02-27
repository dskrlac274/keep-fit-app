import ExerciseLogService from "../service/ExerciseLogService.js"

export default class ExerciseLogController {
    #exerciseLogControllerService

    constructor() {
        this.#exerciseLogControllerService = new ExerciseLogService()
    }

    async get(req, res) {
        const response = await this.#exerciseLogControllerService.get(req.body.userId, req.query.date)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#exerciseLogControllerService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async delete(req, res) {
        const response = await this.#exerciseLogControllerService.delete(req.body.userId, req.params.id, req.query.quantity)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
