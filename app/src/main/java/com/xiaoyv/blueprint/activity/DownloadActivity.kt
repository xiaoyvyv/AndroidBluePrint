package com.xiaoyv.blueprint.activity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.app.databinding.ActivityDownloadBinding
import com.xiaoyv.webview.helper.TBS_DEFEAT_CORE_URL
import com.xiaoyv.webview.helper.X5InstallHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File


class DownloadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadBinding

    private val TAG = "MainActivity"
    private var downloadId: Long = -1L
    private lateinit var downloadManager: DownloadManager

    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.action)) {
                if (downloadId == id) {
                    val query: DownloadManager.Query = DownloadManager.Query()
                    query.setFilterById(id)
                    var cursor = downloadManager.query(query)
                    if (!cursor.moveToFirst()) {
                        return
                    }

                    var columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    var status = cursor.getInt(columnIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Toast.makeText(context, "Download succeeded", Toast.LENGTH_SHORT).show()
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.action)) {
                Toast.makeText(context, "Notification clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        registerReceiver(onDownloadComplete, intentFilter)

        binding.downloadBtn.setOnClickListener {
//            downloadImage()

            lifecycleScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                ToastUtils.showLong("error: $throwable")
            }) {
                val download = X5InstallHelper.download { fl, speed, total ->
                    Log.e(
                        TAG,
                        "onCreate: progress => $${
                            String.format(
                                "%.2f",
                                fl
                            )
                        },speed => ${speed / 1024f}, total => $total"
                    )
                }
                LogUtils.e("下载路径：$download")
            }
        }

        binding.statusBtn.setOnClickListener {
            val status = getStatus(downloadId)
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        binding.cancelBtn.setOnClickListener {
            if (downloadId != -1L) {
                downloadManager.remove(downloadId)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)
    }

    private fun downloadImage() {
        val file = File(getExternalFilesDir(null), "dev_submit.mp4")
        val youtubeUrl = TBS_DEFEAT_CORE_URL

        val request = DownloadManager.Request(Uri.parse(youtubeUrl))
            .setTitle("Downloading a video")
            .setDescription("Downloading Dev Summit")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadId = downloadManager.enqueue(request)
        Log.d(TAG, "path : " + file.path)
    }

    private fun getStatus(id: Long): String {
        val query: DownloadManager.Query = DownloadManager.Query()
        query.setFilterById(id)
        var cursor = downloadManager.query(query)
        if (!cursor.moveToFirst()) {
            Log.e(TAG, "Empty row")
            return "Wrong downloadId"
        }

        var columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        var status = cursor.getInt(columnIndex)
        var columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        var reason = cursor.getInt(columnReason)
        var statusText: String

        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> statusText = "Successful"
            DownloadManager.STATUS_FAILED -> {
                statusText = "Failed: $reason"
            }
            DownloadManager.STATUS_PENDING -> statusText = "Pending"
            DownloadManager.STATUS_RUNNING -> statusText = "Running"
            DownloadManager.STATUS_PAUSED -> {
                statusText = "Paused: $reason"
            }
            else -> statusText = "Unknown"
        }

        return statusText
    }
}