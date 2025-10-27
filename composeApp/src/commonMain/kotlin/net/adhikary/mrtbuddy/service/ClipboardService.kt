package net.adhikary.mrtbuddy.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClipboardService() {
    private var context: Context? = null

    fun setContext(context: Context) {
        this.context = context
    }

    suspend fun copyToClipboard(text: String): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val androidContext = context ?: return@withContext false
                val clipboard = androidContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("MRT Buddy Export", text)
                clipboard.setPrimaryClip(clip)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}