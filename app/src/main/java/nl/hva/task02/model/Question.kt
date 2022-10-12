package nl.hva.task02.model

data class Question(
    val question: String?,
    val choices: ArrayList<String>?,
    val correctAnswer: String?
)