package nl.hva.task02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.hva.task02.model.Question
import nl.hva.task02.repository.QuestRepository

class QuestViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FIRESTORE"
    private val questRepository: QuestRepository = QuestRepository()

    val quest: LiveData<List<Question>> = questRepository.quest

    private val _errorText: MutableLiveData<String> = MutableLiveData()
    val errorText: LiveData<String>
        get() = _errorText

    fun getQuest() {
        viewModelScope.launch {
            try {
                questRepository.getQuest()
            } catch (e: QuestRepository.QuestRetrievalError) {
                val errorMsg = "Something went wrong while retrieving quest"
                Log.e(TAG, e.message ?: errorMsg)
                _errorText.value = errorMsg
            }
        }
    }
}