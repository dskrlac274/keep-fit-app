import Database from "../database/Database.js"

export default class ExerciseLogRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getById(id) {
        const sql = 'SELECT * FROM exercise_log WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async getAllByUserIdAndDate(userId, date) {
        const sql = `
            SELECT el.id, el.user_id, el.exercise_id, el.created_at, el.quantity, e.type, e.name, e.duration, e.details, e.calories_burned
            FROM exercise_log AS el
            JOIN exercise AS e ON el.exercise_id = e.id
            WHERE el.user_id = $1 AND el.created_at = $2;`

        const result = await this.#db.query(sql, [userId, date])
        return result.rows
    }

    async insert(exerciseLogData) {
        const { userId, exercise_id, created_at, quantity } = exerciseLogData

        const sql = `
            INSERT INTO exercise_log (user_id, exercise_id, created_at, quantity)
            VALUES ($1, $2, $3, $4)
            ON CONFLICT (user_id, exercise_id, created_at)
            DO UPDATE SET quantity = exercise_log.quantity + EXCLUDED.quantity
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, exercise_id, created_at, quantity])

        const insertedRow = result.rows[0]

        const exerciseQuery = 'SELECT * FROM exercise WHERE exercise.id = $1;'

        const exerciseResult = await this.#db.query(exerciseQuery, [insertedRow.exercise_id])

        const exerciseDetails = exerciseResult.rows[0]

        return { ...insertedRow, ...exerciseDetails }
    }

    async delete(id, quantity) {

        const sqlUpdate = `
            UPDATE exercise_log
            SET quantity = GREATEST(quantity - $2, 0)
            WHERE id = $1
            RETURNING *;`

        const sqlDelete = `
            DELETE FROM exercise_log
            WHERE id = $1 AND quantity = 0
            RETURNING *;`

        const updateResult = await this.#db.query(sqlUpdate, [id, quantity])
        const deleteResult = await this.#db.query(sqlDelete, [id])

        if (deleteResult.rowCount > 0) return deleteResult.rows[0]
        else return updateResult.rows[0]

    }
}
