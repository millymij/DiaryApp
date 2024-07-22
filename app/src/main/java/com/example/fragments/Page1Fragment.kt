package com.example.fragments
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import java.util.Calendar

/**
 * Fragment to display the first page of the ViewPager, which allows the user to select a date.
 */
class Page1Fragment : Fragment() {
    lateinit var viewModel: MyViewModel
    private var dbHelper: DatabaseHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = activity?.run { ViewModelProvider(this)[MyViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        val view = inflater.inflate(R.layout.page1_fragment, container, false)

        return view }

    /**
     * Show a DatePickerDialog to allow the user to select a date. Then update the ViewModel with
     * the selected date and switch to the second tab.
     */
    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Handle the date selected
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            viewModel.updateSelectedDate(selectedDate)
            // once selected date, switch to tab 2
            val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPager)
            viewPager.currentItem = 1
        }, year, month, day).show()
    }

}