package hr.foi.tbp.keepfit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import hr.foi.tbp.keepfit.auth.Auth
import hr.foi.tbp.keepfit.model.request.NoteCreateRequest
import hr.foi.tbp.keepfit.model.request.NotePatchRequest
import hr.foi.tbp.keepfit.model.response.ApiResponse
import hr.foi.tbp.keepfit.model.response.NoteResponse
import hr.foi.tbp.keepfit.service.KeepFitService.noteService
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val _apiMessage: MutableLiveData<String> = MutableLiveData("")
    val apiMessage: LiveData<String> = _apiMessage
    private val _noteResponse = MutableLiveData<List<NoteResponse>>()
    val noteResponse: LiveData<List<NoteResponse>> = _noteResponse

    fun tryGetNotes(date: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = noteService.get(Auth.authUserData!!.jwt, date)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    _noteResponse.value = apiResponse?.data
                    reportApiMessage(apiResponse!!.message)
                }
                onSucceed()
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryPatchNote(note: NotePatchRequest, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = noteService.patch(Auth.authUserData!!.jwt, note.id.toString(), note)
                if (response.isSuccessful) {
                    val currentList = _noteResponse.value?.toMutableList() ?: mutableListOf()
                    val indexOfElement = currentList.indexOfFirst { it.id == note.id }
                    if (indexOfElement != -1) {
                        currentList.removeAt(indexOfElement)
                        currentList.add(indexOfElement, response.body()!!.data)
                        _noteResponse.value = currentList
                    }
                    onSucceed()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportApiMessage(errorResponse.message)
                    onFailed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryDeleteNote(id: String, onFailed: () -> Unit, onSucceed: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = noteService.delete(Auth.authUserData!!.jwt, id)
                if (response.isSuccessful) {
                    val list = _noteResponse.value?.toMutableList()
                    list?.removeIf { noteResponse -> noteResponse.id == id.toInt() }
                    _noteResponse.value = list

                    onSucceed()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportApiMessage(errorResponse.message)
                    onFailed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }

    fun tryPostNote(note: NoteCreateRequest, onFailed: () -> Unit, onSucceed: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = noteService.post(Auth.authUserData!!.jwt, note)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val currentList = _noteResponse.value?.toMutableList() ?: mutableListOf()
                    currentList.add(apiResponse!!.data)
                    _noteResponse.value = currentList
                    reportApiMessage(apiResponse.message)
                    onSucceed()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                    reportApiMessage(errorResponse.message)
                    onFailed()
                }
            } catch (exception: Exception) {
                reportApiMessage("Service currently not available")
                onFailed()
            }
        }
    }
    private fun reportApiMessage(message: String) {
        _apiMessage.value = message
    }
}