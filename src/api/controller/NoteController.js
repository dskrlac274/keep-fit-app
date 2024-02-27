import NoteService from "../service/NoteService.js"

export default class NoteController {
    #noteService

    constructor() {
        this.#noteService = new NoteService()
    }

    async get(req, res) {
        const response = await this.#noteService.get(req.body.userId, req.query.date)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async getById(req, res) {
        const response = await this.#noteService.getById(req.body.userId, req.params.id)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async create(req, res) {
        const response = await this.#noteService.create(req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async patch(req, res) {
        const response = await this.#noteService.patch(req.params.id, req.body)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }

    async delete(req, res) {
        const response = await this.#noteService.delete(req.body.userId, req.params.id)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}
