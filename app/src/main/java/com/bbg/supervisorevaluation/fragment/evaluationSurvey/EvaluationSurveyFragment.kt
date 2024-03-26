package com.bbg.supervisorevaluation.fragment.evaluationSurvey

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bbg.supervisorevaluation.R
import com.bbg.supervisorevaluation.data.ResultModel
import com.bbg.supervisorevaluation.data.SupervisorModel
import com.bbg.supervisorevaluation.data.SurveyResultModel
import com.bbg.supervisorevaluation.network.ApiService
import com.bbg.supervisorevaluation.network.RetrofitClient
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EvaluationSurveyFragment : Fragment() {

    private val retrofit = RetrofitClient.getRetrofitInstance()
    private val service = retrofit.create(ApiService::class.java)
    private val TAG = "EvaluationSurveyFragment"
    private lateinit var containerLayout: LinearLayout
    private lateinit var topicTitleTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButtonYes: RadioButton
    private lateinit var radioButtonNo: RadioButton
    private lateinit var reasonEditText: EditText
    private lateinit var proceedButton: Button
    private lateinit var backButton: Button
    private var currentTopicIndex = 0
    private var currentQuestionIndex = 0
    private var currentTopicQuestionIndex = 0
    private var results = ArrayList<ResultModel>()
    private var topics: JSONArray? = null
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var startTextView: TextView
    private lateinit var endTextView: TextView
    private var totalQuestions = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_evaluation_survey, container, false)
        initializeViews(view)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchSurveyData()
        setupListeners()
    }
    private fun initializeViews(view: View) {
        containerLayout = view.findViewById(R.id.containerLayout)
        topicTitleTextView = view.findViewById(R.id.topicTitleTextView)
        questionTextView = view.findViewById(R.id.questionTextView)
        radioGroup = view.findViewById(R.id.radioGroup)
        radioButtonYes = view.findViewById(R.id.radioButtonYes)
        radioButtonNo = view.findViewById(R.id.radioButtonNo)
        reasonEditText = view.findViewById(R.id.reasonEditText)
        proceedButton = view.findViewById(R.id.proceedButton)
        backButton = view.findViewById(R.id.backButton)
        progressBar = view.findViewById(R.id.progressBar)
        startTextView = view.findViewById(R.id.start)
        endTextView = view.findViewById(R.id.end)
    }
    private fun fetchSurveyData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val surveyId = sharedPreferences.getInt("surveyId", -1) // -1 is default value if key not found

        if (surveyId != -1) {
            val call = service.getSurvey(surveyId)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val gsonArray = body?.getAsJsonObject("surveyTemplate")?.getAsJsonArray("topics")
                        val jsonArray = JSONArray()
                        gsonArray?.let {
                            for (i in 0 until it.size()) {
                                jsonArray.put(JSONObject(it.get(i).toString()))
                            }
                        }
                        try {
                            topics = jsonArray
                            fillResult()
                            displayTopic(topics?.getJSONObject(currentTopicIndex))
                        } catch (e: JSONException) {
                            Log.e(TAG, getString(R.string.json_parse_error) + ": ${e.message}")

                        }
                    } else {
                        Log.e(TAG, getString(R.string.error_fetching_survey_data) + ": ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e(TAG, getString(R.string.error_fetching_survey_data) + ": ${t.message}")

                }
            })
        } else {
            // Behandelt den Fall, dass die SurveyID nicht in SharedPreferences gefunden wird
        }
    }
    private fun displayTopic(topic: JSONObject?) {
        topic?.let {
            val topicTitle = it.getString("titel")
            val questionsArray = it.getJSONArray("questions")
            startTextView.text = "${currentQuestionIndex + 1}"
            topicTitleTextView.text = topicTitle
            displayQuestion(questionsArray.getJSONObject( currentTopicQuestionIndex))
        }
        backButton.isEnabled = currentQuestionIndex > 0 || currentTopicIndex > 0
        updateProgressBar()
    }
    private fun fillResult(){
        if (topics != null){
            for (i in 0 until topics!!.length()){
                val topic = topics!!.getJSONObject(i)
                val questionsArray = topic.optJSONArray("questions")

                if (questionsArray != null){
                    for (j in 0 until questionsArray!!.length()){
                        totalQuestions++
                        val question = questionsArray!!.getJSONObject(j)
                        results.add(ResultModel(
                            resultId = 0,
                            questionId = question.getInt("id"),
                            answer = -1,
                            explanation = ""
                        ))
                    }
                }
                endTextView.text = "$totalQuestions"

            }
        }
    }
    private fun sendSurveyResult() {
        val selectedSupervisor: Parcelable? = requireArguments().getParcelable("selectedSupervisor")
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val surveyId = sharedPreferences.getInt("surveyId", -1)
        val linkId =  sharedPreferences.getString("id", "")
        if (surveyId != -1) {
            val surveyResultModel = SurveyResultModel(
                surveyResultId = 0,
                results = results,
                supervisor = selectedSupervisor as SupervisorModel?,
                surveyId = surveyId
            )
            val postCall = linkId?.let { service.postSurveyResult(surveyResultModel, it) }
            if (postCall != null) {
                postCall.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            Log.d(TAG, getString(R.string.survey_result_posted_successfully) + ": $results")

                        } else {
                            Log.e(TAG, getString(R.string.failed_to_post_survey_result) + ": ${response.code()}")

                        }
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.e(TAG, getString(R.string.error_posting_survey_result) + ": ${t.message}")
                    }
                })
            }
        } else {
        }
    }
    private fun displayQuestion(question: JSONObject) {
        val questionText = question.getString("text")
        questionTextView.text = questionText
        radioGroup.clearCheck()
        reasonEditText.setText("")
        reasonEditText.visibility = View.GONE
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioButtonNo) {
                reasonEditText.visibility = View.VISIBLE
            } else {
                reasonEditText.visibility = View.GONE
            }
        }
        results[currentQuestionIndex].let { result ->
            var radioButtonId = -1
            if (result.answer == 1){
                radioButtonId = R.id.radioButtonYes
            }
            if (result.answer == 0){
                radioButtonId = R.id.radioButtonNo
                reasonEditText.setText(result.explanation)
            }
            radioGroup.check(radioButtonId)
        }
        proceedButton.setOnClickListener {
            updateProgressBar()
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                if (selectedRadioButtonId == R.id.radioButtonNo && reasonEditText.text.isBlank()) {
                    return@setOnClickListener
                }
                results[currentQuestionIndex].answer = -1
                if (selectedRadioButtonId == R.id.radioButtonYes){
                    results[currentQuestionIndex].answer = 1
                }
                if (selectedRadioButtonId == R.id.radioButtonNo){
                    results[currentQuestionIndex].answer = 0
                    results[currentQuestionIndex].explanation = reasonEditText.text.toString()
                }

                currentQuestionIndex++
                currentTopicQuestionIndex++
                if (currentQuestionIndex == totalQuestions-1) {
                    proceedButton.text = getString(R.string.end_button_text)
                }
                if (currentQuestionIndex >= totalQuestions) {
                    sendSurveyResult()
                    Log.d(TAG, getString(R.string.survey_completed))
                    Navigation.findNavController(requireView()).navigate(R.id.action_evaluation_survey_fragment_to_vielenDankFragment)
                    return@setOnClickListener
                }
                if (currentTopicQuestionIndex >= topics?.getJSONObject(currentTopicIndex)?.getJSONArray("questions")?.length() ?: 0) {
                    currentTopicQuestionIndex=0
                    currentTopicIndex++
                }
                if (currentTopicIndex < topics?.length() ?: 0) {
                    displayTopic(topics?.getJSONObject(currentTopicIndex))
                } else {
                    Log.d(TAG, getString(R.string.survey_completed))
                }
            } else {
            }
        }
    }
    private fun setupListeners() {
        backButton.setOnClickListener {
            if (currentTopicQuestionIndex > 0) {
                currentQuestionIndex--
                currentTopicQuestionIndex--
                displayTopic(topics?.getJSONObject(currentTopicIndex))
            } else if (currentTopicIndex > 0) {
                currentTopicIndex--
                currentQuestionIndex--
                val questionsArray = topics?.getJSONObject(currentTopicIndex)?.getJSONArray("questions")
                currentTopicQuestionIndex = questionsArray?.length()?.minus(1) ?: 0
                displayTopic(topics?.getJSONObject(currentTopicIndex))
            } else {
                Log.d(TAG, getString(R.string.no_previous_topic_or_question))
            }
        }
    }
    private fun updateProgressBar(){
        val progress = (currentQuestionIndex + 0).toFloat() * 100 / (totalQuestions -1)
        progressBar.setProgressCompat(progress.toInt(), true)
    }
}
