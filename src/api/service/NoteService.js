import NoteRepository from "../repository/NoteRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import NoteSchemaValidator from "../validator/NoteSchemaValidator.js"

export default class NoteService {
    #noteRepository

    constructor() {
        this.#noteRepository = new NoteRepository()
    }

    async create(noteData) {
        const validationError = NoteSchemaValidator.validateCreate(noteData)
        if (!validationError.success)
            return validationError

        const alreadyExists = await this.#noteRepository.checkUniqueness(noteData)
        if (alreadyExists > 0)
            return ResponseBuilder.createErrorResponse("Note for type already added.")

        const createdNote = await this.#noteRepository.insert(noteData)

        return ResponseBuilder.createSuccessResponse(createdNote, 'Note successfully added.')
    }

    async get(userId, date) {
        const userNotes = await this.#noteRepository.getAllByUserIdAndDate(userId, date)

        if (userNotes.length == 0)
            return ResponseBuilder.createSuccessResponse([])

        return ResponseBuilder.createSuccessResponse(userNotes)
    }

    async getById(userId, id) {
        const userNote = await this.#noteRepository.getById(id)

        if (!userNote)
            return ResponseBuilder.createErrorResponse('User note not found.', 404)

        if (userNote.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to access this resource.', 401)

        return ResponseBuilder.createSuccessResponse(userNote)
    }

    async patch(id, noteData) {
        const { userId } = noteData

        const userNote = await this.#noteRepository.getById(id)
        if (!userNote)
            return ResponseBuilder.createErrorResponse('User note not found.', 404)

        if (userNote.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        const validationError = NoteSchemaValidator.validatePatch(noteData)
        if (!validationError.success)
            return validationError

        const updatedNote = await this.#noteRepository.update(id, noteData)


        return ResponseBuilder.createSuccessResponse(updatedNote, 'Resource successfully updated.')
    }

    async delete(userId, id) {
        const userNotes = await this.#noteRepository.getById(id)

        if (!userNotes)
            return ResponseBuilder.createErrorResponse('User note not found.', 404)

        if (userNotes.user_id != userId)
            return ResponseBuilder.createErrorResponse('Unauthorized to change this resource.', 401)

        const deletedNote = await this.#noteRepository.delete(id)

        return ResponseBuilder.createSuccessResponse(deletedNote, 'Note successfully deleted.')
    }
}
