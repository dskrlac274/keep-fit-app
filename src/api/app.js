import express from 'express'
import AuthController from './controller/AuthController.js'
import authenticate from './middleware/authentication.js'
import UserController from './controller/UserController.js'
import GoalController from './controller/GoalController.js'
import HealthIndicatorController from './controller/HealthIndicatorController.js'
import NoteController from './controller/NoteController.js'
import ExerciseController from './controller/ExerciseController.js'
import FoodController from './controller/FoodController.js'
import cors from 'cors'
import FoodLogController from './controller/FoodLogController.js'
import ExerciseLogController from './controller/ExerciseLogController.js'

const app = express()

app.use(express.json())
app.use(cors())

registerRoutes()

function registerRoutes() {
    registerAuthRoutes()
    registerGoalRoutes()
    registerUserRoutes()
    registerHealthIndicatorRoutes()
    registerNoteRoutes()
    registerExerciseRoutes()
    registerFoodRoutes()
    registerFoodLogRoutes()
    registerExerciseLogRoutes()
}

function registerAuthRoutes() {
    const authController = new AuthController()
    app.post('/api/v1/auth/login', authController.login.bind(authController))
}

function registerUserRoutes() {
    const userController = new UserController()
    app.get('/api/v1/user/current', authenticate, userController.get.bind(userController))
}

function registerGoalRoutes() {
    const goalController = new GoalController()
    app.get('/api/v1/user/current/goal', authenticate, goalController.get.bind(goalController))
    app.post('/api/v1/user/current/goal', authenticate, goalController.create.bind(goalController))
    app.patch('/api/v1/user/current/goal/:id', authenticate, goalController.patch.bind(goalController))
}

function registerHealthIndicatorRoutes() {
    const healthIndicatorController = new HealthIndicatorController()
    app.get('/api/v1/user/current/health-indicator', authenticate, healthIndicatorController.get.bind(healthIndicatorController))
    app.post('/api/v1/user/current/health-indicator', authenticate, healthIndicatorController.create.bind(healthIndicatorController))
}

function registerNoteRoutes() {
    const noteController = new NoteController()
    app.get('/api/v1/user/current/note/:id', authenticate, noteController.getById.bind(noteController))
    app.get('/api/v1/user/current/note', authenticate, noteController.get.bind(noteController))
    app.post('/api/v1/user/current/note', authenticate, noteController.create.bind(noteController))
    app.patch('/api/v1/user/current/note/:id', authenticate, noteController.patch.bind(noteController))
    app.delete('/api/v1/user/current/note/:id', authenticate, noteController.delete.bind(noteController))
}

function registerExerciseRoutes() {
    const exerciseController = new ExerciseController()
    app.get('/api/v1/user/current/exercise', authenticate, exerciseController.get.bind(exerciseController))
    app.post('/api/v1/user/current/exercise', authenticate, exerciseController.create.bind(exerciseController))
    app.patch('/api/v1/user/current/exercise/:id', authenticate, exerciseController.patch.bind(exerciseController))
    app.delete('/api/v1/user/current/exercise/:id', authenticate, exerciseController.delete.bind(exerciseController))
}

function registerFoodRoutes() {
    const foodController = new FoodController()
    app.get('/api/v1/user/current/food', authenticate, foodController.get.bind(foodController))
    app.post('/api/v1/user/current/food', authenticate, foodController.create.bind(foodController))
    app.patch('/api/v1/user/current/food/:id', authenticate, foodController.patch.bind(foodController))
    app.delete('/api/v1/user/current/food/:id', authenticate, foodController.delete.bind(foodController))
}

function registerFoodLogRoutes(){
    const foodLogController = new FoodLogController()
    app.get('/api/v1/user/current/food-log', authenticate, foodLogController.get.bind(foodLogController))
    app.post('/api/v1/user/current/food-log', authenticate, foodLogController.create.bind(foodLogController))
    app.delete('/api/v1/user/current/food-log/:id', authenticate, foodLogController.delete.bind(foodLogController))
}

function registerExerciseLogRoutes(){
    const exerciseLogController = new ExerciseLogController()
    app.get('/api/v1/user/current/exercise-log', authenticate, exerciseLogController.get.bind(exerciseLogController))
    app.post('/api/v1/user/current/exercise-log', authenticate, exerciseLogController.create.bind(exerciseLogController))
    app.delete('/api/v1/user/current/exercise-log/:id', authenticate, exerciseLogController.delete.bind(exerciseLogController))
}

app.listen(process.env.PORT, () => console.log(`Server running on port ${process.env.PORT}`))
