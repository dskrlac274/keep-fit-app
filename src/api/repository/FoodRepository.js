import Database from "../database/Database.js"

export default class FoodRepository {
    #db

    constructor() {
        this.#db = new Database()
    }


    async getAllByUserId(userId) {
        const sql = 'SELECT * FROM food WHERE user_id = $1;'

        const result = await this.#db.query(sql, [userId])
        return result.rows
    }

    async getById(id) {
        const sql = 'SELECT * FROM food WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async delete(id) {
        const sql = 'DELETE FROM food WHERE id = $1 RETURNING *;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async insert(foodData) {
        const { userId, name, calories, nutrients, type } = foodData

        const sql = `
            INSERT INTO food (user_id, name, calories, nutrients, type)
            VALUES ($1, $2, $3, $4, $5)
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, name, calories, nutrients, type])
        return result.rows[0]
    }

    async update(id, foodData) {
        const { nutrients } = foodData

        const query = 'SELECT update_jsonb($1, $2, $3, $4) AS data;'
        const params = ['food', 'nutrients', id, nutrients]

        await this.#db.query(query, params)
        return this.#updateRegularAttributes(id, foodData)
    }

    async #updateRegularAttributes(id, exerciseData) {
        const { name, calories } = exerciseData

        const sql = `
        UPDATE food
        SET name = COALESCE($1, name),
            calories = COALESCE($2, calories)
        WHERE id = $3
        RETURNING *;`


        const params = [name, calories, id]
        const result = await this.#db.query(sql, params)
        return result.rows[0]
    }
}
