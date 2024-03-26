package com.bbg.supervisorevaluation.fragment.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bbg.supervisorevaluation.R
import com.bbg.supervisorevaluation.data.Department
import com.bbg.supervisorevaluation.data.SupervisorModel
import com.bbg.supervisorevaluation.network.ApiService
import com.bbg.supervisorevaluation.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nav_view: NavigationView
    private lateinit var titleTextView : TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var tv_supervisorname: TextView
    private lateinit var tv_supervisordepartment: TextView
    private var supervisors = mutableListOf<SupervisorModel>()
    private lateinit var rightArrowImageView: ImageView
    private lateinit var navController: NavController
    private lateinit var selectedSupervisor : SupervisorModel
    private lateinit var tvGrey: TextView
    private lateinit var tvAVG: TextView
    private lateinit var tvAVG1: TextView
    private val retrofit = RetrofitClient.getRetrofitInstance()
    private val service = retrofit.create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupDrawerAndToolbar()
        setupNavigationDrawer()
        setupNavigationOnClickListener()
        setupRightArrowClickListener()
        fetchData()
        fetchDataSupervisor()
    }
    private fun initializeViews(view: View) {
        drawerLayout = view.findViewById(R.id.drawer_layout)
        toolbar = view.findViewById(R.id.toolbar)
        nav_view = view.findViewById(R.id.nav_view)
        toolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_menu_black)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        tv_supervisorname = view.findViewById(R.id.tv_supervisorname)
        tv_supervisordepartment = view.findViewById(R.id.tv_supervisordepartment)
        rightArrowImageView = view.findViewById(R.id.rightArrowImageView)
        tvGrey = view.findViewById(R.id.tv_grey)
        tvAVG = view.findViewById(R.id.tv_avg)
        tvAVG1 = view.findViewById(R.id.tv_avg1)
    }
    private fun setupDrawerAndToolbar() {
        val toggle = ActionBarDrawerToggle(
            requireActivity(), drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    @SuppressLint("ResourceAsColor")
    private fun setupNavigationDrawer() {
        navController = view?.let { Navigation.findNavController(it) }!!
        nav_view.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.groupId == R.id.supervisor_group) {
                val supervisorId = menuItem.itemId
                selectedSupervisor = supervisors.find { it.id == supervisorId }!!
                selectedSupervisor?.let { displaySupervisorData(it) }
                tvGrey.setBackgroundColor(R.color.green)
                tvAVG.visibility = View.GONE
                tvAVG1.visibility = View.VISIBLE
                drawerLayout.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }
            return@setNavigationItemSelectedListener false
        }
    }
    private fun setupNavigationOnClickListener() {
        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
    private fun setupRightArrowClickListener() {
        rightArrowImageView.setOnClickListener {
            try {
                selectedSupervisor?.let {
                    val bundle = bundleOf("selectedSupervisor" to it)
                    navController.navigate(R.id.action_profileFragment_to_evaluation_survey_fragment, bundle)
                }
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), getString(R.string.vorgesetzter_wählen), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun fetchData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val surveyId = sharedPreferences.getInt("surveyId", -1) // -1 is default value if key not found
        if (surveyId != -1) {
            val call = service.getSurvey(surveyId)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        titleTextView.text = body?.get("title")?.asString ?: ""
                        descriptionTextView.text = body?.get("description")?.asString ?: ""
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
    private fun fetchDataSupervisor() {
        val call = service.getSupervisors()
        call.enqueue(object : Callback<List<SupervisorModel>> {
            override fun onResponse(
                call: Call<List<SupervisorModel>>,
                response: Response<List<SupervisorModel>>
            ) {
                if (response.isSuccessful) {
                    supervisors.clear()
                    response.body()?.let { supervisors.addAll(it) }
                    updateSidebarMenu(supervisors)
                }
            }

            override fun onFailure(call: Call<List<SupervisorModel>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun updateSidebarMenu(supervisors: List<SupervisorModel>) {
        val menu = nav_view.menu
        menu.removeGroup(R.id.supervisor_group)

        val supervisorGroup = menu.addSubMenu(getString(R.string.vorgesetzter_wählen1))
        supervisors.forEach { supervisor ->
            supervisorGroup.add(R.id.supervisor_group, supervisor.id, Menu.NONE, supervisor.name)
        }
    }
    private fun displaySupervisorData(supervisor: SupervisorModel) {
        tv_supervisorname.text = supervisor.name
        tv_supervisordepartment.text = supervisor.department.dName
    }

}
