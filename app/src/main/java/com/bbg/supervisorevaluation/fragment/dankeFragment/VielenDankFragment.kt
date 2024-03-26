package com.bbg.supervisorevaluation.fragment.dankeFragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bbg.supervisorevaluation.R


class VielenDankFragment : Fragment() {

    private lateinit var arrowBack: AppCompatImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_vielen_dank,container,false)
        arrowBack = view.findViewById(R.id.arrowBack)!!
        arrowBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_vielenDankFragment_to_loginFragment)
        }
        return view

    }

}