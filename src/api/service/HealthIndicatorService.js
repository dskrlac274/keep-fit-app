import HealthMetricsSchemaValidator from "../validator/HealthMetricsSchemaValidator.js"
import HealthIndicatorRepository from "../repository/HealthIndicatorRepository.js"
import ResponseBuilder from "../response/ResponseBuilder.js"

export default class HealthIndicatorService {
    #healthIndicatorRepository

    constructor() {
        this.#healthIndicatorRepository = new HealthIndicatorRepository()
    }

    async create(metricsData) {
        const { userId, indicators } = metricsData

        const validationError = HealthMetricsSchemaValidator.validateCreate(indicators)
        if (!validationError.success)
            return validationError

        const createdMetricsData = await this.#healthIndicatorRepository.insert(metricsData)

        return ResponseBuilder.createSuccessResponse(createdMetricsData, 'Health indicators successfully added.')
    }

    async get(userId, filters) {
        const filterValues = filters ? filters.split(',').map(filter => filter.trim()) : []
        return await this.#getByFilters(userId, filterValues)
    }

    async #getByFilters(userId, filters) {
        const userHealthIndicators = await this.#healthIndicatorRepository.getAllByUserIdAndFilters(userId, filters)

        if (userHealthIndicators.length == 0)
            return ResponseBuilder.createErrorResponse("User health indicators not found.", 404)

        const formattedResponse = this.#reformatGetFilterData(userHealthIndicators, filters)

        return ResponseBuilder.createSuccessResponse(formattedResponse)
    }

    #reformatGetFilterData(data, filterParameters) {
        const aggregatedData = {}
    
        filterParameters.forEach(param => {
            aggregatedData[param] = data.reduce((acc, record) => {
                const dateKey = record.created_at.toISOString()
                if (!acc[dateKey]) {
                    acc[dateKey] = { sum: 0, count: 0, sumSecondValue: 0 }
                }
                if (param === 'blood_pressure') {
                    const [firstValue, secondValue] = record.indicators[param].split('/').map(Number)
                    acc[dateKey].sum += firstValue
                    acc[dateKey].sumSecondValue += secondValue
                } else {
                    acc[dateKey].sum += Number(record.indicators[param])
                }
                acc[dateKey].count++
                return acc
            }, {})
        })
    
        const formattedResponse = {}
        Object.entries(aggregatedData).forEach(([param, dates]) => {
            formattedResponse[param] = Object.entries(dates).map(([date, { sum, count, sumSecondValue }]) => {
                if (param === 'blood_pressure') {
                    const avgFirstValue = sum / count
                    const avgSecondValue = sumSecondValue / count
                    return { date, value: `${avgFirstValue.toFixed(0)}/${avgSecondValue.toFixed(0)}` }
                } else {
                    return { date, value: (sum / count).toFixed(0) }
                }
            })
        })
    
        filterParameters.forEach(param => {
            formattedResponse[param].sort((a, b) => new Date(a.date) - new Date(b.date))
        })
    
        return formattedResponse
    }
}
