import AuthService from "../service/AuthService.js"
import GoogleAuthService from "../service/GoogleAuthService.js"

export default class AuthController {
    #googleAuthService
    #authService

    constructor() {
        this.#googleAuthService = new GoogleAuthService()
        this.#authService = new AuthService()
    }

    async login(req, res) {
        const token = req.body.token

        const authResponse = await this.#googleAuthService.authenticate(token)
        if (!authResponse.success)
            return res.status(401).json(authResponse)
        
        const response = await this.#authService.login(authResponse.data)
        return response.success
            ? res.status(response.statusCode).json(response)
            : res.status(response.statusCode).json(response)
    }
}