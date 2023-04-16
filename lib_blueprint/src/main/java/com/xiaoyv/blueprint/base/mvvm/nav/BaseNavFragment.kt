@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.nav

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.base.bindListener
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel

/**
 * BaseNavFragment
 *
 * @author why
 * @since 2022/7/9
 **/
abstract class BaseNavFragment<VB : ViewBinding, VM : BaseNavViewModel> : Fragment(),
    OnAnimationListener {
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

        initView()
        initListener()
        viewLifecycleOwner.initObserver()
        viewModel.onViewCreated()

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

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return AnimationOrAnimator.onCreateAnimation(this, transit, enter, nextAnim)
            .animation
            .bindListener(object : OnAnimationListener {
                override fun onAnimationStart() {
                    this@BaseNavFragment.onAnimationStart()
                    viewModel.onAnimationStart()
                }

                override fun onAnimationEnd() {
                    this@BaseNavFragment.onAnimationEnd()
                    viewModel.onAnimationEnd()
                }
            }, enter)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return AnimationOrAnimator.onCreateAnimation(this, transit, enter, nextAnim)
            .animator
            .bindListener(object : OnAnimationListener {
                override fun onAnimationStart() {
                    this@BaseNavFragment.onAnimationStart()
                    viewModel.onAnimationStart()
                }

                override fun onAnimationEnd() {
                    this@BaseNavFragment.onAnimationEnd()
                    viewModel.onAnimationEnd()
                }
            }, enter)
    }

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


    /**
     * Contains either an animator or animation. One of these should be null.
     */
    internal class AnimationOrAnimator {
        val animation: Animation?
        val animator: Animator?

        constructor() {
            this.animation = null
            this.animator = null
        }

        constructor(animation: Animation) {
            this.animation = animation
            this.animator = null
        }

        constructor(animator: Animator) {
            this.animation = null
            this.animator = animator
        }

        companion object {
            fun onCreateAnimation(
                fragment: Fragment,
                transit: Int,
                enter: Boolean,
                nextAnimId: Int
            ): AnimationOrAnimator {
                val context = fragment.requireContext()
                var nextAnim = nextAnimId
                if (nextAnim == 0 && transit != 0) {
                    runCatching {
                        Class.forName("androidx.fragment.app.FragmentAnim").also { cls ->
                            val transitToAnimResourceId = cls.getMethod(
                                "transitToAnimResourceId",
                                Context::class.java,
                                Int::class.java,
                                Boolean::class.java
                            )
                            nextAnim = transitToAnimResourceId.invoke(
                                null, context, transit, enter
                            ) as Int
                            ToastUtils.showLong("anim：$nextAnim")
                        }
                    }
                }

                if (nextAnim != 0) {
                    val dir: String = context.resources.getResourceTypeName(nextAnim)
                    val isAnim = "anim" == dir

                    var successfulLoad = false
                    if (isAnim) {
                        runCatching {
                            val animation = AnimationUtils.loadAnimation(context, nextAnim)
                            if (animation != null) {
                                return AnimationOrAnimator(animation)
                            }
                            successfulLoad = true
                        }
                    }
                    if (!successfulLoad) {
                        try {
                            val animator = AnimatorInflater.loadAnimator(context, nextAnim)
                            if (animator != null) {
                                return AnimationOrAnimator(animator)
                            }
                        } catch (e: RuntimeException) {
                            if (isAnim) {
                                return AnimationOrAnimator()
                            }
                            // 否则很可能是动画资源
                            val animation = AnimationUtils.loadAnimation(context, nextAnim)
                            if (animation != null) {
                                return AnimationOrAnimator(animation)
                            }
                        }
                    }
                }
                return AnimationOrAnimator()
            }
        }
    }
}


