import jwt from 'jsonwebtoken'
import ResponseBuilder from '../response/ResponseBuilder.js'

export default (req, res, next) => {
    if (req.headers.authorization) {
        try {
            const token = req.headers.authorization.split(' ')[1]
            const decoded = jwt.verify(token, process.env.JWT_SECRET)
            req.body.userId = decoded.userId

            return next()
        } catch(err) {
            const response = ResponseBuilder.createErrorResponse('Access token not valid.', 401)
            return res.status(response.statusCode).send(response)
        }
    } else {
        const response = ResponseBuilder.createErrorResponse('Authorization header is missing.', 401)
        return res.status(response.statusCode).send(response)
    }
}
