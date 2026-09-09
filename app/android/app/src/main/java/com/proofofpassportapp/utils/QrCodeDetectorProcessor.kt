// SPDX-License-Identifier: BUSL-1.1; Copyright (c) 2025 Social Connect Labs, Inc.; Licensed under BUSL-1.1 (see LICENSE); Apache-2.0 from 2029-06-11

// Stubbed for fork build — private FrameMetadata/ImageUtil removed.
// Camera-based QR scanning is disabled; bitmap-based QR decoding (used by
// PdfQrHelper for e-Aadhaar) is preserved.

package com.proofofpassportapp.utils

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class QrCodeDetectorProcessor {

    private val shouldThrottle = AtomicBoolean(false)
    var executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun stop() {}

    /**
     * Synchronously decodes a QR code from a still bitmap (e.g. a rasterized PDF
     * page). Returns the decoded text, or null if none is found.
     */
    fun decodeBitmapSync(image: Bitmap): String? {
        val hints = mapOf(
            com.google.zxing.DecodeHintType.PURE_BARCODE to false
        )
        return detectInImage(image, hints)?.text
    }

    /**
     * Decodes a QR from an already high-resolution bitmap WITHOUT the internal
     * 2x/0.5x rescaling.
     */
    fun decodeBitmapDirect(image: Bitmap): String? {
        val hints = mapOf(
            com.google.zxing.DecodeHintType.PURE_BARCODE to false
        )
        return tryDetectInBitmap(image, QRCodeReader(), hints)?.text
    }

    fun detectQrCodeInBitmap(
        image: Bitmap,
        listener: Listener
    ): Boolean {
        val start = System.currentTimeMillis()
        executor.execute {
            val hints = mapOf(
                com.google.zxing.DecodeHintType.PURE_BARCODE to false
            )
            val result = detectInImage(image, hints)
            val timeRequired = System.currentTimeMillis() - start
            if (result != null) {
                listener.onSuccess(result.text!!, null, timeRequired, null)
            } else {
                listener.onCompletedFrame(timeRequired)
            }
        }
        return true
    }

    private fun detectInImage(bitmap: Bitmap, additionalHints: Map<com.google.zxing.DecodeHintType, Any>? = null): Result? {
        val qRCodeDetectorReader = QRCodeReader()

        var result = tryDetectInBitmap(bitmap, qRCodeDetectorReader, additionalHints)
        if (result != null) return result

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width * 2, bitmap.height * 2, true)
        result = tryDetectInBitmap(scaledBitmap, qRCodeDetectorReader, additionalHints)
        if (result != null) return result

        val scaledDownBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
        result = tryDetectInBitmap(scaledDownBitmap, qRCodeDetectorReader, additionalHints)
        if (result != null) return result

        return null
    }

    private fun tryDetectInBitmap(bitmap: Bitmap, qRCodeDetectorReader: QRCodeReader, additionalHints: Map<com.google.zxing.DecodeHintType, Any>? = null): Result? {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val source: LuminanceSource =
            RGBLuminanceSource(bitmap.width, bitmap.height, intArray)

        val binarizers = listOf(
            HybridBinarizer(source),
            com.google.zxing.common.GlobalHistogramBinarizer(source)
        )

        for (binarizer in binarizers) {
            val binaryBitMap = BinaryBitmap(binarizer)
            try {
                return qRCodeDetectorReader.decode(binaryBitMap)
            } catch (e: Exception) {
                // try next
            }
        }

        val hints = buildMap {
            put(com.google.zxing.DecodeHintType.TRY_HARDER, true)
            put(com.google.zxing.DecodeHintType.POSSIBLE_FORMATS, listOf(com.google.zxing.BarcodeFormat.QR_CODE))
            additionalHints?.forEach { (key, value) -> put(key, value) }
        }

        for (binarizer in binarizers) {
            val binaryBitMap = BinaryBitmap(binarizer)
            try {
                return qRCodeDetectorReader.decode(binaryBitMap, hints)
            } catch (e: Exception) {
                // try next
            }
        }

        return null
    }

    /** Listener with no FrameMetadata dependency (stubbed). */
    interface Listener {
        fun onSuccess(results: String, frameMetadata: Any?, timeRequired: Long, bitmap: Bitmap?)
        fun onFailure(e: Exception, timeRequired: Long)
        fun onCompletedFrame(timeRequired: Long)
    }

    companion object {
        private val TAG = QrCodeDetectorProcessor::class.java.simpleName
    }
}
