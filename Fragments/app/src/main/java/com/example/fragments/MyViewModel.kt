package com.example.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * This class is used to store the data that needs to be shared between fragments.
 * It is used to store the selected date and the current entry.
 */
class MyViewModel : ViewModel() {
    private val _value = MutableLiveData<String>()
    private val _selectedDate = MutableLiveData<String>()
    private val _currentEntry = MutableLiveData<String>()
    val selectedDate: MutableLiveData<String>
        get() = _selectedDate
   val currentEntry: MutableLiveData<String>
        get() = _currentEntry
    val value: MutableLiveData<String>
        get() = _value
    init { _value.value = "default"
    }

    /**
     * This function is used to update the current entry.
     * @param newSelectedDate The new date to be stored.
     */
    fun updateSelectedDate(newSelectedDate: String) {
        _selectedDate.value = newSelectedDate
    }
}