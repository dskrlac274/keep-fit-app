package hr.foi.tbp.keepfit.page


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.tbp.keepfit.model.request.GoalCreateRequest
import hr.foi.tbp.keepfit.model.request.GoalPatchRequest
import hr.foi.tbp.keepfit.model.response.FitnessGoal
import hr.foi.tbp.keepfit.model.response.Goal
import hr.foi.tbp.keepfit.model.response.GoalCoreResponse
import hr.foi.tbp.keepfit.model.response.NutrientGoal
import hr.foi.tbp.keepfit.model.response.WeightGoal
import hr.foi.tbp.keepfit.viewmodel.GoalViewModel
import java.math.RoundingMode

@Composable
fun GoalPage(goalViewModel: GoalViewModel = viewModel()) {
    val context = LocalContext.current
    val goalView by goalViewModel.goalGetPostResponse.observeAsState(null)
    var isLoading by remember { mutableStateOf(true) }
    var showDialogEdit by remember { mutableStateOf(false) }
    var showDialogCreate by remember { mutableStateOf(false) }
    var selectedGoalType by remember { mutableStateOf("") }
    var goalData: GoalCoreResponse<Goal>? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        goalViewModel.tryGetGoal(onFailed = {
            Toast.makeText(
                context,
                goalViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
        },
            onSucceed = {
                isLoading = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(3, 6, 21))
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    color = Color(0xFF1565C0),
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }

            goalView != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        GoalSection("Weight Goals") {
                            selectedGoalType = "Weight Goals"
                            goalData = goalView?.weightGoal
                            showDialogEdit = true
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        GoalSection("Nutrition Goals") {
                            selectedGoalType = "Nutrition Goals"
                            goalData = goalView?.nutrientsGoal
                            showDialogEdit = true
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        GoalSection("Fitness Goals") {
                            selectedGoalType = "Fitness Goals"
                            goalData = goalView?.fitnessGoal
                            showDialogEdit = true
                        }
                    }
                }
            }

            else -> {
                Button(
                    onClick = {
                        showDialogCreate = true
                    },
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(3, 6, 33),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.Center)
                        .height(50.dp)
                ) {
                    Text("Add Goal")
                }
            }
        }
    }

    if (showDialogEdit) {
        EditGoalDialog(
            goalType = selectedGoalType,
            goalData = goalData!!,
            onUpdate = {
                goalViewModel.tryPatchGoal(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            goalViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            goalViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        showDialogEdit = false
                    }
                )
            },
            onDismiss = { showDialogEdit = false }
        )
    }

    if (showDialogCreate) {
        CreateGoalDialog(
            onCreate = {
                goalViewModel.tryPostGoal(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            goalViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            goalViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        showDialogCreate = false
                    }
                )
            },
            onDismiss = { showDialogCreate = false }
        )
    }
}

@Composable
fun GoalSection(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = BorderStroke(1.dp, Color.White),
        colors = CardDefaults.cardColors(containerColor = Color(3, 6, 33))
    ) {
        Text(
            text = title,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun CreateGoalDialog(
    onCreate: (goal: GoalCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var weeklyGoal by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }

    val sexOptions = listOf("M", "F")
    val weeklyGoalOptions = listOf("-1.0", "-0.5", "-0.25", "0.0", "0.25", "0.5", "1.0")
    val activityLevelOptions = listOf("Not very active", "Lightly active", "Active", "Very active")

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Create Weight goal") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = age, onValueChange = {
                            age = it
                        }, label = { Text("Age") })
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Sex",
                            options = sexOptions,
                            selectedValue = sex,
                            onValueChange = { newValue ->
                                sex = newValue
                            }
                        )
                    }

                    item {
                        TextField(value = height, onValueChange = {
                            height = it
                        }, label = { Text("Height") })
                    }

                    item {
                        TextField(value = goalWeight, onValueChange = {
                            goalWeight = it
                        }, label = { Text("Goal Weight") })
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Weekly Goal",
                            options = weeklyGoalOptions,
                            selectedValue = weeklyGoal,
                            onValueChange = { newValue ->
                                weeklyGoal = newValue
                            }
                        )
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Activity Level",
                            options = activityLevelOptions,
                            selectedValue = activityLevel,
                            onValueChange = { newValue ->
                                activityLevel = newValue
                            }
                        )
                    }

                    item {
                        TextField(value = currentWeight, onValueChange = {
                            currentWeight = it
                        }, label = { Text("Current Weight") })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val weightGoal = WeightGoal(
                    age = age.toIntOrNull() ?: 0,
                    sex = sex,
                    height = height.toIntOrNull() ?: 0,
                    goalWeight = goalWeight.toIntOrNull() ?: 0,
                    weeklyGoal = weeklyGoal.toFloatOrNull() ?: 0f,
                    activityLevel = activityLevel,
                    currentWeight = currentWeight.toIntOrNull() ?: 0,
                    dailyCaloriesIntake = 0.0
                )

                val goalCreateRequest = GoalCreateRequest("Weight", weightGoal)
                onCreate(goalCreateRequest)
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditGoalDialog(
    goalType: String,
    goalData: GoalCoreResponse<Goal>,
    onUpdate: (goal: GoalPatchRequest<Goal>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Update $goalType") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (val goal = goalData.goal) {
                    is WeightGoal -> EditWeightGoalForm(goal)
                    is NutrientGoal -> EditNutrientGoalForm(goal)
                    is FitnessGoal -> EditFitnessGoalForm(goal)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val goalPatch = GoalPatchRequest(goalData.id, goalData.goal)
                onUpdate(goalPatch)
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditWeightGoalForm(weightGoal: WeightGoal) {
    var age by remember { mutableStateOf(weightGoal.age.toString()) }
    var sex by remember { mutableStateOf(weightGoal.sex) }
    var height by remember { mutableStateOf(weightGoal.height.toString()) }
    var goalWeight by remember { mutableStateOf(weightGoal.goalWeight.toString()) }
    var weeklyGoal by remember { mutableStateOf(weightGoal.weeklyGoal.toString()) }
    var activityLevel by remember { mutableStateOf(weightGoal.activityLevel) }
    var currentWeight by remember { mutableStateOf(weightGoal.currentWeight.toString()) }

    val sexOptions = listOf("M", "F")
    val weeklyGoalOptions = listOf("-1.0", "-0.5", "-0.25", "0.0", "0.25", "0.5", "1.0")
    val activityLevelOptions = listOf("Not very active", "Lightly active", "Active", "Very active")

    LazyColumn {
        item {
            TextField(value = age, onValueChange = {
                age = it
                weightGoal.age = it.toIntOrNull() ?: weightGoal.age
            }, label = { Text("Age") })
        }

        item {
            CustomDropdownMenu(
                label = "Sex",
                options = sexOptions,
                selectedValue = sex,
                onValueChange = { newValue ->
                    sex = newValue
                    weightGoal.sex = newValue
                }
            )
        }

        item {
            TextField(value = height, onValueChange = {
                height = it
                weightGoal.height = it.toIntOrNull() ?: weightGoal.height
            }, label = { Text("Height") })
        }

        item {
            TextField(value = goalWeight, onValueChange = {
                goalWeight = it
                weightGoal.goalWeight =
                    it.toIntOrNull() ?: weightGoal.goalWeight
            }, label = { Text("Goal Weight") })
        }

        item {
            CustomDropdownMenu(
                label = "Weekly Goal",
                options = weeklyGoalOptions,
                selectedValue = weeklyGoal,
                onValueChange = { newValue ->
                    weeklyGoal = newValue
                    weightGoal.weeklyGoal =
                        newValue.toFloatOrNull() ?: weightGoal.weeklyGoal
                }
            )
        }

        item {
            CustomDropdownMenu(
                label = "Activity Level",
                options = activityLevelOptions,
                selectedValue = activityLevel,
                onValueChange = { newValue ->
                    activityLevel = newValue
                    weightGoal.activityLevel = newValue
                }
            )
        }

        item {
            TextField(value = currentWeight, onValueChange = {
                currentWeight = it
                weightGoal.currentWeight =
                    it.toIntOrNull() ?: weightGoal.currentWeight
            }, label = { Text("Current Weight") })
        }
    }
}

@Composable
fun EditNutrientGoalForm(
    nutrientGoal: NutrientGoal
) {
    var proteins by remember {
        mutableStateOf(
            nutrientGoal.proteins.toBigDecimal().setScale(1, RoundingMode.UP).toString()
        )
    }
    var fats by remember {
        mutableStateOf(
            nutrientGoal.fats.toBigDecimal().setScale(1, RoundingMode.UP).toString()
        )
    }
    var carbs by remember {
        mutableStateOf(
            nutrientGoal.carbohydrates.toBigDecimal().setScale(1, RoundingMode.UP).toString()
        )
    }

    LazyColumn {
        item {
            TextField(value = proteins, onValueChange = {
                proteins = it
                nutrientGoal.proteins =
                    it.toDoubleOrNull() ?: nutrientGoal.proteins
            }, label = { Text("Proteins") })
        }

        item {
            TextField(value = fats, onValueChange = {
                fats = it
                nutrientGoal.fats =
                    it.toDoubleOrNull() ?: nutrientGoal.fats
            }, label = { Text("Fats") })
        }

        item {
            TextField(value = carbs, onValueChange = {
                carbs = it
                nutrientGoal.carbohydrates =
                    it.toDoubleOrNull() ?: nutrientGoal.carbohydrates
            }, label = { Text("Carbohydrates") })
        }
    }
}

@Composable
fun EditFitnessGoalForm(fitnessGoal: FitnessGoal) {
    var dailyBurnedCalories by remember { mutableStateOf(fitnessGoal.dailyBurnedCaloriesGoal.toString()) }

    LazyColumn {
        item {
            TextField(value = dailyBurnedCalories, onValueChange = {
                dailyBurnedCalories = it
                fitnessGoal.dailyBurnedCaloriesGoal =
                    it.toIntOrNull() ?: fitnessGoal.dailyBurnedCaloriesGoal
            }, label = { Text("Daily burned calories goal") })
        }
    }
}

@Composable
fun <T> CustomDropdownMenu(
    label: String,
    options: List<T>,
    selectedValue: T,
    onValueChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(selectedValue.toString()) }

    Column {
        Box(modifier = Modifier.clickable { expanded = true }) {
            TextField(
                label = { Text(label) },
                value = textFieldValue,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    textFieldValue = option.toString()
                    onValueChange(option)
                    expanded = false
                }, text = {
                    Text(option.toString())
                })
            }
        }
    }
}