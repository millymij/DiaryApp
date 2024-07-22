package com.example.fragments
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * PageAdapter is a FragmentStateAdapter that will return a fragment corresponding to
 * one of the sections/tabs/pages.
 * @param fa FragmentActivity
 * @param mNumOfTabs Int
 */
class PageAdapter(fa: FragmentActivity, private val mNumOfTabs: Int) :
    FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return mNumOfTabs
    }
    override fun createFragment(position: Int):
            Fragment { return when (position) {
                0 -> Page1Fragment()
                1 -> Page2Fragment()
                2 -> Page3Fragment()
                else -> Page1Fragment()
            } } }