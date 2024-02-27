import FoodLogService from "../service/FoodLogService.js"

export default class FoodLogController {
    #foodLogControllerService

    constructor() {
        this.#foodLogControllerService = new FoodLogService()
    }

    async get(req, res) {
        const response = await this.#foodLogControllerService.get(req.body.userId, req.query.date)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#foodLogControllerService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async delete(req, res) {
        const response = await this.#foodLogControllerService.delete(req.body.userId, req.params.id, req.query.quantity)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
