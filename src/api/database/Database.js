import pkg from 'pg'
const { Pool } = pkg

export default class Database {
    constructor() {
        this.pool = new Pool({
            user: 'postgres',
            host: 'localhost',
            database: 'keep_fit',
            password: '2805',
            port: 5432
        })

        this.pool.on('error', (err, _) => {
            console.error('Unexpected error on idle client', err)
            process.exit(-1)
        })
    }

    async query(sql, params) {
        const client = await this.pool.connect()
        try {
            const result = await client.query(sql, params)
            return result
        } catch (err) {
            console.error('Error executing query', err.stack)
        } finally {
            client.release()
        }
    }
}
