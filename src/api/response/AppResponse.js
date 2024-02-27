export default class AppResponse {
    constructor(data, message = '', success = true, statusCode) {
        this.data = data
        this.message = message
        this.success = success
        this.statusCode = statusCode
    }
}
