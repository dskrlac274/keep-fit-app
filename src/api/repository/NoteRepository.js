import Database from "../database/Database.js"

export default class NoteRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getById(id) {
        const sql = 'SELECT * FROM note WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async getAllByUserIdAndDate(userId, date) {
        const sql = 'SELECT * FROM note WHERE user_id = $1 AND created_at = $2;'

        const result = await this.#db.query(sql, [userId, date])
        return result.rows
    }

    async delete(id) {
        const sql = 'DELETE FROM note WHERE id = $1 RETURNING *;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async checkUniqueness(noteData) {
        const { userId, created_at, type } = noteData
        const sql = 'SELECT 1 FROM note WHERE user_id = $1 AND created_at = $2 AND type = $3;'

        const result = await this.#db.query(sql, [userId, created_at, type])
        return result.rowCount
    }

    async insert(noteData) {
        const { userId, created_at, type, description } = noteData
        const sql = 'INSERT INTO note (user_id, created_at, type, description) VALUES ($1, $2, $3, $4) RETURNING *;'

        const result = await this.#db.query(sql, [userId, created_at, type, description])
        return result.rows[0]
    }

    async update(id, noteData) {
        const { description } = noteData
        const sql = `
            UPDATE note 
            SET 
                description = COALESCE($1, description)
            WHERE id = $2
            RETURNING *;`

        const result = await this.#db.query(sql, [description, id])
        return result.rows[0]
    }
}
