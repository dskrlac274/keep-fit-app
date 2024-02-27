import Database from "../database/Database.js"

export default class HealthIndicatorRepository {
    #db

    constructor() {
        this.#db = new Database()
    }

    async getAllByUserId(userId) {
        const sql = 'SELECT * FROM health_indicator_history WHERE user_id = $1 AND created_at = CURRENT_DATE;'

        const result = await this.#db.query(sql, [userId])
        return result.rows[0]
    }

    async getById(id) {
        const sql = 'SELECT * FROM health_indicator_history WHERE id = $1;'

        const result = await this.#db.query(sql, [id])
        return result.rows[0]
    }

    async getAllByUserIdAndFilters(userId, filters) {
        let sqlBase = `
            SELECT * FROM health_indicator_history 
            WHERE user_id = $1 
            AND created_at BETWEEN DATE_TRUNC('month', CURRENT_DATE) AND CURRENT_DATE`

        let filterSubqueries = filters.map(key =>
            `(indicators->>'${key}') IS NOT NULL`).join(' OR ')

        sqlBase += ` AND (${filterSubqueries})`

        const result = await this.#db.query(sqlBase, [userId])
        return result.rows
    }

    async checkUniqueness(userId) {
        const sql = 'SELECT 1 FROM health_indicator_history WHERE user_id = $1 AND created_at = CURRENT_DATE;'

        const result = await this.#db.query(sql, [userId])
        return result.rowCount
    }

    async insert(metricsData) {
        const { userId, indicators } = metricsData

        const sql = `
            INSERT INTO health_indicator_history(user_id, created_at, indicators)
            VALUES ($1, CURRENT_DATE, $2)
            RETURNING *;`

        const result = await this.#db.query(sql, [userId, indicators])
        return result.rows[0]
    }
}
