package com.xiaoyv.blueprint.activity

import android.view.View
import com.xiaoyv.blueprint.app.databinding.ActivityCropBinding
import com.xiaoyv.blueprint.base.BaseActivity

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