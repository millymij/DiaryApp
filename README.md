## Diary App (2023)

### Overview
This Android app allows users to create, view, edit, and delete diary entries. It utilizes a SQLite database for data storage and offers features like date selection, entry filtering, and sorting.

### Features
* Create new diary entries with title, date, and text content.
* View a list of diary entries, filterable by date and sortable by title.
* Edit existing diary entries, including modifying title, text, and date.
* Delete individual or all diary entries.

### Tech Stack
* Kotlin
* Android SDK
* SQLite

### Project Structure
The project is organized into the following key components:

* Fragments: Page1Fragment (date selection), Page2Fragment (diary entry), Page3Fragment (entry list)
* Activities: MainActivity (main app entry point), EditEntryActivity (for editing entries)
* Adapters: EntryAdapter (for displaying diary entries in a list)
* Database: DatabaseHelper, DatabaseAdapter
* Models: DiaryEntry (data class representing a diary entry)
* ViewModels: MyViewModel (for managing UI state)

### Getting Started
1) Clone this repository:
2) Open the project in Android Studio.
3) Connect a physical device or create an emulator.
4) Build and run the app.


