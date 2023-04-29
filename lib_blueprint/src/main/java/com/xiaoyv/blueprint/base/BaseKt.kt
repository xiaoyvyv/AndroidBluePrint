@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.blueprint.base

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.R
import com.xiaoyv.blueprint.base.mvvm.nav.BaseNavFragment
import com.xiaoyv.blueprint.base.mvvm.nav.BaseNavViewModel
import com.xiaoyv.blueprint.base.mvvm.nav.OnAnimationListener
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

var GlobalStringResLoading = R.string.brvah_loading

/**
 * Activity ViewBinding 泛型反射初始化
 */
@MainThread
fun <VB : ViewBinding> Activity.createBinding(): VB {
    val binding = javaClass.createBinding<VB>(layoutInflater)
        ?: javaClass.superclass?.createBinding(layoutInflater)
        ?: javaClass.superclass?.superclass?.createBinding(layoutInflater)
        ?: javaClass.superclass?.superclass?.superclass?.createBinding(layoutInflater)
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.createBinding(layoutInflater)
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.superclass?.createBinding(
            layoutInflater
        )
    return binding
        ?: throw Exception(javaClass.simpleName + " Unable to initialize ViewBinding properly, please check!")
}

/**
 * Fragment ViewBinding 泛型反射初始化
 */
@MainThread
fun <VB : ViewBinding> Fragment.createBinding(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?
): VB {
    val binding = javaClass.createBinding<VB>(layoutInflater, parent)
        ?: javaClass.superclass?.createBinding(layoutInflater, parent)
        ?: javaClass.superclass?.superclass?.createBinding(layoutInflater, parent)
        ?: javaClass.superclass?.superclass?.superclass?.createBinding(layoutInflater, parent)
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.createBinding(
            layoutInflater,
            parent
        )
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.superclass?.createBinding(
            layoutInflater,
            parent
        )
    return binding
        ?: throw Exception(javaClass.simpleName + " Unable to initialize ViewBinding properly, please check!")
}

@MainThread
private fun <VB : ViewBinding> Class<*>.createBinding(
    layoutInflater: LayoutInflater,
    parent: ViewGroup? = null,
    attach: Boolean = false
): VB? {
    val genericSuperclass = this.genericSuperclass
    if (genericSuperclass is ParameterizedType) {
        // 检查泛型
        genericSuperclass.actualTypeArguments.forEach {
            val bindingCls = it as Class<*>

            val implViewBinding = bindingCls.checkImplViewBinding()
            if (implViewBinding) {
                val inflateMethod = bindingCls.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
                inflateMethod.isAccessible = true
                return inflateMethod.invoke(null, layoutInflater, parent, attach) as? VB
            }
        }
    }
    return null
}

/**
 * 检查该类是否实现了 ViewBinding 接口
 */
@MainThread
private fun Class<*>.checkImplViewBinding(): Boolean {
    genericInterfaces.forEach {
        if (it == ViewBinding::class.java) {
            return true
        }
    }
    return false
}


/**
 * Fragment ViewBinding 泛型反射初始化
 *
 * ```
 * abstract class BaseNavFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {
 *     private var _binding: VB? = null
 *     val binding get() = _binding!!
 *     ...
 *
 *     val viewModel: VM by createViewModel()
 *
 *     ...
 * }
 * ```
 */
@MainThread
fun <VM : ViewModel> Fragment.createViewModel(
    ownerProducer: () -> ViewModelStoreOwner = { this },
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val viewModelClass = javaClass.createViewModel<VM>()
        ?: javaClass.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.superclass?.createViewModel()
        ?: throw Exception(javaClass.simpleName + " Unable to initialize ViewModel properly, please check!")

    val owner by lazy(LazyThreadSafetyMode.NONE) {
        ownerProducer()
    }

    return createViewModelLazy(
        viewModelClass = viewModelClass,
        storeProducer = {
            owner.viewModelStore
        },
        extrasProducer = {
            (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
                ?: CreationExtras.Empty
        },
        factoryProducer = factoryProducer ?: {
            (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
                ?: defaultViewModelProviderFactory
        })
}


@MainThread
fun <VM : ViewModel> ComponentActivity.createViewModel(
    extrasProducer: (() -> CreationExtras)? = null,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val viewModelClass = javaClass.createViewModel<VM>()
        ?: javaClass.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.createViewModel()
        ?: javaClass.superclass?.superclass?.superclass?.superclass?.superclass?.createViewModel()
        ?: throw Exception(javaClass.simpleName + " Unable to initialize ViewModel properly, please check!")

    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ViewModelLazy(
        viewModelClass = viewModelClass,
        storeProducer = { viewModelStore },
        factoryProducer = factoryPromise,
        extrasProducer = { extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras }
    )
}

@MainThread
fun <VM : ViewModel> Class<*>.createViewModel(): KClass<VM>? {
    val genericSuperclass = this.genericSuperclass
    if (genericSuperclass is ParameterizedType) {
        // 检查父类
        genericSuperclass.actualTypeArguments.forEach {
            val viewModelCls = it as Class<*>

            val implViewModel = viewModelCls.checkImplViewModel()
            if (implViewModel) {
                return viewModelCls.kotlin as KClass<VM>
            }
        }
    }
    return null
}

/**
 * 检查该类是否继承了 ViewModel 接口
 */
@MainThread
private fun Class<*>.checkImplViewModel(): Boolean {
    if (superclass == ViewModel::class.java
        || superclass?.superclass == ViewModel::class.java
        || superclass?.superclass?.superclass == ViewModel::class.java
        || superclass?.superclass?.superclass?.superclass == ViewModel::class.java
        || superclass?.superclass?.superclass?.superclass?.superclass == ViewModel::class.java
        || superclass?.superclass?.superclass?.superclass?.superclass?.superclass == ViewModel::class.java
    ) {
        return true
    }
    return false
}


/**
 * 跳转
 */
@MainThread
fun <VB : ViewBinding, VM : BaseNavViewModel> BaseNavFragment<VB, VM>.navigate(
    @IdRes resId: Int, args: Bundle? = null,
    options: NavOptions? = navOptions {
        anim {
            enter = R.anim.anim_slide_in_right
            exit = R.anim.anim_slide_out_left
            popEnter = R.anim.anim_slide_in_left
            popExit = R.anim.anim_slide_out_right
        }
    }
) {
    navController.navigate(resId, args, options)
}


fun Animation?.bindListener(animationListener: OnAnimationListener, enter: Boolean): Animation? {
    if (enter.not()) {
        return this
    }
    this?.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            animationListener.onAnimationStart()
        }

        override fun onAnimationEnd(animation: Animation?) {
            animationListener.onAnimationEnd()
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }
    })
    return this
}

fun Animator?.bindListener(animationListener: OnAnimationListener, enter: Boolean): Animator? {
    if (enter.not()) {
        return this
    }
    this?.addListener(
        onStart = {
            animationListener.onAnimationStart()
        },
        onEnd = {
            animationListener.onAnimationEnd()
        }
    )
    return this
}
