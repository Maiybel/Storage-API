//package com.example.storageapi
//
//import android.Manifest
//import android.app.AlertDialog
//import android.content.ContentValues
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.io.OutputStream
//
//class MainActivity : AppCompatActivity() {
//
//    private val REQUEST_EXTERNAL_STORAGE = 1
//    private val PERMISSIONS_STORAGE = arrayOf(
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Check for permissions
//        requestStoragePermissions()
//
//        // Set up buttons
//        findViewById<Button>(R.id.btnCopyImage).setOnClickListener {
//            copyImageFile()
//        }
//
//        findViewById<Button>(R.id.btnListFiles).setOnClickListener {
//            listFilesInDirectory()
//        }
//
//        findViewById<Button>(R.id.btnDeleteFile).setOnClickListener {
//            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
//            showDeleteConfirmationDialog(file)
//        }
//
//        findViewById<Button>(R.id.btnShareFile).setOnClickListener {
//            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
//            if (file.exists()) {
//                shareFile(file)
//            } else {
//                Toast.makeText(this, "File doesn't exist yet. Copy image first.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    // Exercise 1: Copy an image from one folder to another
//    private fun copyImageFile() {
//        try {
//            // Source file from resources
//            val inputStream = resources.openRawResource(R.raw.sample_image)
//
//            // Destination file in app-specific external storage
//            val destDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            if (!destDir!!.exists()) {
//                destDir.mkdirs()
//            }
//
//            val destFile = File(destDir, "copied_image.jpg")
//            val outputStream = FileOutputStream(destFile)
//
//            // Copy the file
//            inputStream.copyTo(outputStream)
//            inputStream.close()
//            outputStream.close()
//
//            Toast.makeText(this, "Image copied successfully to ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
//
//        } catch (e: IOException) {
//            Toast.makeText(this, "Failed to copy image: ${e.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//    // Alternative copy method for MediaStore (Android 10+)
//    private fun copyImageToMediaStore() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            try {
//                // Source image from resources
//                val bitmap = BitmapFactory.decodeResource(resources, R.raw.sample_image)
//
//                // Create content values for new image
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.Images.Media.DISPLAY_NAME, "copied_image.jpg")
//                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//                    put(MediaStore.Images.Media.IS_PENDING, 1)
//                }
//
//                // Insert into MediaStore
//                val resolver = contentResolver
//                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//                // Write the image
//                uri?.let {
//                    val outputStream: OutputStream? = resolver.openOutputStream(it)
//                    outputStream?.use { stream ->
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    }
//
//                    // Update IS_PENDING flag
//                    contentValues.clear()
//                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
//                    resolver.update(it, contentValues, null, null)
//
//                    Toast.makeText(this, "Image copied to Media Store", Toast.LENGTH_LONG).show()
//                }
//
//            } catch (e: Exception) {
//                Toast.makeText(this, "Failed to copy to Media Store: ${e.message}", Toast.LENGTH_LONG).show()
//                e.printStackTrace()
//            }
//        }
//    }
//
//    // Exercise 2: List all files in a directory
//    private fun listFilesInDirectory() {
//        val picturesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val textView = findViewById<TextView>(R.id.tvFileList)
//        val filesList = StringBuilder()
//
//        if (picturesDir != null && picturesDir.exists()) {
//            val files = picturesDir.listFiles()
//
//            if (files != null && files.isNotEmpty()) {
//                filesList.append("Files in ${picturesDir.absolutePath}:\n\n")
//
//                for (file in files) {
//                    filesList.append("- ${file.name} (${file.length() / 1024} KB)\n")
//                }
//            } else {
//                filesList.append("No files found in ${picturesDir.absolutePath}")
//            }
//        } else {
//            filesList.append("Directory does not exist")
//        }
//
//        textView.text = filesList.toString()
//    }
//
//    // Exercise 3: Delete a selected file with confirmation dialog (Bonus)
//    private fun showDeleteConfirmationDialog(file: File) {
//        AlertDialog.Builder(this)
//            .setTitle("Delete File")
//            .setMessage("Are you sure you want to delete ${file.name}?")
//            .setPositiveButton("Delete") { _, _ ->
//                deleteFile(file)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun deleteFile(file: File) {
//        if (file.exists()) {
//            if (file.delete()) {
//                Toast.makeText(this, "${file.name} deleted successfully", Toast.LENGTH_SHORT).show()
//                // Refresh the file list
//                listFilesInDirectory()
//            } else {
//                Toast.makeText(this, "Failed to delete ${file.name}", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Added security best practice: Sharing files with FileProvider
//    private fun shareFile(file: File) {
//        try {
//            // Get URI from FileProvider
//            val fileUri = FileProvider.getUriForFile(
//                this,
//                "${applicationContext.packageName}.fileprovider",
//                file
//            )
//
//            // Create intent to share the file
//            val shareIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_STREAM, fileUri)
//                type = "image/jpeg"
//                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            }
//
//            startActivity(Intent.createChooser(shareIntent, "Share image using"))
//
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error sharing file: ${e.message}", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//    }
//
//    // Request storage permissions
//    private fun requestStoragePermissions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            val permission = ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            )
//
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//                )
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

package com.example.storageapi

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for permissions
        requestStoragePermissions()

        // Set up buttons on the application
        findViewById<Button>(R.id.btnCopyImage).setOnClickListener {
            copyImageFile()
        }

        findViewById<Button>(R.id.btnListFiles).setOnClickListener {
            listFilesInDirectory()
        }

//        findViewById<Button>(R.id.btnDeleteFile).setOnClickListener {
//            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
//            showDeleteConfirmationDialog(file)
//        }
        findViewById<Button>(R.id.btnDeleteFile).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, use MediaStore API
                showMediaStoreDeleteConfirmationDialog("copied_image.jpg")
            } else {
                // For older Android versions, use direct file access
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
                showDeleteConfirmationDialog(file)
            }
        }


//        findViewById<Button>(R.id.btnShareFile).setOnClickListener {
//            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
//            if (file.exists()) {
//                shareFile(file)
//            } else {
//                Toast.makeText(this, "File doesn't exist yet. Copy image first.", Toast.LENGTH_SHORT).show()
//            }
//        }
        findViewById<Button>(R.id.btnShareFile).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, find and share the file using MediaStore
                shareMediaStoreImage("copied_image.jpg")
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "copied_image.jpg")
                if (file.exists()) {
                    shareFile(file)
                } else {
                    Toast.makeText(this, "File doesn't exist yet. Copy image first.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
// method for sharing MediaStore images
    private fun shareMediaStoreImage(fileName: String) {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val imageUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                // Create intent to share the file
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    type = "image/jpeg"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                startActivity(Intent.createChooser(shareIntent, "Share image using"))
            } else {
                Toast.makeText(this, "File doesn't exist yet. Copy image first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Exercise 1: Copy an image from one folder to another
//    Curretly saves the copied image to the app external storage directory
//    1. make it copy to a public directory where other images are
//    2. use shared external storage and MediaStore APi
//    private fun copyImageFile() {
//        try {
//            // Source file from resources
//            val inputStream = resources.openRawResource(R.raw.sample_image)
//
//            // Destination file in app-specific external storage
//            val destDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            if (!destDir!!.exists()) {
//                destDir.mkdirs()
//            }
//
//            val destFile = File(destDir, "copied_image.jpg")
//            val outputStream = FileOutputStream(destFile)
//
//            // Copy the file
//            inputStream.copyTo(outputStream)
//            inputStream.close()
//            outputStream.close()
//
//            Toast.makeText(this, "Image copied successfully to ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
//
//        } catch (e: IOException) {
//            Toast.makeText(this, "Failed to copy image: ${e.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }

    private fun copyImageFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ use MediaStore API
            copyImageToMediaStore()
        } else {
            // For older versions, use legacy approach
            copyImageToSharedStorage()
        }
    }

//    To copy yo shared external storage
    private fun copyImageToSharedStorage() {
        try {
            // Source file from resources
            val inputStream = resources.openRawResource(R.raw.sample_image)

            // Destination file in shared Pictures directory
            val publicPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!publicPicturesDir.exists()) {
                publicPicturesDir.mkdirs()
            }

            val destFile = File(publicPicturesDir, "copied_image.jpg")
            val outputStream = FileOutputStream(destFile)

            // Copy the file
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            Toast.makeText(this, "Image copied successfully to ${destFile.absolutePath}", Toast.LENGTH_LONG).show()

            // Make the file visible in the gallery
            MediaStore.Images.Media.insertImage(contentResolver,
                destFile.absolutePath, destFile.name, "Image copied by Storage API Demo")

        } catch (e: IOException) {
            Toast.makeText(this, "Failed to copy image: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // Alternative copy method for MediaStore (Android 10+)
    private fun copyImageToMediaStore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                // Source image from resources
                val bitmap = BitmapFactory.decodeResource(resources, R.raw.sample_image)

                // Create content values for new image
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "copied_image.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                // Insert into MediaStore
                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Write the image
                uri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    }

                    // Update IS_PENDING flag
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)

                    Toast.makeText(this, "Image copied to Media Store", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Failed to copy to Media Store: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    // Exercise 2: List all files in a directory
    // Exercise 2: List all files in a directory
    private fun listFilesInDirectory() {
        val textView = findViewById<TextView>(R.id.tvFileList)
        val filesList = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, query the MediaStore
            listMediaStoreImages(filesList)
        } else {
            // For older Android versions, read from shared storage directly
            val publicPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            listDirectoryFiles(publicPicturesDir, filesList)
        }

        textView.text = filesList.toString()
    }

    private fun listDirectoryFiles(directory: File, filesList: StringBuilder) {
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()

            if (files != null && files.isNotEmpty()) {
                filesList.append("Files in ${directory.absolutePath}:\n\n")

                for (file in files) {
                    if (file.isFile) {
                        filesList.append("- ${file.name} (${file.length() / 1024} KB)\n")
                    }
                }
            } else {
                filesList.append("No files found in ${directory.absolutePath}")
            }
        } else {
            filesList.append("Directory does not exist")
        }
    }

    private fun listMediaStoreImages(filesList: StringBuilder) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )

        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_PICTURES}%")

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            filesList.append("Images in Pictures directory:\n\n")

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameColumn)
                    val size = cursor.getLong(sizeColumn) / 1024 // Convert to KB
                    filesList.append("- $name ($size KB)\n")
                }
            } else {
                filesList.append("No images found")
            }
        }
    }
    // Exercise 3: Delete a selected file with confirmation dialog (Bonus)
    private fun showDeleteConfirmationDialog(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete ${file.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteFile(file)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(this, "${file.name} deleted successfully", Toast.LENGTH_SHORT).show()
                // Refresh the file list
                listFilesInDirectory()
            } else {
                Toast.makeText(this, "Failed to delete ${file.name}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    // Added security best practice: Sharing files with FileProvider
    private fun shareFile(file: File) {
        try {
            // Get URI from FileProvider
            val fileUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )

            // Create intent to share the file
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/jpeg"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            startActivity(Intent.createChooser(shareIntent, "Share image using"))

        } catch (e: Exception) {
            Toast.makeText(this, "Error sharing file: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Request storage permissions
    private fun requestStoragePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMediaStoreDeleteConfirmationDialog(fileName: String) {
        // First, check if the file exists in MediaStore
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val imageUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Delete File")
                    .setMessage("Are you sure you want to delete $fileName?")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteMediaStoreFile(imageUri, fileName)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // method to actually delete the file through MediaStore
    private fun deleteMediaStoreFile(uri: Uri, fileName: String) {
        try {
            // Delete the file using ContentResolver
            val deletedRows = contentResolver.delete(uri, null, null)

            if (deletedRows > 0) {
                Toast.makeText(this, "$fileName deleted successfully", Toast.LENGTH_SHORT).show()
                // Refresh the file list
                listFilesInDirectory()
            } else {
                Toast.makeText(this, "Failed to delete $fileName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error deleting file: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}