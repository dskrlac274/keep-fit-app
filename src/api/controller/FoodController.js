import FoodService from "../service/FoodService.js"

export default class FoodController {
    #foodService

    constructor() {
        this.#foodService = new FoodService()
    }

    async get(req, res) {
        const response = await this.#foodService.get(req.body.userId, req.query.date)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#foodService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async patch(req, res) {
        const response = await this.#foodService.patch(req.params.id, req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async delete(req, res) {
        const response = await this.#foodService.delete(req.body.userId, req.params.id)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
