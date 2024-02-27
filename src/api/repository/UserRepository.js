import Database from "../database/Database.js"

export default class UserRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getByEmail(email) {
        const sql = 'SELECT * FROM o_user WHERE email = $1'

        const result = await this.#db.query(sql, [email])
        return result.rows[0]
    }

    async getById(id) {
        const sql = 'SELECT * FROM o_user WHERE id = $1'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async insert(user) {
        const { email, name, image } = user
        const sql = `
          INSERT INTO o_user (email, name, image, created_at)
          VALUES ($1, $2, $3, NOW())
          RETURNING *;`

        const result = await this.#db.query(sql, [email, name, image])
        return result.rows[0]
    }
}
