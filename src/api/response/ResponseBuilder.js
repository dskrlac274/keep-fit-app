import AppResponse from './AppResponse.js'

export default class ResponseBuilder {
    static createSuccessResponse = (data = null, message = '', statusCode = 200) => new AppResponse(data, message, true, statusCode)

    static createErrorResponse = (message = '', statusCode = 400) => new AppResponse(null, message, false, statusCode)
}
