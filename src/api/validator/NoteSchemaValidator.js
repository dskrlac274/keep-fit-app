import { NoteType } from "../enum/NoteType.js"
import ResponseBuilder from "../response/ResponseBuilder.js"
import CoreSchemaValidator from "./CoreSchemaValidator.js"

export default class NoteSchemaValidator {
    static validateCreate(noteData) {
        const { created_at, type, description } = noteData

        if (!CoreSchemaValidator.isValidDate(created_at) || typeof description !== 'string' ||
            ![NoteType.FOOD, NoteType.EXERCISE].includes(type))
            return ResponseBuilder.createErrorResponse('Invalid health metrics data.')

        return ResponseBuilder.createSuccessResponse()
    }

    static validatePatch(noteData) {
        if (!noteData)
            return ResponseBuilder.createSuccessResponse()

        const { description } = noteData

        if (description != null && typeof description !== 'string')
            return ResponseBuilder.createErrorResponse('Invalid health metrics data.')

        return ResponseBuilder.createSuccessResponse()
    }
}
