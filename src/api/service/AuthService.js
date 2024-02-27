import JwtHelper from '../helper/JwtHelper.js'
import UserRepository from '../repository/UserRepository.js'
import ResponseBuilder from '../response/ResponseBuilder.js'

export default class AuthService {
    #jwtHelper
    #userRepository

    constructor() {
        this.#userRepository = new UserRepository()
        this.#jwtHelper = new JwtHelper()
    }

    async login(userGoogleData) {
        const userEmail = userGoogleData.email
        const userName = userGoogleData.name
        const userImage = userGoogleData.picture

        if (!userEmail)
            return ResponseBuilder.createErrorResponse("Email can't be empty.")

        if (!userName)
            return ResponseBuilder.createErrorResponse("Name can't be empty.")

        let user = await this.#userRepository.getByEmail(userEmail)
        if (!user) {
            const createUser = {
                email: userEmail,
                name: userName,
                image: userImage
            }
            user = await this.#userRepository.insert(createUser)
        }

        const jwtToken = this.#jwtHelper.createJwt(user.id)

        return ResponseBuilder.createSuccessResponse({ token: jwtToken }, 'Login successful')
    }
}