package nl.hva.task02.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import nl.hva.task02.model.Question

class QuestRepository {
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var questCollection = firestore.collection("questions")

    private val _quest: MutableLiveData<List<Question>> = MutableLiveData()
    val quest: LiveData<List<Question>>
        get() = _quest

    suspend fun getQuest() {
        try {
            //firestore has support for coroutines via the extra dependency we've added :)
            withTimeout(5_000) {
                val data = questCollection
                    .get()
                    .await()

                val questions: ArrayList<Question> = arrayListOf()
                for (document in data.documents) {
                    val question = document.getString("question")

                    @Suppress("UNCHECKED_CAST")
                    val choices = document.get("choices") as ArrayList<String>?
                    val correctAnswer = document.getString("correctAnswer")

                    questions.add(
                        Question(
                            question,
                            choices,
                            correctAnswer
                        )
                    )
                }

                _quest.value = questions
            }
        } catch (e: Exception) {
            throw QuestRetrievalError("Retrieval-firebase-task was unsuccessful")
        }
    }

    class QuestRetrievalError(message: String) : Exception(message)
}