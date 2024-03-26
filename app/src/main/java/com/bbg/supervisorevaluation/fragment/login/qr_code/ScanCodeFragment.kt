package com.bbg.supervisorevaluation.fragment.login.qr_code

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bbg.supervisorevaluation.R
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback

class ScanCodeFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan_code, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setImmersiveMode()
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        initializeCodeScanner(scannerView)
        setupScannerClickListener(scannerView)
        requestCameraPermission()
    }
    private fun setImmersiveMode() {
        activity?.window?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
    private fun initializeCodeScanner(scannerView: CodeScannerView) {
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val scannedData = it.text
                val bundle = Bundle().apply {
                    putString("scannedData", scannedData)
                }
                navigateToLoginFragment(bundle)
            }
        }
    }
    private fun setupScannerClickListener(scannerView: CodeScannerView) {
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initBarcodeScanner()
        } else {
            requestCameraPermissionFromUser()
        }
    }
    private fun initBarcodeScanner() {

    }
    private fun requestCameraPermissionFromUser() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }
    private fun navigateToLoginFragment(bundle: Bundle) {
        findNavController().navigate(R.id.action_scanCodeFragment_to_loginFragment, bundle)
    }
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initBarcodeScanner()
            } else {
                Toast.makeText(activity, getString(R.string.kameraberechtigung_verweigert), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
