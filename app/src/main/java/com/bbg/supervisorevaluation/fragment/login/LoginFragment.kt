package com.bbg.supervisorevaluation.fragment.login

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bbg.supervisorevaluation.R
import com.bbg.supervisorevaluation.network.ApiService
import com.bbg.supervisorevaluation.network.RetrofitClient
import com.bbg.supervisorevaluation.network.utils.StorageUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

import retrofit2.*

class LoginFragment : Fragment() {

    private lateinit var button: MaterialButton
    private lateinit var mandantenIdTextInputLayout: TextInputLayout
    private lateinit var qrCodeScannen: AppCompatImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        applySystemBarConfiguration()
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        initializeViews(view)
        setupListeners()
        return view
    }
    private fun applySystemBarConfiguration() {
        activity?.window?.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }
    private fun initializeViews(view: View) {
        button = view.findViewById(R.id.buttonLoginNavigate)
        qrCodeScannen = view.findViewById(R.id.qrCodeScannen)
        mandantenIdTextInputLayout = view.findViewById(R.id.mandantenIdTextInputLayout)
        arguments?.let {
            val scannedData = it.getString("scannedData")
            mandantenIdTextInputLayout.editText?.setText(scannedData)
        }
    }
    private fun setupListeners() {
        qrCodeScannen.setOnClickListener {
            view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_loginFragment_to_scanCodeFragment) }
        }
        button.setOnClickListener {
            val id = mandantenIdTextInputLayout.editText?.text.toString()
            if (id.isNotBlank()){
                performApiCall(id)
            } else {
                Toast.makeText(requireContext(), "Bitte geben Sie Ihre gültige ID ein", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun performApiCall(guidLinkId: String) {
        val apiService: ApiService = RetrofitClient.getRetrofitInstance().create(ApiService::class.java)
        lifecycleScope.launch {
            try {
                val response = apiService.getNumberForGuidLink(guidLinkId)
                if (response.isSuccessful) {
                    val body = response.body() //Unique ID can be retrieved from this Link https://api-supervisor-appraisal.azurewebsites.net/swagger/index.html
                    body?.let { StorageUtil.saveIdAndSurveyId(requireContext(),guidLinkId, it.toInt()) }
                    view?.let { Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_profileFragment) }
                }else {
                    Log.d(TAG, getString(R.string.id_existiert_nicht))
                    Toast.makeText(requireContext(), getString(R.string.id_existiert_nicht), Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Log.e(TAG, "Exception: ${e.message}")
            }
        }
    }
}