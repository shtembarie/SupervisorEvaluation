package com.bbg.supervisorevaluation.data

import org.json.JSONArray
import org.json.JSONObject

data class SurveyResultModel(
    val results: ArrayList<ResultModel>,
    val supervisor: SupervisorModel?,
    val surveyId: Int,
    val surveyResultId: Int
) {
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("surveyId", surveyId)
        jsonObject.put("surveyResultId", surveyResultId)
        jsonObject.put("supervisor", supervisor?.toJSON())

        val resultsArray = JSONArray()
        results.forEach { result ->
            val resultObject = JSONObject()
            resultObject.put("resultId", result.resultId)
            resultObject.put("questionId", result.questionId)
            resultObject.put("answer", result.answer)
            resultObject.put("explanation", result.explanation)
            resultsArray.put(resultObject)
        }
        jsonObject.put("results", resultsArray)

        return jsonObject
    }
}

