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
import java.io.OutputStream
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
     * @param context Application context
     * @param bitmap The bitmap to save
     * @param firstName First name for the filename
     * @param familyName Family name for the filename
     * @return The file path if successful, null otherwise
     */
    fun saveBarcodeToStorage(
        context: Context,
        bitmap: Bitmap,
        firstName: String,
        familyName: String
    ): String? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "AAMVA_${familyName}_${firstName}_$timestamp.png"
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToMediaStore(context, bitmap, fileName)
        } else {
            saveImageToExternalStorage(context, bitmap, fileName)
        }
    }
    
    /**
     * Save image using MediaStore (Android 10+)
     */
    private fun saveImageToMediaStore(context: Context, bitmap: Bitmap, fileName: String): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$FOLDER_NAME")
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
    private fun saveImageToExternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, FOLDER_NAME)
        
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