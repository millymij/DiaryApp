package com.example.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2

/**
 * Page2Fragment is the second page of the ViewPager2.
 * It allows the user to enter a diary entry for the selected date.
 * The user can also save the diary entry to the database.
 */
class Page2Fragment : Fragment() {
    lateinit var viewModel: MyViewModel
    private lateinit var textView: TextView
    private lateinit var buttonClear: Button
    private lateinit var buttonSave: Button
    private var selectedDate: String? = null
    private lateinit var editTextDiaryEntry: EditText
    private lateinit var editTextTitle: EditText

    /**
     * onCreateView is called when the fragment is created to create the view.
     * It inflates the layout for this fragment and sets up the buttons.
     * It also sets up the observer for the selectedDate and currentEntry.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        val view = inflater.inflate(R.layout.page2_fragment, container, false)

        textView = view.findViewById(R.id.textViewF2)
        editTextDiaryEntry = view.findViewById(R.id.editTextDiaryEntry)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        buttonClear = view.findViewById(R.id.buttonClear)
        buttonSave = view.findViewById(R.id.buttonSave)

        buttonClear.setOnClickListener {
            editTextDiaryEntry.text.clear()
        }

        buttonSave.setOnClickListener {
            val diaryText = editTextDiaryEntry.text.toString()
            if (diaryText.isNotEmpty()) {
                // val currentDate = viewModel.selectedDate.value ?: Date()
                saveDiaryEntry(selectedDate.toString())

                Toast.makeText(context, "Entry saved", Toast.LENGTH_SHORT).show()
                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
                viewPager.currentItem = 2
                editTextTitle.text.clear()
                editTextDiaryEntry.text.clear()
            }
         else {
                Toast.makeText(context, "Please enter a diary entry.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    /**
     * onViewCreated is called after onCreateView is called.
     * It sets up the observer for the selectedDate and currentEntry.
     * It also sets the text of the textView to the selectedDate and editTextDiaryEntry to the currentEntry.
     * @param view The view returned by onCreateView.
     * @param savedInstanceState The saved instance state of the fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MyViewModel::class.java)
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { date ->
            selectedDate = date
            textView.text = date

            viewModel.currentEntry.observe(viewLifecycleOwner, Observer { entry ->
                editTextDiaryEntry.setText(entry)
            })
        })
    }

    /**
     * onResume is called when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * saveDiaryEntry saves the diary entry to the database.
     * @param date The date of the diary entry.
     */
    private fun saveDiaryEntry(date: String) {
        val databaseAdapter = DatabaseAdapter(requireActivity())
        databaseAdapter.open()

        // change
        val media = null
        val diaryTitle = editTextTitle.text.toString()
        val diaryText = editTextDiaryEntry.text.toString()

        val diaryEntry = DiaryEntry(
            id = 0, // assume id is 0 for now, since its autoincremented
            date = date,
            title = diaryTitle,
            media = media, // assume media is null for now
            entry = diaryText
        )
        databaseAdapter.insertDiaryEntry(diaryEntry)
        databaseAdapter.close()
        }
    }

