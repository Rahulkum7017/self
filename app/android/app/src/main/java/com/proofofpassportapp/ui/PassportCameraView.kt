// SPDX-License-Identifier: BUSL-1.1; Copyright (c) 2025 Social Connect Labs, Inc.; Licensed under BUSL-1.1 (see LICENSE); Apache-2.0 from 2029-06-11

// Stubbed for fork build — private passport-reader dependencies removed.
// Camera/OCR/NFC scanning is non-functional; mock flow does not require it.

package com.proofofpassportapp.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event

class PassportCameraView(context: Context) : FrameLayout(context) {
  init {
    setBackgroundColor(Color.BLACK)
    val label = TextView(context).apply {
      text = "Camera stub — scanning disabled in fork build"
      setTextColor(Color.WHITE)
    }
    addView(label)
  }

  fun setMounted(value: Boolean) {
    // no-op stub
  }

  fun stopCamera() {
    // no-op stub
  }
}

internal class PassportReadEvent(
    surfaceId: Int,
    viewId: Int,
    private val data: String,
) : Event<PassportReadEvent>(surfaceId, viewId) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData() =
      Arguments.createMap().apply {
        putString("data", data)
      }

  private companion object {
    const val EVENT_NAME = "topPassportRead"
  }
}

internal class PassportErrorEvent(
    surfaceId: Int,
    viewId: Int,
    private val message: String,
    private val error: String,
    private val stackTrace: String,
) : Event<PassportErrorEvent>(surfaceId, viewId) {
  override fun getEventName(): String = EVENT_NAME

  override fun getEventData() =
      Arguments.createMap().apply {
        putString("errorMessage", message)
        putString("error", error)
        putString("stackTrace", stackTrace)
      }

  private companion object {
    const val EVENT_NAME = "topError"
  }
}
