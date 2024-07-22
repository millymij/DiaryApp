package com.example.fragments
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This class represents the main activity of the app.
 * It contains the tab layout and view pager.
 */
class MainActivity : AppCompatActivity() {

    /**
     * This method is called when the activity is created.
     * It sets the content view and the toolbar.
     * It also sets up the tab layout and view pager.
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "My Diary App"

        val tabLayout: TabLayout = findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("Pick Date"))
        tabLayout.addTab(tabLayout.newTab().setText("Write Entry"))
        tabLayout.addTab(tabLayout.newTab().setText("View Entries"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    val page = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
                    if (page is Page1Fragment) {
                        page.showDatePickerDialog()
                    }
                }
            }
        })

        val adapter = PageAdapter(this,3)
        viewPager.setAdapter(adapter)

        TabLayoutMediator(tabLayout, viewPager)
        { tab, position ->
            when (position) {
                0 -> tab.text = "Pick Date"
                1 -> tab.text = "Write Entry"
                2 -> tab.text = "View Entries"
            }
        }.attach()
    }
}