@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.floater.widget.listview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.github.nukc.stateview.StateView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.xiaoyv.floater.widget.databinding.UiViewListBinderBinding
import com.xiaoyv.floater.widget.databinding.UiViewListNoMoreBinding
import com.xiaoyv.floater.widget.kts.fetchActivity
import com.xiaoyv.floater.widget.stateview.StateViewImpl
import java.lang.ref.WeakReference

/**
 * 仅仅包含 刷新、加载更多、和错误布局的列表控件
 *
 * @author why
 */
class BinderDataListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    var binding =
        UiViewListBinderBinding.inflate(LayoutInflater.from(context), this, false)
        private set

    private var reference: WeakReference<StateView>? = null
    private var stateViewImpl: StateViewImpl? = null

    var adapter = BaseBinderAdapter()
        private set

    var recyclerView = binding.rvData
        private set

    private val loadEndBinder = LoadEndBinder()
    private val loadEndBean = LoadEndBean(0)

    var layoutManager: RecyclerView.LayoutManager =
        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            binding.rvData.layoutManager = value
        }

    var canRefresh: Boolean = true
        set(value) {
            field = value
            binding.refreshLayout.setEnableRefresh(value)
        }

    var canLoadMore: Boolean = true
        set(value) {
            field = value
            binding.refreshLayout.setEnableLoadMore(value)
        }

    var canLoadMoreAuto: Boolean = false
        set(value) {
            field = value
            if (value) {
                canLoadMore = true
            }
            binding.refreshLayout.setEnableAutoLoadMore(value)
        }

    var header: RefreshHeader = ClassicsHeader(context)
        set(value) {
            field = value
            binding.refreshLayout.setRefreshHeader(value)
        }

    var footer: RefreshFooter = ClassicsFooter(context)
        set(value) {
            field = value
            binding.refreshLayout.setRefreshFooter(value)
        }

    var onRetryListener: (StateView, View) -> Unit = { _, _ -> }

    val isEmpty: Boolean
        get() {
            var count = 0
            adapter.data.forEach {
                if (it !is LoadEndBean) {
                    count++
                }
            }
            return count == 0
        }

    var bottomPadding: Int = 0
        set(value) {
            field = value
            binding.rvData.updatePadding(bottom = value)
        }

    var startPadding: Int = 0
        set(value) {
            field = value
            binding.rvData.updatePadding(left = value)
        }

    var topPadding: Int = 0
        set(value) {
            field = value
            binding.rvData.updatePadding(top = value)
        }

    var endPadding: Int = 0
        set(value) {
            field = value
            binding.rvData.updatePadding(right = value)
        }

    /**
     * 若有图片 id=R.id.iv_status ，设置在屏幕中的偏移系数，控制位置比例，默认 0.3
     */
    var tipImageVerticalBias: Float = 0.3f
        set(value) {
            field = value
            stateViewImpl?.imageVerticalBias = value
        }

    init {
        addView(binding.root)

        // 取消过度滚动的效果
        binding.rvData.overScrollMode = OVER_SCROLL_NEVER

        binding.rvData.layoutManager = layoutManager
        binding.rvData.adapter = adapter

        binding.refreshLayout.setRefreshHeader(header)
        binding.refreshLayout.setRefreshFooter(footer)
        binding.refreshLayout.setEnableRefresh(canRefresh)
        binding.refreshLayout.setEnableLoadMore(canLoadMore)
        binding.refreshLayout.setEnableAutoLoadMore(canLoadMoreAuto)

        // 默认空实现
        binding.refreshLayout.setOnRefreshListener { finishRefresh(300) }
        binding.refreshLayout.setOnLoadMoreListener { finishLoadMore(300) }

        addItemBinder(LoadEndBean::class.java, loadEndBinder)

        val activity = fetchActivity()
        if (activity is FragmentActivity) {
            // 状态布局
            stateViewImpl = object : StateViewImpl(activity) {
                override fun onCreateStateView(): StateView {
                    var stateView = reference?.get()
                    if (stateView != null) {
                        return stateView
                    }

                    stateView =
                        createStateView(activity, this@BinderDataListView, onRetryListener)
                    reference = WeakReference(stateView)
                    return stateView
                }
            }
        }

        // showLoadingView()
    }

    /**
     * 添加 ItemBinder
     */
    @JvmOverloads
    fun <T : Any> addItemBinder(
        clazz: Class<out T>,
        baseItemBinder: BaseItemBinder<T, *>,
        callback: DiffUtil.ItemCallback<T>? = null,
    ): BaseBinderAdapter {
        return adapter.addItemBinder(clazz, baseItemBinder, callback)
    }

    /**
     * kotlin 可以使用如下方法添加 ItemBinder，更加简单
     */
    inline fun <reified T : Any> addItemBinder(
        baseItemBinder: BaseItemBinder<T, *>,
        callback: DiffUtil.ItemCallback<T>? = null,
    ): BaseBinderAdapter {
        return adapter.addItemBinder(T::class.java, baseItemBinder, callback)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RecyclerView.LayoutManager> getManagerBy() = layoutManager as T


    fun clear() {
        adapter.setList(mutableListOf())
    }

    fun setList(list: Collection<Any>?) {
        adapter.setList(list)
    }

    fun addData(@IntRange(from = 0) position: Int, data: Any) {
        adapter.addData(position, data)
    }

    fun addData(@NonNull data: Any) {
        adapter.addData(data)
    }

    fun addData(@IntRange(from = 0) position: Int, newData: Collection<Any>) {
        adapter.addData(position, newData)
    }

    fun addData(@NonNull newData: Collection<Any>) {
        adapter.addData(newData)
    }

    fun addDataOnly(@NonNull data: Any) {
        adapter.data.add(data)
    }

    /**
     * 列表数据加载完了，没有更多了
     */
    fun loadMoreEnd() {
        val data = adapter.data

        var refreshStartIndex = -1
        if (data.contains(loadEndBean)) {
            refreshStartIndex = data.indexOf(loadEndBean)
            data.remove(loadEndBean)
        }
        if (refreshStartIndex == -1) {
            refreshStartIndex = data.size
        }

        data.add(data.size, loadEndBean)
        adapter.notifyItemRangeChanged(refreshStartIndex, adapter.itemCount)

        // 临时禁用加载更多，刷新就会解除
        binding.refreshLayout.setEnableLoadMore(false)
    }


    @JvmOverloads
    fun finishAll(success: Boolean = true) {
        binding.refreshLayout.finishRefresh(success)
        binding.refreshLayout.finishLoadMore(success)
    }

    @JvmOverloads
    fun finishRefresh(delayed: Int = 0, success: Boolean = true) {
        binding.refreshLayout.finishRefresh(delayed, success, false)
    }

    @JvmOverloads
    fun finishLoadMore(delayed: Int = 0, success: Boolean = true) {
        binding.refreshLayout.finishLoadMore(delayed, success, false)
    }

    fun showLoadingView() {
        stateViewImpl?.showLoadingView()
    }

    @JvmOverloads
    fun showEmptyOrTipView(
        emptyTip: String? = null,
        @DrawableRes imgResId: Int? = null,
    ) {
        stateViewImpl?.showTipView(emptyTip, imgResId)

        finishAll()

        // 临时禁用加载更多，刷新就会解除
        binding.refreshLayout.setEnableLoadMore(false)
    }

    @JvmOverloads
    fun showErrorView(
        msg: String? = null,
        btText: String? = null,
        @DrawableRes imgResId: Int? = null,
    ) {
        stateViewImpl?.showRetryView(msg, btText, imgResId)

        finishAll()

        // 临时禁用加载更多，刷新就会解除
        binding.refreshLayout.setEnableLoadMore(false)
    }

    fun showContentView() {
        stateViewImpl?.showNormalView()
        finishAll()
    }

    inline fun setDateListener(
        crossinline onRefreshListener: (RefreshLayout) -> Unit = {},
        crossinline onLoadMoreListener: (RefreshLayout) -> Unit = {},
    ) {
        binding.refreshLayout.setOnRefreshListener {
            // 恢复配置的是否可以加载更多
            binding.refreshLayout.setEnableLoadMore(canLoadMore)
            onRefreshListener.invoke(it)
        }
        binding.refreshLayout.setOnLoadMoreListener {
            onLoadMoreListener.invoke(it)
        }
    }

    fun remove(position: Int) {
        if (position < 0 || position >= adapter.itemCount) {
            return
        }
        adapter.removeAt(position)
    }

    /**
     * 获取第一条数据【非加载底线的模型】
     */
    fun firstData(): Any? {
        val data = adapter.data
        return data.firstOrNull {
            it !is LoadEndBean
        }
    }

    /**
     * 获取最后一条数据【非加载底线的模型】
     */
    fun lastData(): Any? {
        val data = adapter.data
        return data.lastOrNull {
            it !is LoadEndBean
        }
    }

    data class LoadEndBean(val id: Long)

    inner class LoadEndBinder : BaseItemBinder<LoadEndBean, BaseViewHolder>() {
        override fun convert(holder: BaseViewHolder, data: LoadEndBean) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val noMoreBinding =
                UiViewListNoMoreBinding.inflate(LayoutInflater.from(context), parent, false)
            return BaseViewHolder(noMoreBinding.root)
        }
    }

}
