package hr.foi.tbp.keepfit.page

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.tbp.keepfit.model.response.FitnessGoal
import hr.foi.tbp.keepfit.model.response.NutrientGoal
import hr.foi.tbp.keepfit.model.response.WeightGoal
import hr.foi.tbp.keepfit.viewmodel.ExerciseViewModel
import hr.foi.tbp.keepfit.viewmodel.FoodViewModel
import hr.foi.tbp.keepfit.viewmodel.GoalViewModel
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomePage(
    goalViewModel: GoalViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val goalResponse by goalViewModel.goalGetPostResponse.observeAsState(null)
    val foodResponse by foodViewModel.foodResponse.observeAsState(null)
    val exerciseResponse by exerciseViewModel.exerciseResponse.observeAsState(null)

    var isLoading by remember { mutableStateOf(true) }

    fun checkLoadingState() {
        if (goalResponse != null && exerciseResponse != null && foodResponse != null) {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        goalViewModel.tryGetGoal(
            onSucceed = {
                checkLoadingState()
            },
            onFailed = {
                isLoading = false
                Toast.makeText(
                    context,
                    goalViewModel.apiMessage.value.toString(),
                    Toast.LENGTH_LONG
                ).show()
            })

        exerciseViewModel.tryGetExercisesLog(
            LocalDate.now().toString().format(DateTimeFormatter.ISO_DATE),
            onSucceed = {
                checkLoadingState()
            },
            onFailed = {
                isLoading = false
                Toast.makeText(
                    context,
                    exerciseViewModel.apiMessage.value.toString(),
                    Toast.LENGTH_LONG
                ).show()
            })

        foodViewModel.tryGetFoodLog(
            LocalDate.now().toString().format(DateTimeFormatter.ISO_DATE),
            onSucceed = {
                checkLoadingState()
            },
            onFailed = {
                isLoading = false
                Toast.makeText(
                    context,
                    foodViewModel.apiMessage.value.toString(),
                    Toast.LENGTH_LONG
                ).show()
            })
    }

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(3, 6, 21))
                .padding(16.dp)
        ) {
            LazyColumn {
                item {
                    Text(
                        text = "Today's Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                item {
                    val weightGoal = goalResponse!!.weightGoal.goal as WeightGoal
                    val todayCalorieIntake = foodResponse!!.sumOf { it.calories * it.quantity }
                    val todayBurnedCalories =
                        exerciseResponse!!.sumOf { it.caloriesBurned * it.quantity }

                    CaloriesContainer(
                        baseGoal = weightGoal.dailyCaloriesIntake.toBigDecimal()
                            .setScale(1, RoundingMode.UP).toDouble(),
                        todayCalorieIntake = todayCalorieIntake.toBigDecimal()
                            .setScale(1, RoundingMode.UP).toDouble(),
                        todayBurnedCalories = todayBurnedCalories.toBigDecimal()
                            .setScale(1, RoundingMode.UP).toDouble()
                    )
                }

                val caloriesBurned = exerciseResponse!!.sumOf { it.caloriesBurned * it.quantity }
                val minutesWorkout = exerciseResponse!!.sumOf { it.duration * it.quantity }
                val exerciseGoal = goalResponse!!.fitnessGoal.goal as FitnessGoal

                item {
                    ExerciseContainer(
                        caloriesBurned = caloriesBurned.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble(),
                        minutesWorkout = minutesWorkout,
                        exerciseGoal = exerciseGoal.dailyBurnedCaloriesGoal.toBigDecimal()
                            .setScale(1, RoundingMode.UP)
                            .toDouble()
                    )
                }

                val nutrientGoal = goalResponse!!.nutrientsGoal.goal as NutrientGoal
                val proteinGoal = nutrientGoal.proteins
                val fatGoal = nutrientGoal.fats
                val carbsGoal = nutrientGoal.carbohydrates
                val proteinIntake = foodResponse!!.sumOf { it.nutrients.protein!! * it.quantity }
                val fatIntake = foodResponse!!.sumOf { it.nutrients.fat!! * it.quantity }
                val carbsIntake =
                    foodResponse!!.sumOf { it.nutrients.carbohydrates!! * it.quantity }

                item {
                    FoodContainer(
                        proteinGoal = proteinGoal.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble(),
                        proteinIntake = proteinIntake.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble(),
                        fatGoal = fatGoal.toBigDecimal().setScale(1, RoundingMode.UP).toDouble(),
                        fatIntake = fatIntake.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble(),
                        carbsGoal = carbsGoal.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble(),
                        carbsIntake = carbsIntake.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble()
                    )
                }
            }
        }
    }
}

@Composable
fun CaloriesContainer(
    baseGoal: Double,
    todayCalorieIntake: Double,
    todayBurnedCalories: Double
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Calories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remaining = Goal - Food + Exercise",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${
                            (baseGoal - todayCalorieIntake + todayBurnedCalories).toBigDecimal()
                                .setScale(1, RoundingMode.UP).toDouble()
                        }",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Column {
                    InfoItem(
                        label = "Base Goal",
                        value = "${
                            baseGoal.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                        }",
                        icon = Icons.Filled.Info
                    )
                    InfoItem(
                        label = "Food",
                        value = "${
                            todayCalorieIntake.toBigDecimal().setScale(1, RoundingMode.UP)
                                .toDouble()
                        }",
                        icon = Icons.Filled.Info
                    )
                    InfoItem(
                        label = "Exercise",
                        value = "${
                            todayBurnedCalories.toBigDecimal().setScale(1, RoundingMode.UP)
                                .toDouble()
                        }",
                        icon = Icons.Filled.Info
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            val remainingCalories = baseGoal - todayCalorieIntake + todayBurnedCalories
            val progress = (baseGoal - remainingCalories) / baseGoal.toFloat()
            ProgressBar(progress = progress)
        }
    }
}

@Composable
fun ExerciseContainer(
    caloriesBurned: Double,
    minutesWorkout: Int,
    exerciseGoal: Double
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Exercise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            InfoItem(label = "Calories Burned", value = "$caloriesBurned", icon = Icons.Filled.Info)
            InfoItem(
                label = "Minutes Worked Out",
                value = "$minutesWorkout",
                icon = Icons.Filled.Info
            )
            InfoItem(label = "Goal", value = "$exerciseGoal Calories", icon = Icons.Filled.Info)

            val progress = (caloriesBurned / exerciseGoal).coerceIn(0.0, 1.0)
            ProgressBar(progress = progress)
        }
    }
}

@Composable
fun FoodContainer(
    proteinGoal: Double,
    fatGoal: Double,
    carbsGoal: Double,
    proteinIntake: Double,
    fatIntake: Double,
    carbsIntake: Double
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Food",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            val proteinProgress = (proteinIntake / proteinGoal).coerceIn(0.0, 1.0)
            val fatProgress = (fatIntake / fatGoal).coerceIn(0.0, 1.0)
            val carbsProgress = (carbsIntake / carbsGoal).coerceIn(0.0, 1.0)

            InfoItem(
                label = "Proteins",
                value = "$proteinIntake g / $proteinGoal g",
                icon = Icons.Filled.Info
            )
            ProgressBar(progress = proteinProgress)

            InfoItem(label = "Fats", value = "$fatIntake g / $fatGoal g", icon = Icons.Filled.Info)
            ProgressBar(progress = fatProgress)

            InfoItem(
                label = "Carbs",
                value = "$carbsIntake g / $carbsGoal g",
                icon = Icons.Filled.Info
            )
            ProgressBar(progress = carbsProgress)
        }
    }
}

@Composable
fun ProgressBar(progress: Double) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val cornerRadius = CornerRadius(x = size.height / 2, y = size.height / 2)
        val progressBarColor = if (progress >= 1.0f) Color(19, 141, 212) else Color.White

        drawRoundRect(
            color = Color.Gray,
            size = size,
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = progressBarColor,
            topLeft = Offset.Zero,
            size = Size(width = (size.width * progress).toFloat(), height = size.height),
            cornerRadius = cornerRadius
        )
    }
}

@Composable
fun InfoItem(label: String, value: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(38.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontSize = 12.sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}