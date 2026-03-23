package com.aamva.barcodegenerator.util

import android.content.Context
import android.content.SharedPreferences
import com.aamva.barcodegenerator.model.HistoryItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Manages barcode generation history using SharedPreferences
 */
class HistoryManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "barcode_history"
        private const val KEY_HISTORY = "history_list"
        private const val MAX_HISTORY_ITEMS = 50
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    
    /**
     * Get all history items
     */
    fun getHistory(): List<HistoryItem> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(json)
            val items = mutableListOf<HistoryItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                items.add(parseHistoryItem(obj))
            }
            items.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Add a new history item
     */
    fun addHistoryItem(item: HistoryItem) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.add(0, item)
        
        // Keep only the most recent items
        val trimmedHistory = currentHistory.take(MAX_HISTORY_ITEMS)
        saveHistory(trimmedHistory)
    }
    
    /**
     * Delete a history item by ID
     */
    fun deleteHistoryItem(id: String) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.id == id }
        saveHistory(currentHistory)
    }
    
    /**
     * Clear all history
     */
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
    
    private fun saveHistory(items: List<HistoryItem>) {
        val jsonArray = JSONArray()
        items.forEach { item ->
            jsonArray.put(historyItemToJson(item))
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
    
    private fun historyItemToJson(item: HistoryItem): JSONObject {
        return JSONObject().apply {
            put("id", item.id)
            put("firstName", item.firstName)
            put("familyName", item.familyName)
            put("dateOfBirth", item.dateOfBirth)
            put("customerId", item.customerId)
            put("timestamp", dateFormat.format(item.timestamp))
            put("filePath", item.filePath)
        }
    }
    
    private fun parseHistoryItem(json: JSONObject): HistoryItem {
        return HistoryItem(
            id = json.getString("id"),
            firstName = json.getString("firstName"),
            familyName = json.getString("familyName"),
            dateOfBirth = json.getString("dateOfBirth"),
            customerId = json.getString("customerId"),
            timestamp = dateFormat.parse(json.getString("timestamp")) ?: java.util.Date(),
            filePath = json.getString("filePath")
        )
    }
}