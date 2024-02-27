import Database from "../database/Database.js"

export default class FoodLogRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getById(id) {
        const sql = 'SELECT * FROM food_log WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async getAllByUserIdAndDate(userId, date) {
        const sql = `
            SELECT fl.id, fl.user_id, fl.food_id, fl.created_at, fl.quantity, f.name, f.calories, f.nutrients, f.type
            FROM food_log AS fl
            JOIN food AS f ON fl.food_id = f.id
            WHERE fl.user_id = $1 AND fl.created_at = $2;`

        const result = await this.#db.query(sql, [userId, date])
        return result.rows
    }

    async insert(foodLogData) {
        const { userId, food_id, created_at, quantity } = foodLogData

        const sql = `
            INSERT INTO food_log (user_id, food_id, created_at, quantity)
            VALUES ($1, $2, $3, $4)
            ON CONFLICT (user_id, food_id, created_at)
            DO UPDATE SET quantity = food_log.quantity + EXCLUDED.quantity
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, food_id, created_at, quantity])

        const insertedRow = result.rows[0]

        const foodQuery = 'SELECT * FROM food WHERE id = $1;'

        const foodResult = await this.#db.query(foodQuery, [insertedRow.food_id])

        const foodDetails = foodResult.rows[0]

        return { ...insertedRow, ...foodDetails }
    }

    async delete(id, quantity) {
        const sqlUpdate = `
            UPDATE food_log
            SET quantity = GREATEST(quantity - $2, 0)
            WHERE id = $1
            RETURNING *;`

        const sqlDelete = `
            DELETE FROM food_log
            WHERE id = $1 AND quantity = 0
            RETURNING *;`

        const updateResult = await this.#db.query(sqlUpdate, [id, quantity])
        const deleteResult = await this.#db.query(sqlDelete, [id])

        if (deleteResult.rowCount > 0) return deleteResult.rows[0]
        else return updateResult.rows[0]
    }
}
