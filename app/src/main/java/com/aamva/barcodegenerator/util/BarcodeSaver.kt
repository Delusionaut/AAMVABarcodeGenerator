package com.aamva.barcodegenerator.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.aamva.barcodegenerator.model.HistoryItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Utility class for saving barcodes to device storage
 */
object BarcodeSaver {
    
    private const val FOLDER_NAME = "AAMVA_Barcodes"
    
    /**
     * Save a bitmap to the device's Pictures directory
     * Filename format: Firstname_First3LettersLastname_DOB_State.png
     */
    fun saveBarcodeToStorage(
        context: Context,
        bitmap: Bitmap,
        firstName: String,
        familyName: String,
        state: String,
        dateOfBirth: String
    ): String? {
        val fileName = generateFileName(firstName, familyName, state, dateOfBirth)
        val relativePath = getSaveLocation(context)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToMediaStore(context, bitmap, fileName, relativePath)
        } else {
            saveImageToExternalStorage(context, bitmap, fileName, relativePath)
        }
    }
    
    private fun getSaveLocation(context: Context): String {
        val prefs = context.getSharedPreferences("AAMVA_Settings", Context.MODE_PRIVATE)
        return prefs.getString("save_location", "Pictures/AAMVA_Barcodes") ?: "Pictures/AAMVA_Barcodes"
    }
    
    /**
     * Generate filename: Firstname_First3LettersLastname_DOB_State.png
     * Example: John_Doe_01011990_CA.png
     */
    private fun generateFileName(
        firstName: String,
        familyName: String,
        state: String,
        dateOfBirth: String
    ): String {
        val cleanFirstName = firstName.replace(Regex("[^a-zA-Z]"), "").take(20)
        val cleanLastName = familyName.replace(Regex("[^a-zA-Z]"), "").take(3)
        val cleanState = state.replace(Regex("[^a-zA-Z]"), "").take(2)
        val cleanDOB = dateOfBirth.filter { it.isDigit() }.take(8)
        
        return "${cleanFirstName}_${cleanLastName}_${cleanDOB}_${cleanState}.png"
    }
    
    /**
     * Save image using MediaStore (Android 10+)
     */
    private fun saveImageToMediaStore(
        context: Context, 
        bitmap: Bitmap, 
        fileName: String,
        relativePath: String
    ): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        return uri?.let { imageUri ->
            try {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
                
                imageUri.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                resolver.delete(imageUri, null, null)
                null
            }
        }
    }
    
    /**
     * Save image to external storage (Android 9 and below)
     */
    @Suppress("DEPRECATION")
    private fun saveImageToExternalStorage(
        context: Context, 
        bitmap: Bitmap, 
        fileName: String,
        relativePath: String
    ): String? {
        val parts = relativePath.split("/")
        var baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        
        if (parts.size >= 2) {
            when (parts[0]) {
                "Downloads" -> baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                "Documents" -> baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            }
        }
        
        val appDir = File(baseDir, "AAMVA_Barcodes")
        
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        
        val file = File(appDir, fileName)
        
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Create a history item from saved barcode
     */
    fun createHistoryItem(
        firstName: String,
        familyName: String,
        dateOfBirth: String,
        customerId: String,
        filePath: String
    ): HistoryItem {
        return HistoryItem(
            id = UUID.randomUUID().toString(),
            firstName = firstName,
            familyName = familyName,
            dateOfBirth = dateOfBirth,
            customerId = customerId,
            timestamp = Date(),
            filePath = filePath
        )
    }
}
