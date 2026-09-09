// SPDX-License-Identifier: BUSL-1.1; Copyright (c) 2025 Social Connect Labs, Inc.; Licensed under BUSL-1.1 (see LICENSE); Apache-2.0 from 2029-06-11

// Stubbed for fork build — private CameraFragment dependency removed.
// QR scanning is non-functional; mock flow does not require it.

package com.proofofpassportapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.Color
import androidx.fragment.app.Fragment

class QrCodeScannerFragment(private var callback: QRCodeScannerCallback?) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val frame = FrameLayout(requireContext()).apply {
            setBackgroundColor(Color.BLACK)
            val label = TextView(context).apply {
                text = "QR scanner stub — disabled in fork build"
                setTextColor(Color.WHITE)
            }
            addView(label)
        }
        return frame
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    fun forceStopCamera() {
        // no-op stub
    }

    interface QRCodeScannerCallback {
        fun onQRData(data: String)
        fun onError(e: Exception)
    }

    companion object {
        private val TAG = QrCodeScannerFragment::class.java.simpleName
    }
}
