package com.hfad.skindoc.ml

import android.os.Handler
import android.os.Looper
import com.hfad.skindoc.home.Home
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
    private val callback: Home
) : RequestBody() {

    override fun contentType(): MediaType? {
        return "$contentType/*".toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        val handler = Handler(Looper.getMainLooper())

        fileInputStream.use { inputStream ->
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                uploaded += read
                sink.write(buffer, 0, read)

                // Post progress update to main thread
                handler.post(ProgressUpdater(uploaded, length))
            }
        }
    }

    interface UploadCallback {
        fun onProgressUpdater(percentage: Int)
    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
//            callback.onProgressUpdater((100 * uploaded / total).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}
