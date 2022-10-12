package nl.hva.task02.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nl.hva.task02.R
import nl.hva.task02.databinding.FragmentQuestionBinding
import nl.hva.task02.model.Question
import nl.hva.task02.viewmodel.QuestViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private var radioButtons: ArrayList<RadioButton> = arrayListOf()

    private var quest: List<Question> = arrayListOf()

    private val viewModel: QuestViewModel by activityViewModels()

    // question counter (+ 1) in the top right corner (e.g. 1/3)
    private var questionCounter: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // all radio buttons in one list for to iterate easily
        radioButtons.addAll(
            arrayListOf(
                binding.rbAnswerOne,
                binding.rbAnswerTwo,
                binding.rbAnswerThree,
                binding.rbAnswerFour
            )
        )

        questionCounter = 0
        observeQuest()

        binding.btnConfirm.setOnClickListener { onConfirmClick() }
    }

    private fun observeQuest() {
        viewModel.getQuest()

        viewModel.quest.observe(viewLifecycleOwner) {
            quest = it

            // set/update question counter
            binding.tvQuestionCounter.text =
                getString(R.string.question_counter, questionCounter + 1, quest.size)

            // set question and answers
            binding.tvQuestion.text = quest[questionCounter].question
            radioButtons.forEachIndexed { index, _ ->
                radioButtons[index].text = quest[questionCounter].choices?.get(index)
            }
        }

        viewModel.errorText.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onConfirmClick() {
        val checkedRadioButtonId = binding.rgAnswers.checkedRadioButtonId
        val selectedAnswer = view?.findViewById<RadioButton>(checkedRadioButtonId)?.text

        val correctAnswer = quest[questionCounter].correctAnswer

        if (selectedAnswer == correctAnswer) {
            // if it wasn't the last question, increase counter and observeQuest() again
            if ((questionCounter + 1) < quest.size) {
                questionCounter++
                observeQuest()
            } else {
                Toast.makeText(activity, getString(R.string.quest_completed), Toast.LENGTH_SHORT)
                    .show()

                findNavController().navigate(R.id.action_questionFragment_to_startFragment)
            }
        } else {
            Toast.makeText(activity, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}