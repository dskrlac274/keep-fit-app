import HealthIndicatorService from "../service/HealthIndicatorService.js"

export default class HealthIndicatorController {
    #healthIndicatorService

    constructor() {
        this.#healthIndicatorService = new HealthIndicatorService()
    }

    async get(req, res) {
        const response = await this.#healthIndicatorService.get(req.body.userId, req.query.filters)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#healthIndicatorService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
