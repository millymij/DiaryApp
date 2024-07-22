package com.example.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

/**
 * This class serves as an adapter for the list view in the main activity.
 * It represents how each entry should be displayed in the list view.
 * @param context The context of the adapter.
 * @param entries The list of entries to be displayed.
 * @param onEditentry The function to be called when the user selects the edit option.
 */

class EntryAdapter(context: Context,
                   private var entries: List<DiaryEntry>,
    private val onEditentry : (DiaryEntry) -> Unit
)
    : ArrayAdapter<DiaryEntry>(context, 0, entries) {
    val databaseAdapter = DatabaseAdapter(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.entry_item, parent, false)
        val textViewEntry = view.findViewById<TextView>(R.id.textViewEntry)
        val spinnerOptions = view.findViewById<Spinner>(R.id.spinnerOptions)
        val diaryEntry = getItem(position)
        val entryText = diaryEntry?.title ?: ""
        val entryPreview = diaryEntry?.entry?.take(30) ?: ""
        val styledText = SpannableString("$entryText\n$entryPreview...")

        styledText.setSpan(StyleSpan(Typeface.BOLD), 0, entryText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewEntry.text = styledText

        spinnerOptions.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, listOf("Choose action", "View/Edit", "Delete"))
        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                when (parent.getItemAtPosition(pos)) {
                    "View/Edit" -> {
                        diaryEntry?.let { onEditentry(it) }
                        notifyDataSetChanged()
                    }
                    "Delete" -> {
                        diaryEntry?.let { deleteEntry(it) }
                        notifyDataSetChanged()
                    }
                }
                spinnerOptions.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinnerOptions.setSelection(0)
        notifyDataSetChanged()
        return view
    }

    /**
     * This function is used to delte an entry from the database.
     * @param entry The entry to be deleted.
     */
    private fun deleteEntry(entry: DiaryEntry) {
        Thread {
            databaseAdapter.open()
            databaseAdapter.deleteDiaryEntry(entry.id.toString())
            databaseAdapter.close()
            (context as Activity).runOnUiThread {
                remove(entry)
                Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }.start()
    }
}
