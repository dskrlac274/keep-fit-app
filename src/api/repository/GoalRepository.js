import Database from "../database/Database.js"

export default class GoalRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getAllByUserId(userId) {
        const sql = 'SELECT * FROM goal WHERE user_id = $1;'

        const result = await this.#db.query(sql, [userId])
        return result.rows
    }

    async getById(id) {
        const sql = 'SELECT * FROM goal WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async insert(goalData) {
        const { userId, type, goal } = goalData

        const sql = `
            INSERT INTO goal (user_id, type, goal)
            VALUES ($1, $2, $3)
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, type, goal])
        return result.rows[0]
    }

    async checkTypesUniqueness(userId, type) {
        const sql = `
            SELECT COUNT(*)
            FROM goal
            WHERE user_id = $1 AND type = $2;`

        const result = await this.#db.query(sql, [userId, type])
        return result.rows[0].count
    }

    async update(id, goal) {
        const query = 'SELECT update_jsonb($1, $2, $3, $4) AS data;'

        const result = await this.#db.query(query, ['goal', 'goal', id, goal])
        return result.rows[0].data
    }
}
