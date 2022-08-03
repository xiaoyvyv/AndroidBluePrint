@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.core.view.OneShotPreDrawListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel

/**
 * BaseNavFragment
 *
 * @author why
 * @since 2022/7/9
 **/
abstract class BaseNavFragment<VB : ViewBinding, VM : BaseNavViewModel> : Fragment() {
    private var _binding: VB? = null

    /**
     * 此属性仅在 onCreateView 和 onDestroyView 之间有效
     */
    val binding get() = _binding ?: throw NullPointerException("_binding 已经释放，请检查泄露代码！！！")

    val viewModel: VM by createViewModel()

    /**
     * NavController
     */
    open val navController get() = findNavController()

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding init
        _binding = createBinding(inflater, container)

        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onAttach(requireContext())
        arguments?.initArguments()

        view.post {
            // Pause the transition animation
            postponeEnterTransition()
            initView()
            initListener()
            viewLifecycleOwner.initObserver()
            viewModel.onViewCreated()

            // Finally, use this method to listen to the view structure , And start performing the cut-off animation
            (view.parent as? ViewGroup)?.apply {
                OneShotPreDrawListener.add(this) {
                    startPostponedEnterTransition()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (onBackPressed()) {
                        return
                    }
                    if (navController.popBackStack().not()) {
                        requireActivity().finish()
                    }
                }
            })
    }

    protected open fun Bundle.initArguments() {}

    abstract fun initView()

    abstract fun initListener()

    abstract fun LifecycleOwner.initObserver()

    /**
     * 子方法重写该方法释放资源时，需要在回调 `super.onDestroyView()` 方法前操作
     */
    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onViewDestroy()

        _binding = null
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.onDestroy()
    }

    /**
     * 返回键监听
     */
    protected open fun onBackPressed(): Boolean {
        return false
    }
}


