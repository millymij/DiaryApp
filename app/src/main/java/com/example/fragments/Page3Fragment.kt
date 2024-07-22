package com.example.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2

/**
 * Page3Fragment is the fragment that displays the diary entries for the selected date.
 * Allowing the user to view, edit, and delete entries.
 */
class Page3Fragment : Fragment() {
    private lateinit var viewModel: MyViewModel
    private lateinit var selectedDateTextView: TextView
    private lateinit var entriesListView: ListView
    private lateinit var sortSpinner: Spinner
    private val EDIT_ENTRY_REQUEST_CODE = 1

    /**
     * onCreateView is called when the fragment is created.
     * It sets up the view and the buttons.
     * It also sets up the spinner for sorting the entries.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        val view = inflater.inflate(R.layout.page3_fragment, container, false)


        selectedDateTextView = view.findViewById(R.id.textViewF3)
        entriesListView = view.findViewById(R.id.listViewDiaryEntries)

        val buttonChooseDate = view.findViewById<View>(R.id.buttonChooseDifferentDate)
        buttonChooseDate.setOnClickListener {
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            viewPager.currentItem = 0
            viewModel.selectedDate.value?.let { date ->
                viewModel.updateSelectedDate(date)
            }
        }
        val buttonShowAll = view.findViewById<View>(R.id.buttonShowAllEntries)
        buttonShowAll.setOnClickListener{
            loadAllEntries()
            selectedDateTextView.text = "All Entries"
        }

        val buttonDeleteAll = view.findViewById<View>(R.id.buttonDeleteAll)
        buttonDeleteAll.setOnClickListener {
            // make sure user wants to delete all entries
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> { //If "YES", delete all entries
                            val databaseAdapter = DatabaseAdapter(requireActivity())
                            databaseAdapter.open()
                            databaseAdapter.deleteAllDiaryEntries()
                            databaseAdapter.close()
                            onResume()
                            Toast.makeText(getActivity(), "All entries have been removed from the database.", Toast.LENGTH_SHORT).show()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> { //else return to fragment
                            Toast.makeText(getActivity(), "Command Undone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("WARNING: Are you sure you want to delete all entries? This action cannot be undone!")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
        return view
    }

    /**
     * onViewCreated is called after the view is created.
     * It sets up the spinner for sorting the entries.
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            selectedDateTextView.text = date

            sortSpinner = view.findViewById(R.id.sortSpinner)
            val sortOptions = arrayOf("No Sorting", "Alphabetical", "Reverse Alphabetical")
            sortSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sortOptions)

            sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> loadEntriesForDate(date)
                        1 -> sortEntriesAlphabetically(true)
                        2 -> sortEntriesAlphabetically(false)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
            savedInstanceState?.let {
                val listPosition = it.getInt("listPosition")
                entriesListView.setSelection(listPosition)
            }
        }
    }

    /**
     * loadEntriesForDate loads the entries for the selected date.
     * @param date
     */
    @SuppressLint("Range")
    private fun loadEntriesForDate(date: String) {
        val databaseAdapter = DatabaseAdapter(requireContext())
        databaseAdapter.open()
        val cursor = databaseAdapter.getAllDiaryEntriesForDate(date)
        val entries = mutableListOf<DiaryEntry>()

        while (cursor?.moveToNext() == true) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_TITLE)) ?: "No Title"
            val content = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ENTRY))
            val media = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_MEDIA)) ?: ""

            entries.add(DiaryEntry(id, date, title, media, content))
        }
        cursor?.close()
        val adapter = EntryAdapter(requireContext(), entries) { entry ->
            editEntry(entry)
        }
        entriesListView.adapter = adapter
        databaseAdapter.close()
    }

    /**
     * loadAllEntries loads all the entries in the database.
     */
    @SuppressLint("Range")
    private fun loadAllEntries() {
        val databaseAdapter = DatabaseAdapter(requireContext())
        databaseAdapter.open()
        val cursor = databaseAdapter.getAllDiaryEntries()
        val entries = mutableListOf<DiaryEntry>()

        while (cursor?.moveToNext() == true) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_TITLE)) ?: "No Title"
            val content = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ENTRY))
            val date = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_DATE))
            val media = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_MEDIA)) ?: ""

            entries.add(DiaryEntry(id, date, title, media, content))
        }
        cursor?.close()
        val adapter = EntryAdapter(requireContext(), entries) { entry ->
            editEntry(entry)
        }
        entriesListView.adapter = adapter
        databaseAdapter.close()
    }


    /**
     * sortEntriesAlphabetically sorts the entries alphabetically.
     * @param ascending
     */
    @SuppressLint("Range")
    private fun sortEntriesAlphabetically(ascending: Boolean) {
        val databaseAdapter = DatabaseAdapter(requireContext())
        databaseAdapter.open()
        val cursor = databaseAdapter.getAllDiaryEntries()
        val entries = mutableListOf<DiaryEntry>()

        while (cursor?.moveToNext() == true) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_TITLE)) ?: "No Title"
            val content = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_ENTRY))
            val date = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_DATE))
            val media = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.COLUMN_MEDIA)) ?: ""

            entries.add(DiaryEntry(id, date, title, media, content))
        }
        cursor?.close()
        if (ascending) {
            entries.sortBy { it.title }
        } else {
            entries.sortByDescending { it.title }
        }
        val adapter = EntryAdapter(requireContext(), entries) { entry ->
            editEntry(entry)
        }
        entriesListView.adapter = adapter
        databaseAdapter.close()
    }

    /**
     * editEntry opens the EditEntryActivity to edit the selected entry.
     * @param entry
     */
    private fun editEntry(entry: DiaryEntry) {
        val intent = Intent(requireContext(), EditEntryActivity::class.java)
        intent.putExtra("ENTRY_ID", entry.id)
        intent.putExtra("ENTRY_DATE", entry.date)
        intent.putExtra("ENTRY_TITLE", entry.title)
        intent.putExtra("ENTRY_MEDIA", entry.media)
        intent.putExtra("ENTRY_TEXT", entry.entry)
        startActivityForResult(intent, EDIT_ENTRY_REQUEST_CODE)
    }

    /**
     * onActivityResult is called when the EditEntryActivity is finished.
     * It refreshes the entries list.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ENTRY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Refresh the entries list
            onResume()
        }
    }

    /**
     * onSaveInstanceState saves the list position when the fragment is paused.
     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("listPosition", entriesListView.firstVisiblePosition)
    }

    /**
     * onResume is called when the fragment is resumed.
     * It loads the entries for the selected date.
     */
    override fun onResume() {
        super.onResume()
        viewModel.selectedDate.value?.let {
            loadEntriesForDate(it)
        }
    }

}

