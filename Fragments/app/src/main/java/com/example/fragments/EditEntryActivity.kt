package com.example.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * This activity is used to edit an existing diary entry.
 * It is used to display the details of the entry and allow the user to edit them.
 * The user can also upload/change the image associated with the entry.
 * All the changes are saved to the database.
 */

class EditEntryActivity: AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 101
    private val REQUEST_IMAGE_PICK = 102

    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private var selectedImagePath: String? = null
    private val viewModel = MyViewModel()
    private var hasReadPermission = false
    private val databaseAdapter = DatabaseAdapter(this)

    /**
     * This function is called when the activity is created.
     * It sets the layout of the activity and initializes the views.
     * It also sets the onClickListeners for the buttons.
     * It also extracts the details of the entry from the intent and displays them.
     * It also updates the database with the changes made by the user.
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_entry_activity)

        if (savedInstanceState != null) {
            hasReadPermission = savedInstanceState.getBoolean("READ_PERMISSION", false)
        }

        val dateEditText = findViewById<EditText>(R.id.editTextDate)
        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val contentEditText = findViewById<EditText>(R.id.editTextEntryContent)
        val mediaButton = findViewById<Button>(R.id.buttonMedia)
        val saveButton = findViewById<Button>(R.id.buttonSave)
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        imageView = findViewById(R.id.imageViewSelectedMedia)

        // Extracting the details from the intent
        val entryId = intent.getIntExtra("ENTRY_ID", -1)
        val entryDate = intent.getStringExtra("ENTRY_DATE")
        val entryTitle = intent.getStringExtra("ENTRY_TITLE") ?: ""
        val entryMedia = intent.getStringExtra("ENTRY_MEDIA") ?: ""
        val entryContent = intent.getStringExtra("ENTRY_TEXT")

        dateEditText.setText(entryDate)
        titleEditText.setText(entryTitle)
        contentEditText.setText(entryContent)

        entryMedia?.let {
            selectedImagePath = it
            imageView.setImageURI(Uri.parse(it))
        }

        buttonBack.setOnClickListener {
            finish()
        }

        mediaButton.setOnClickListener { (checkAndRequestPermissions()) }

        saveButton.setOnClickListener {
            val updatedDate = dateEditText.text.toString()
            val updatedTitle = titleEditText.text.toString()
            val updatedMedia = selectedImagePath  // This holds the updated image path
            val updatedContent = contentEditText.text.toString()

            databaseAdapter.open()
            selectedImagePath?.let { it1 ->
                databaseAdapter.updateDiaryEntry(
                    entryId, updatedDate, updatedTitle,
                    it1, updatedContent
                )
            }
            databaseAdapter.close()

            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("ENTRY_ID", entryId)
                putExtra("ENTRY_DATE", updatedDate)
                putExtra("ENTRY_TITLE", updatedTitle)
                putExtra("ENTRY_MEDIA", updatedMedia)
                putExtra("ENTRY_TEXT", updatedContent)
            })
            finish()
        }
    }

    /**
     * This function is used to check if the app has the required permissions.
     * If the app does not have the required permissions, it requests the user for the permissions.
     * If the app has the required permissions, it opens the gallery.
     */
    private fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION)
        val listPermissionsNeeded = ArrayList<String>()
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission) } }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        } else {
            openGallery() } }


    /**
     * This function is called when the user responds to the permission request.
     * It checks if the user has granted the required permissions.
     * If the user has granted the required permissions, it opens the gallery.
     * If the user has denied the required permissions, it displays a toast message.
     * @param requestCode The request code of the permission request.
     * @param permissions The list of permissions requested.
     * @param grantResults The list of results for the permissions requested.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            hasReadPermission = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (hasReadPermission) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                openGallery()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * This function is called when the activity is destroyed.
     * It saves the state of the activity.
     * @param outState The bundle in which the state is saved.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("READ_PERMISSION", hasReadPermission)
    }

    /**
     * This function is called when the user selects an image from the gallery.
     * It sets the image in the imageView.
     * @param requestCode The request code of the image pick request.
     * @param resultCode The result code of the image pick request.
     * @param data The intent containing the image data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImagePath = selectedImageUri?.path
            imageView.setImageURI(selectedImageUri)
        }
    }

    /**
     * This function is used to open the gallery.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    /**
     * This function is called when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
    }
}