package hr.foi.tbp.keepfit.page

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.tbp.keepfit.model.request.ExerciseCreateRequest
import hr.foi.tbp.keepfit.model.request.ExerciseLogCreateRequest
import hr.foi.tbp.keepfit.model.request.ExercisePatchRequest
import hr.foi.tbp.keepfit.model.request.FoodCreateRequest
import hr.foi.tbp.keepfit.model.request.FoodLogCreateRequest
import hr.foi.tbp.keepfit.model.request.FoodPatchRequest
import hr.foi.tbp.keepfit.model.request.NoteCreateRequest
import hr.foi.tbp.keepfit.model.request.NotePatchRequest
import hr.foi.tbp.keepfit.model.response.ExerciseCardioDetails
import hr.foi.tbp.keepfit.model.response.ExerciseLogResponse
import hr.foi.tbp.keepfit.model.response.ExerciseResponse
import hr.foi.tbp.keepfit.model.response.ExerciseStrengthDetails
import hr.foi.tbp.keepfit.model.response.FoodDetails
import hr.foi.tbp.keepfit.model.response.FoodLogResponse
import hr.foi.tbp.keepfit.model.response.FoodResponse
import hr.foi.tbp.keepfit.model.response.NoteResponse
import hr.foi.tbp.keepfit.viewmodel.ExerciseViewModel
import hr.foi.tbp.keepfit.viewmodel.FoodViewModel
import hr.foi.tbp.keepfit.viewmodel.NoteViewModel
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DiaryPage(
    noteViewModel: NoteViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel(),
    foodViewModel: FoodViewModel = viewModel(),
) {
    val context = LocalContext.current
    val noteResponse by noteViewModel.noteResponse.observeAsState(null)
    val exerciseResponse by exerciseViewModel.exerciseResponse.observeAsState(null)
    val foodResponse by foodViewModel.foodResponse.observeAsState(null)

    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(currentDate) {
        noteViewModel.tryGetNotes(currentDate.toString(), onFailed = {
            Toast.makeText(
                context,
                noteViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
        })

        exerciseViewModel.tryGetExercisesLog(currentDate.toString(), onFailed = {
            Toast.makeText(
                context,
                noteViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
        })

        foodViewModel.tryGetFoodLog(currentDate.toString(), onFailed = {
            Toast.makeText(
                context,
                noteViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(3, 6, 21))
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Date",
                        tint = Color.White,
                        modifier = Modifier.clickable {
                            currentDate = currentDate.minusDays(1)
                        }
                    )
                    Text(
                        text = if (currentDate == LocalDate.now()) {
                            "Today"
                        } else {
                            currentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                        },
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    if (currentDate != LocalDate.now()) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Date",
                            tint = Color.White,
                            modifier = Modifier.clickable {
                                currentDate = currentDate.plusDays(1)
                            }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Date",
                            tint = Color.Gray
                        )
                    }
                }
            }
            item {
                FoodCard(
                    type = "Breakfast",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    foods = foodResponse?.filter { it.type == "Breakfast" }?.toMutableList()
                        ?: mutableListOf(),
                    foodViewModel = foodViewModel
                )
            }
            item {
                FoodCard(
                    type = "Lunch",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    foods = foodResponse?.filter { it.type == "Lunch" }?.toMutableList()
                        ?: mutableListOf(),
                    foodViewModel = foodViewModel
                )
            }
            item {

                FoodCard(
                    type = "Dinner",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    foods = foodResponse?.filter { it.type == "Dinner" }?.toMutableList()
                        ?: mutableListOf(),
                    foodViewModel = foodViewModel
                )
            }

            item {
                ExerciseCard(
                    type = "Strength",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    exercises = exerciseResponse?.filter { it.type == "Strength" }?.toMutableList()
                        ?: mutableListOf(),
                    exerciseViewModel = exerciseViewModel
                )
            }
            item {
                ExerciseCard(
                    type = "Cardiovascular",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    exercises = exerciseResponse?.filter { it.type == "Cardiovascular" }
                        ?.toMutableList() ?: mutableListOf(),
                    exerciseViewModel = exerciseViewModel
                )
            }

            item {
                NotesCard(
                    type = "Food",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    notes = noteResponse?.filter { it.type == "Food" }?.toMutableList()
                        ?: mutableListOf(),
                    noteViewModel = noteViewModel
                )
            }
            item {
                NotesCard(
                    type = "Exercise",
                    date = currentDate.toString().format(DateTimeFormatter.ISO_DATE),
                    notes = noteResponse?.filter { it.type == "Exercise" }?.toMutableList()
                        ?: mutableListOf(),
                    noteViewModel = noteViewModel
                )
            }
        }
    }
}

@Composable
fun FoodCard(
    type: String,
    date: String,
    foods: MutableList<FoodLogResponse>,
    foodViewModel: FoodViewModel
) {
    val context = LocalContext.current
    val deletingFood = remember { mutableStateOf<FoodLogResponse?>(null) }
    var showDialogSelect by remember { mutableStateOf(false) }
    var showDialogCreate by remember { mutableStateOf(false) }
    var deleteQuantity by remember { mutableStateOf("") }

    fun deleteFoodConfirmation(food: FoodLogResponse) {
        deleteQuantity = food.quantity.toString()
        deletingFood.value = food
    }

    deletingFood.value?.let { food ->
        AlertDialog(
            onDismissRequest = {
                deletingFood.value = null
            },
            title = {
                Text(text = "Delete Food")
            },
            text = {
                Text(text = "Delete this food?")

                TextField(value = deleteQuantity, onValueChange = {
                    deleteQuantity = it
                }, label = { Text("Quantity to delete") })

            },
            confirmButton = {
                Button(
                    onClick = {
                        foodViewModel.tryDeleteFoodLog(
                            food.id,
                            deleteQuantity.toIntOrNull() ?: 0,
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    foodViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                deletingFood.value = null
                            },
                            onSucceed = {
                                deletingFood.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deletingFood.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Food - $type",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showDialogSelect = true },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.List,
                            contentDescription = "Select Food"
                        )
                    }

                    IconButton(
                        onClick = { showDialogCreate = true },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Food"
                        )
                    }
                }
            }

            foods.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${it.name} (${it.quantity}) - ${round(it.calories * it.quantity)}",
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { deleteFoodConfirmation(it) },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Food"
                        )
                    }
                }
            }

            Text(
                text = "Total Calories: ${round(foods.sumOf { it.calories * it.quantity })}",
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showDialogSelect) {
        FoodSelectionDialog(
            type = type,
            date = date,
            onDismiss = { showDialogSelect = false },
            foodViewModel = foodViewModel
        )
    }

    if (showDialogCreate) {
        CreateFoodDialog(
            type = type,
            onCreate = {
                foodViewModel.tryPost(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            foodViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            foodViewModel.apiMessage.value.toString(),
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
fun CreateFoodDialog(
    type: String,
    onCreate: (food: FoodCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var calories by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var fats by remember { mutableStateOf("") }
    var cholesterol by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbohydrates by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Create $type - Food") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                LazyColumn {
                    item {
                        TextField(value = name, onValueChange = {
                            name = it
                        }, label = { Text("Name") })
                    }
                    item {
                        TextField(value = calories, onValueChange = {
                            calories = it
                        }, label = { Text("Calories") })
                    }
                    item {
                        TextField(value = fats, onValueChange = {
                            fats = it
                        }, label = { Text("Fats") })
                    }
                    item {
                        TextField(value = cholesterol, onValueChange = {
                            cholesterol = it
                        }, label = { Text("Cholesterol") })
                    }
                    item {
                        TextField(value = sodium, onValueChange = {
                            sodium = it
                        }, label = { Text("Sodium") })
                    }
                    item {
                        TextField(value = sugar, onValueChange = {
                            sugar = it
                        }, label = { Text("Sugar") })
                    }
                    item {
                        TextField(value = protein, onValueChange = {
                            protein = it
                        }, label = { Text("Protein") })
                    }
                    item {
                        TextField(value = carbohydrates, onValueChange = {
                            carbohydrates = it
                        }, label = { Text("Carbohydrates") })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val nutrients = FoodDetails(
                    fat = fats.toDoubleOrNull() ?: 0.00,
                    cholesterol = cholesterol.toDoubleOrNull() ?: 0.00,
                    sodium = sodium.toDoubleOrNull() ?: 0.00,
                    sugar = sugar.toDoubleOrNull() ?: 0.00,
                    protein = protein.toDoubleOrNull() ?: 0.00,
                    carbohydrates = carbohydrates.toDoubleOrNull() ?: 0.00,
                )
                val foodCreate = FoodCreateRequest(
                    type = type,
                    name = name,
                    calories = calories.toDoubleOrNull() ?: 0.00,
                    nutrients = nutrients
                )
                onCreate(foodCreate)
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
fun FoodSelectionDialog(
    type: String,
    date: String,
    onDismiss: () -> Unit,
    foodViewModel: FoodViewModel
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val foodHistoryResponse by foodViewModel.foodHistoryResponse.observeAsState(null)

    LaunchedEffect(Unit) {
        foodViewModel.tryGetAllFoods(type, onFailed = {
            Toast.makeText(
                context,
                foodViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
            showDialog = true
        },
            onSucceed = {
                showDialog = true
            })
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Select Food")
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            },
            text = {
                LazyColumn {
                    items(foodHistoryResponse ?: emptyList()) { food ->
                        FoodItem(
                            date = date,
                            food = food,
                            foodViewModel = foodViewModel
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun FoodItem(
    date: String,
    food: FoodResponse,
    foodViewModel: FoodViewModel,
) {
    val context = LocalContext.current
    val deletingFood = remember { mutableStateOf<FoodResponse?>(null) }
    var updateQuantity by remember { mutableStateOf("") }
    val openQuantityDialog = remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }

    fun deleteFoodConfirmation(food: FoodResponse) {
        deletingFood.value = food
    }

    deletingFood.value?.let {
        AlertDialog(
            onDismissRequest = {
                deletingFood.value = null
            },
            title = {
                Text(text = "Delete Food")
            },
            text = {
                Text(text = "Delete this Food?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        foodViewModel.tryDeleteFood(
                            it.id.toString(),
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    foodViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                deletingFood.value = null
                            },
                            onSucceed = {
                                deletingFood.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deletingFood.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (openQuantityDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openQuantityDialog.value = false
            },
            title = { Text(text = "Enter Quantity") },
            text = {
                TextField(value = updateQuantity, onValueChange = {
                    updateQuantity = it
                }, label = { Text("Quantity to add") })
            },
            confirmButton = {
                Button(
                    onClick = {
                        val foodLog = FoodLogCreateRequest(
                            food.id,
                            date,
                            updateQuantity.toIntOrNull() ?: 0
                        )
                        foodViewModel.tryPostLog(
                            foodLog,
                            onFailed = {
                                Toast
                                    .makeText(
                                        context,
                                        foodViewModel.apiMessage.value.toString(),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                openQuantityDialog.value = false
                                updateQuantity = ""
                            },
                            onSucceed = {
                                Toast
                                    .makeText(
                                        context,
                                        foodViewModel.apiMessage.value.toString(),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                openQuantityDialog.value = false
                                updateQuantity = ""
                            })
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { openQuantityDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                openQuantityDialog.value = true
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${food.name} - ${round(food.calories)} calories",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = { showDialogEdit = true },
            ) {
                Icon(
                    tint = Color.Black,
                    imageVector = Icons.Default.Build,
                    contentDescription = "Update Food"
                )
            }
            IconButton(
                onClick = { deleteFoodConfirmation(food) },
            ) {
                Icon(
                    tint = Color.Black,
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Food"
                )
            }
        }
    }

    if (showDialogEdit) {
        EditFoodDialog(
            type = food.type,
            food = food,
            onDismiss = { showDialogEdit = false },
            onUpdate = {
                foodViewModel.tryPatch(
                    it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            foodViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        showDialogEdit = false
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            foodViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        showDialogEdit = false
                    })
            }
        )
    }
}

@Composable
fun EditFoodDialog(
    type: String,
    food: FoodResponse,
    onUpdate: (food: FoodPatchRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var calories by remember { mutableStateOf(food.calories.toString()) }
    var name by remember { mutableStateOf(food.name) }

    var fats by remember { mutableStateOf(food.nutrients.fat.toString()) }
    var cholesterol by remember { mutableStateOf(food.nutrients.cholesterol.toString()) }
    var sodium by remember { mutableStateOf(food.nutrients.sodium.toString()) }
    var sugar by remember { mutableStateOf(food.nutrients.sugar.toString()) }
    var protein by remember { mutableStateOf(food.nutrients.protein.toString()) }
    var carbohydrates by remember { mutableStateOf(food.nutrients.carbohydrates.toString()) }

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Update $type - Food") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                LazyColumn {
                    item {
                        TextField(value = name, onValueChange = {
                            name = it
                        }, label = { Text("Name") })
                    }
                    item {
                        TextField(value = calories, onValueChange = {
                            calories = it
                        }, label = { Text("Calories") })
                    }
                    item {
                        TextField(value = fats, onValueChange = {
                            fats = it
                        }, label = { Text("Fats") })
                    }
                    item {
                        TextField(value = cholesterol, onValueChange = {
                            cholesterol = it
                        }, label = { Text("Cholesterol") })
                    }
                    item {
                        TextField(value = sodium, onValueChange = {
                            sodium = it
                        }, label = { Text("Sodium") })
                    }
                    item {
                        TextField(value = sugar, onValueChange = {
                            sugar = it
                        }, label = { Text("Sugar") })
                    }
                    item {
                        TextField(value = protein, onValueChange = {
                            protein = it
                        }, label = { Text("Protein") })
                    }
                    item {
                        TextField(value = carbohydrates, onValueChange = {
                            carbohydrates = it
                        }, label = { Text("Carbohydrates") })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val nutrients = FoodDetails(
                    fat = fats.toDoubleOrNull() ?: 0.00,
                    cholesterol = cholesterol.toDoubleOrNull() ?: 0.00,
                    sodium = sodium.toDoubleOrNull() ?: 0.00,
                    sugar = sugar.toDoubleOrNull() ?: 0.00,
                    protein = protein.toDoubleOrNull() ?: 0.00,
                    carbohydrates = carbohydrates.toDoubleOrNull() ?: 0.00,
                )
                val foodUpdate = FoodPatchRequest(
                    id = food.id,
                    name = name,
                    calories = calories.toDoubleOrNull() ?: 0.00,
                    nutrients = nutrients
                )
                onUpdate(foodUpdate)
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
fun ExerciseCard(
    type: String,
    date: String,
    exercises: MutableList<ExerciseLogResponse>,
    exerciseViewModel: ExerciseViewModel
) {
    val context = LocalContext.current
    val deletingExercise = remember { mutableStateOf<ExerciseLogResponse?>(null) }
    var showDialogSelect by remember { mutableStateOf(false) }
    var showDialogCreate by remember { mutableStateOf(false) }
    var deleteQuantity by remember { mutableStateOf("") }

    fun deleteExerciseConfirmation(exercise: ExerciseLogResponse) {
        deleteQuantity = exercise.quantity.toString()
        deletingExercise.value = exercise
    }

    deletingExercise.value?.let { exercise ->
        AlertDialog(
            onDismissRequest = {
                deletingExercise.value = null
            },
            title = {
                Text(text = "Delete Exercise")
            },
            text = {
                Text(text = "Delete this Exercise?")

                TextField(value = deleteQuantity, onValueChange = {
                    deleteQuantity = it
                }, label = { Text("Quantity to delete") })

            },
            confirmButton = {
                Button(
                    onClick = {
                        exerciseViewModel.tryDeleteExerciseLog(
                            exercise.id,
                            deleteQuantity.toIntOrNull() ?: 0,
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    exerciseViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                deletingExercise.value = null
                            },
                            onSucceed = {
                                deletingExercise.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deletingExercise.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise - $type",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showDialogSelect = true },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.List,
                            contentDescription = "Select Exercise"
                        )
                    }

                    IconButton(
                        onClick = { showDialogCreate = true },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Exercise"
                        )
                    }
                }
            }
            Log.i("daniel", "exercises je $exercises")

            exercises.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${it.name} (${it.quantity}) - ${round(it.caloriesBurned * it.quantity)}",
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { deleteExerciseConfirmation(it) },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Exercise"
                        )
                    }
                }
            }

            Text(
                text = "Total Calories Burned: ${round(exercises.sumOf { it.caloriesBurned * it.quantity })}",
                modifier = Modifier.padding(top = 8.dp),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showDialogSelect) {
        ExerciseSelectionDialog(
            type = type,
            date = date,
            onDismiss = { showDialogSelect = false },
            exerciseViewModel = exerciseViewModel
        )
    }
    if (showDialogCreate) {
        CreateExerciseDialog(
            type = type,
            onCreate = {
                exerciseViewModel.tryPost(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            exerciseViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            exerciseViewModel.apiMessage.value.toString(),
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
fun CreateExerciseDialog(
    type: String,
    onCreate: (exercise: ExerciseCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var duration by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    var sets by remember { mutableStateOf("") }
    var repsPerSet by remember { mutableStateOf("") }
    var liftWeight by remember { mutableStateOf("") }

    var distance by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Create $type - Exercise") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (type) {
                    "Strength" -> {
                        LazyColumn {
                            item {
                                TextField(value = name, onValueChange = {
                                    name = it
                                }, label = { Text("Name") })
                            }
                            item {
                                TextField(value = duration, onValueChange = {
                                    duration = it
                                }, label = { Text("Duration") })
                            }
                            item {
                                TextField(value = sets, onValueChange = {
                                    sets = it
                                }, label = { Text("Sets") })
                            }
                            item {
                                TextField(value = repsPerSet, onValueChange = {
                                    repsPerSet = it
                                }, label = { Text("Reps per set") })
                            }
                            item {
                                TextField(value = liftWeight, onValueChange = {
                                    liftWeight = it
                                }, label = { Text("Lift weight") })
                            }
                        }
                    }

                    "Cardiovascular" -> {
                        LazyColumn {
                            item {
                                TextField(value = name, onValueChange = {
                                    name = it
                                }, label = { Text("Name") })
                            }
                            item {
                                TextField(value = duration, onValueChange = {
                                    duration = it
                                }, label = { Text("Duration") })
                            }
                            item {
                                TextField(value = distance, onValueChange = {
                                    distance = it
                                }, label = { Text("Distance") })
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val exerciseCreate: ExerciseCreateRequest?
                if (type == "Strength") {
                    val details = ExerciseStrengthDetails(
                        sets = sets.toIntOrNull() ?: 0,
                        repsPerSet = repsPerSet.toIntOrNull() ?: 0,
                        liftWeight = liftWeight.toDoubleOrNull() ?: 0.00
                    )
                    exerciseCreate = ExerciseCreateRequest(
                        type = type,
                        name = name,
                        duration = duration.toIntOrNull() ?: 0,
                        caloriesBurned = calculateBurnedCaloriesForStrength(
                            sets = details.sets,
                            repsPerSet = details.repsPerSet,
                            liftWeight = details.liftWeight,
                            duration = duration.toIntOrNull() ?: 0
                        ),
                        details = details
                    )
                    onCreate(exerciseCreate)
                } else {
                    val details = ExerciseCardioDetails(
                        distance = distance.toDoubleOrNull() ?: 0.00
                    )
                    exerciseCreate = ExerciseCreateRequest(
                        type = type,
                        name = name,
                        duration = duration.toIntOrNull() ?: 0,
                        caloriesBurned = calculateBurnedCaloriesForCardio(
                            distance = details.distance
                        ),
                        details = details
                    )
                    onCreate(exerciseCreate)
                }
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
fun ExerciseSelectionDialog(
    type: String,
    date: String,
    onDismiss: () -> Unit,
    exerciseViewModel: ExerciseViewModel
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val exerciseHistoryResponse by exerciseViewModel.exerciseHistoryResponse.observeAsState(null)

    LaunchedEffect(Unit) {
        exerciseViewModel.tryGetAllExercises(type, onFailed = {
            Toast.makeText(
                context,
                exerciseViewModel.apiMessage.value.toString(),
                Toast.LENGTH_LONG
            ).show()
            showDialog = true
        },
            onSucceed = {
                showDialog = true
            })
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Select Exercise")
            },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss")
                }
            },
            text = {
                LazyColumn {
                    items(exerciseHistoryResponse ?: emptyList()) { exercise ->
                        ExerciseItem(
                            date = date,
                            exercise = exercise,
                            exerciseViewModel = exerciseViewModel
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ExerciseItem(
    date: String,
    exercise: ExerciseResponse,
    exerciseViewModel: ExerciseViewModel,
) {
    val context = LocalContext.current
    val deletingExercise = remember { mutableStateOf<ExerciseResponse?>(null) }
    var updateQuantity by remember { mutableStateOf("") }
    val openQuantityDialog = remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }

    fun deleteExerciseConfirmation(exercise: ExerciseResponse) {
        deletingExercise.value = exercise
    }

    deletingExercise.value?.let {
        AlertDialog(
            onDismissRequest = {
                deletingExercise.value = null
            },
            title = {
                Text(text = "Delete Exercise")
            },
            text = {
                Text(text = "Delete this exercise?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        exerciseViewModel.tryDeleteExercise(
                            it.id.toString(),
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    exerciseViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                deletingExercise.value = null
                            },
                            onSucceed = {
                                deletingExercise.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deletingExercise.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (openQuantityDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openQuantityDialog.value = false
            },
            title = { Text(text = "Enter Quantity") },
            text = {
                TextField(value = updateQuantity, onValueChange = {
                    updateQuantity = it
                }, label = { Text("Quantity to add") })
            },
            confirmButton = {
                Button(
                    onClick = {
                        val exerciseLog = ExerciseLogCreateRequest(
                            exercise.id,
                            date,
                            updateQuantity.toIntOrNull() ?: 0
                        )
                        exerciseViewModel.tryPostLog(
                            exerciseLog,
                            onFailed = {
                                Toast
                                    .makeText(
                                        context,
                                        exerciseViewModel.apiMessage.value.toString(),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                openQuantityDialog.value = false
                                updateQuantity = ""
                            },
                            onSucceed = {
                                Toast
                                    .makeText(
                                        context,
                                        exerciseViewModel.apiMessage.value.toString(),
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                openQuantityDialog.value = false
                                updateQuantity = ""
                            })
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { openQuantityDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                openQuantityDialog.value = true
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${exercise.name} - ${round(exercise.caloriesBurned)} calories burned",
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = { showDialogEdit = true },
            ) {
                Icon(
                    tint = Color.Black,
                    imageVector = Icons.Default.Build,
                    contentDescription = "Update Exercise"
                )
            }
            IconButton(
                onClick = { deleteExerciseConfirmation(exercise) },
            ) {
                Icon(
                    tint = Color.Black,
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Exercise"
                )
            }
        }
    }

    if (showDialogEdit) {
        EditExerciseDialog(
            type = exercise.type,
            exercise = exercise,
            onDismiss = { showDialogEdit = false },
            onUpdate = {
                exerciseViewModel.tryPatch(
                    it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            exerciseViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        showDialogEdit = false
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            exerciseViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        showDialogEdit = false
                    })
            }
        )
    }
}

@Composable
fun EditExerciseDialog(
    type: String,
    exercise: ExerciseResponse,
    onUpdate: (exercise: ExercisePatchRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var duration by remember { mutableStateOf(exercise.duration.toString()) }
    var name by remember { mutableStateOf(exercise.name) }
    var caloriesBurned by remember { mutableStateOf(exercise.caloriesBurned.toString()) }

    var sets by remember { mutableStateOf("") }
    var repsPerSet by remember { mutableStateOf("") }
    var liftWeight by remember { mutableStateOf("") }

    var distance by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Update $type") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                when (val exerciseDetails = exercise.details) {
                    is ExerciseStrengthDetails -> {
                        sets = exerciseDetails.sets.toString()
                        repsPerSet = exerciseDetails.repsPerSet.toString()
                        liftWeight = exerciseDetails.liftWeight.toString()

                        LazyColumn {
                            item {
                                TextField(value = name, onValueChange = {
                                    name = it
                                }, label = { Text("Name") })
                            }
                            item {
                                TextField(value = duration, onValueChange = {
                                    duration = it
                                }, label = { Text("Duration") })
                            }
                            item {
                                TextField(value = sets, onValueChange = {
                                    sets = it
                                }, label = { Text("Sets") })
                            }
                            item {
                                TextField(value = repsPerSet, onValueChange = {
                                    repsPerSet = it
                                }, label = { Text("Reps per set") })
                            }
                            item {
                                TextField(value = liftWeight, onValueChange = {
                                    liftWeight = it
                                }, label = { Text("Lift weight") })
                            }
                            item {
                                TextField(value = caloriesBurned, onValueChange = {
                                    caloriesBurned = it
                                }, label = { Text("Calories burned") }, enabled = false)
                            }
                        }
                    }

                    is ExerciseCardioDetails -> {
                        distance = exerciseDetails.distance.toString()

                        LazyColumn {
                            item {
                                TextField(value = name, onValueChange = {
                                    name = it
                                }, label = { Text("Name") })
                            }
                            item {
                                TextField(value = duration, onValueChange = {
                                    duration = it
                                }, label = { Text("Duration") })
                            }
                            item {
                                TextField(value = distance, onValueChange = {
                                    distance = it
                                }, label = { Text("Distance") })
                            }
                            item {
                                TextField(value = caloriesBurned, onValueChange = {
                                    caloriesBurned = it
                                }, label = { Text("Calories burned") }, enabled = false)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val exercisePatch: ExercisePatchRequest?
                if (type == "Strength") {
                    val details = ExerciseStrengthDetails(
                        sets = sets.toIntOrNull() ?: 0,
                        repsPerSet = repsPerSet.toIntOrNull() ?: 0,
                        liftWeight = liftWeight.toDoubleOrNull() ?: 0.00
                    )
                    exercisePatch = ExercisePatchRequest(
                        id = exercise.id,
                        name = name,
                        duration = duration.toIntOrNull() ?: 0,
                        caloriesBurned = calculateBurnedCaloriesForStrength(
                            sets = details.sets,
                            repsPerSet = details.repsPerSet,
                            liftWeight = details.liftWeight,
                            duration = duration.toIntOrNull() ?: 0
                        ),
                        details = details
                    )
                    onUpdate(exercisePatch)
                } else {
                    val details = ExerciseCardioDetails(
                        distance = distance.toDoubleOrNull() ?: 0.00
                    )
                    exercisePatch = ExercisePatchRequest(
                        id = exercise.id,
                        name = name,
                        duration = duration.toIntOrNull() ?: 0,
                        caloriesBurned = calculateBurnedCaloriesForCardio(
                            distance = details.distance
                        ),
                        details = details
                    )
                    onUpdate(exercisePatch)
                }
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
fun NotesCard(
    type: String,
    date: String,
    notes: MutableList<NoteResponse>,
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current
    val deletingNote = remember { mutableStateOf<NoteResponse?>(null) }
    var showDialogCreate by remember { mutableStateOf(false) }
    val updatingNote = remember { mutableStateOf<NoteResponse?>(null) }
    var description by remember { mutableStateOf("") }

    fun deleteNoteConfirmation(note: NoteResponse) {
        deletingNote.value = note
    }

    fun updateNoteConfirmation(note: NoteResponse) {
        updatingNote.value = note
        description = updatingNote.value!!.description
    }

    updatingNote.value?.let { note ->
        AlertDialog(
            onDismissRequest = {
                updatingNote.value = null
            },
            title = {
                Text(text = "Update Note")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        item {
                            TextField(
                                value = description,
                                onValueChange = { newValue ->
                                    description = newValue
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Description") },
                                maxLines = 20
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteViewModel.tryPatchNote(
                            NotePatchRequest(note.id, description),
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    noteViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                updatingNote.value = null
                            },
                            onSucceed = {
                                updatingNote.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        updatingNote.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    deletingNote.value?.let { note ->
        AlertDialog(
            onDismissRequest = {
                deletingNote.value = null
            },
            title = {
                Text(text = "Delete Note")
            },
            text = {
                Text(text = "Delete this note?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteViewModel.tryDeleteNote(
                            note.id.toString(),
                            onFailed = {
                                Toast.makeText(
                                    context,
                                    noteViewModel.apiMessage.value.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                deletingNote.value = null
                            },
                            onSucceed = {
                                deletingNote.value = null
                            })
                    }
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        deletingNote.value = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Note - $type", fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(
                    onClick = { showDialogCreate = true },
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note"
                    )
                }
            }
            notes.forEach { note ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.description,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = { updateNoteConfirmation(note) },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Build,
                            contentDescription = "Update Note"
                        )
                    }

                    IconButton(
                        onClick = { deleteNoteConfirmation(note) },
                    ) {
                        Icon(
                            tint = Color.White,
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note"
                        )
                    }
                }
            }
        }
    }

    if (showDialogCreate) {
        CreateNoteDialog(
            type = type,
            date = date,
            onCreate = {
                noteViewModel.tryPostNote(it,
                    onFailed = {
                        Toast.makeText(
                            context,
                            noteViewModel.apiMessage.value.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onSucceed = {
                        Toast.makeText(
                            context,
                            noteViewModel.apiMessage.value.toString(),
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
fun CreateNoteDialog(
    type: String,
    date: String,
    onCreate: (note: NoteCreateRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Add note") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                LazyColumn {
                    item {
                        TextField(
                            value = description,
                            onValueChange = { newValue ->
                                description = newValue
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Description") },
                            maxLines = 20
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val noteCreateRequest = NoteCreateRequest(type, description, date)
                onCreate(noteCreateRequest)
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

fun calculateBurnedCaloriesForCardio(distance: Double): Double {
    return distance * 50 * 0.6
}

fun calculateBurnedCaloriesForStrength(
    sets: Int,
    repsPerSet: Int,
    liftWeight: Double,
    duration: Int
): Double {
    return (duration.toDouble() / 60.0) * (liftWeight / 2.205) * sets * repsPerSet
}

fun round(num: Double): Double {
    return num.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
}
