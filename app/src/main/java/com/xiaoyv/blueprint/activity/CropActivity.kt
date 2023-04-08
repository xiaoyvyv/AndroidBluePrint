package com.xiaoyv.blueprint.activity

import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.app.databinding.ActivityCropBinding
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.crop.CropVideoView
import com.xiaoyv.widget.crop.extractFrames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * CropActivity
 *
 * @author why
 * @since 2023/1/12
 **/
class CropActivity : BaseActivity() {
    private val binding by lazy { ActivityCropBinding.inflate(layoutInflater) }

    override fun createContentView(): View {
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {
//        binding.btnTest.setOnFastLimitClickListener {
//            val filePath = PathUtils.getExternalAppFilesPath() + "/test.mp4"
//            lifecycleScope.launch {
//                val time = System.currentTimeMillis()
//                val extract = arrayListOf<Long>().apply {
//                    repeat(12) {
//                        add(it * 1000L)
//                    }
//                }
//                val extractFrames = withContext(Dispatchers.IO) {
//                    extractFrames(filePath, extract)
//                }
//                val spend = System.currentTimeMillis() - time
//                ToastUtils.showShort("抽取帧：$spend ms")
//                binding.cropView.imageList = extractFrames
//
//                extractFrames.onEachIndexed { index, bitmap ->
////                    val imagePath = PathUtils.getExternalAppFilesPath() + "/video_frame_$index.png"
////                    ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.PNG)
//                }
//            }
//        }
//
//
//        lifecycleScope.launch {
//            callbackFlow {
//                // 背压处理（处理 onCropListener 的回调太快了）最快 100ms 回调一次
//                binding.cropView.onCropListener = object : CropVideoView.OnCropListener {
//                    override fun onCropChange(
//                        startPosition: Long,
//                        endPosition: Long,
//                        progressPosition: Long,
//                        totalDuration: Long
//                    ) {
//                        trySend(
//                            longArrayOf(
//                                startPosition,
//                                endPosition,
//                                progressPosition,
//                                totalDuration
//                            )
//                        )
//                    }
//                }
//                awaitClose()
//            }.buffer(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
//                .collect {
//                    withContext(Dispatchers.IO) {
//                        delay(100)
//                    }
//                    Log.e("CropVideoView", GsonUtils.toJson(it))
//                }
//        }
    }
}