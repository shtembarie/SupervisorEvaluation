package com.bbg.supervisorevaluation.fragment.splashScreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.navigation.Navigation
import com.bbg.supervisorevaluation.R
import com.google.android.material.button.MaterialButton

@SuppressLint("CustomSplashScreen")
class SplashScreen : Fragment() {

    private lateinit var button: MaterialButton
    private lateinit var imageView3: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        val view: View = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        button = view.findViewById(R.id.buttonNavigate)
        button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_splashScreen_to_loginFragment)
        }
            imageView3 = view.findViewById(R.id.imageView3)
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_and_rotate_animation)
            imageView3.startAnimation(animation)
            return view
        }


}

