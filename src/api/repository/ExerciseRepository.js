import Database from "../database/Database.js"

export default class ExerciseRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getAllByUserId(userId) {
        const sql = 'SELECT * FROM exercise WHERE user_id = $1;'

        const result = await this.#db.query(sql, [userId])
        return result.rows
    }

    async getById(id) {
        const sql = 'SELECT * FROM exercise WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async delete(id) {
        const sql = 'DELETE FROM exercise WHERE id = $1 RETURNING *;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async insert(exerciseData) {
        const { userId, type, duration, name, calories_burned, details } = exerciseData

        const sql = `
            INSERT INTO exercise (user_id, type, duration, name, calories_burned, details)
            VALUES ($1, $2, $3, $4, $5, $6)
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, type, duration, name, calories_burned, details])
        return result.rows[0]
    }

    async update(id, exerciseData) {
        const {details} = exerciseData

        const query = 'SELECT update_jsonb($1, $2, $3, $4) AS data;'

        await this.#db.query(query, ['exercise', 'details', id, details])
        return this.#updateRegularAttributes(id, exerciseData)
    }

    async #updateRegularAttributes(id, exerciseData) {
        const { duration, name, calories_burned } = exerciseData
        const sql = `
            UPDATE exercise
            SET duration = COALESCE($1, duration),
                name = COALESCE($2, name),
                calories_burned = COALESCE($3, calories_burned)
            WHERE id = $4
            RETURNING *;`
    
        const params = [duration, name, calories_burned, id]
        const result = await this.#db.query(sql, params)
        return result.rows[0]
    }
}
