package com.hnv.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File
import java.util.*
import kotlin.math.roundToLong


object Utils {


    /**
     * Mở chợ ứng dụng, có thể là Play Store hoặc một chợ nào khác được cài trên máy
     * Nếu không có chợ ứng dụng thì mở trình duyệt
     * Nếu không có trình duyệt thì nên... crash
     */
    fun openAppStore(context: Context, packageName: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    /**
     * Mở ứng dụng @param packageName nếu đã cài hoặc mở chợ ứng dụng
     */
    fun openAppOrAppStore(context: Context, packageName: String) {
        val intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {//Bring user to the market
            openAppStore(context, packageName)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }





    fun getTemporaryStorageDir(context: Context): File? {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    fun getBearerParam(bearer: String): String {
        return String.format("Bearer %1\$s", bearer)
    }

    fun generateCustomerUUID(): String {
        return "HV" + System.currentTimeMillis() + (Math.random() * 100).roundToLong() + (Math.random() * 100).roundToLong()
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }



}