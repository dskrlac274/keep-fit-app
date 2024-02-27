import jwt from 'jsonwebtoken'

export default class JwtHelper {
    #jwtSecret
    #jwtTimeout
    
    constructor() {
        this.#jwtSecret = process.env.JWT_SECRET
        this.#jwtTimeout = process.env.JWT_TIMEOUT
    }

    createJwt(userId) {
        const payload = {
            userId: userId
        }

        const options = {
            expiresIn: this.#jwtTimeout,
            algorithm: 'HS256'
        };

        return jwt.sign(payload, this.#jwtSecret, options)
    }
}