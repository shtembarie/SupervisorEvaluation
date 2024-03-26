package com.bbg.supervisorevaluation.network.utils

import android.content.Context

object StorageUtil {
    fun saveIdAndSurveyId(context: Context, id: String, surveyId: Int) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("id", id)
            putInt("surveyId", surveyId)
            apply()
        }
    }
}
