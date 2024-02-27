import jwt from 'jsonwebtoken'
import jwksClient from 'jwks-rsa'
import ResponseBuilder from '../response/ResponseBuilder.js'

export default class GoogleAuthService {
    #jwksUri
    #issuer
    #clientId
    #client

    constructor() {
        this.#jwksUri = process.env.GOOGLE_CERT
        this.#issuer = process.env.GOOGLE_ISSUER
        this.#clientId = process.env.GOOGLE_CLIENT_ID

        this.#client = jwksClient({
            jwksUri: this.#jwksUri
        });
    }

    async authenticate(token) {
        if (!token)
            return ResponseBuilder.createErrorResponse('Token not provided.')

        try {
            const decoded = await new Promise((resolve, reject) => {
                jwt.verify(token, async (header, callback) => {
                    try {
                        const key = await this.#getKey(header)
                        callback(null, key)
                    } catch (err) {
                        callback(err)
                    }
                }, {
                    issuer: this.#issuer,
                    audience: this.#clientId,
                    algorithms: ['RS256']
                }, (err, decoded) => err ? reject(err) : resolve(decoded))

            })
            return ResponseBuilder.createSuccessResponse(decoded)
        } catch {
            return ResponseBuilder.createErrorResponse('Access token not valid.', 401)
        }
    }

    async #getKey(header) {
        const key = await this.#client.getSigningKey(header.kid)
        const signingKey = key.getPublicKey()
        return signingKey
    }
}