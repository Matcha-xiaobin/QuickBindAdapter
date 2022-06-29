package com.xiaobin.quickbindadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.xiaobin.quickbindadapter.databinding.LayoutPlaceholderBinding

abstract class BasePlaceholder<
        EmptyPage : ViewDataBinding,
        ErrorPage : ViewDataBinding,
        LoadingPage : ViewDataBinding
        >
    (
    private val emptyPageId: Int,
    private val errorPageId: Int,
    private val loadingPageId: Int,
) {

    private var placeholderDecorView: LayoutPlaceholderBinding? = null
    private var lifecycleOwner: LifecycleOwner? = null

    var emptyView: EmptyPage? = null
    var loadingView: LoadingPage? = null
    var errorView: ErrorPage? = null

    protected abstract fun onPageCreate(action: PlaceholderAction)
    protected abstract fun onActionCall(action: PlaceholderAction)

    fun hasEmptyPage(): Boolean = emptyView != null
    fun hasLoadingPage(): Boolean = loadingView != null
    fun hasErrorPage(): Boolean = errorView != null

    fun createViewHolder(parent: ViewGroup, mLifecycleOwner: LifecycleOwner? = null): BindHolder {
        placeholderDecorView = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_placeholder,
            parent,
            false
        )
        mLifecycleOwner?.apply {
            lifecycleOwner = this
            placeholderDecorView!!.lifecycleOwner = this
        }
        setPlaceholderAction(PlaceholderAction.ShowLoadingPage)
        return BindHolder(placeholderDecorView!!, mLifecycleOwner)
    }

    private fun <T : ViewDataBinding> createPageViewBinding(layoutId: Int): T {
        val parent = placeholderDecorView!!.flPlaceholder
        val pageBindingView: T =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
        lifecycleOwner?.let {
            pageBindingView.lifecycleOwner = it
        }
        return pageBindingView
    }

    private fun getEmptyPage(): EmptyPage? {
        check()
        return if (hasEmptyPage()) {
            lifecycleOwner?.let {
                emptyView!!.lifecycleOwner = it
            }
            emptyView
        } else {
            emptyView = createPageViewBinding(emptyPageId)
            onPageCreate(PlaceholderAction.ShowEmptyPage)
            emptyView
        }
    }

    private fun getLoadingPage(): LoadingPage? {
        check()
        return if (hasLoadingPage()) {
            lifecycleOwner?.let {
                loadingView!!.lifecycleOwner = it
            }
            loadingView
        } else {
            loadingView = createPageViewBinding(loadingPageId)
            onPageCreate(PlaceholderAction.ShowLoadingPage)
            loadingView
        }
    }

    private fun getErrorPage(): ErrorPage? {
        check()
        return if (hasErrorPage()) {
            lifecycleOwner?.let {
                errorView!!.lifecycleOwner = it
            }
            errorView
        } else {
            errorView = createPageViewBinding(errorPageId)
            onPageCreate(PlaceholderAction.ShowErrPage)
            errorView
        }
    }

    fun setPlaceholderAction(action: PlaceholderAction) {
        check()
        onActionCall(action)
        when (action) {
            PlaceholderAction.ShowEmptyPage -> {
                getEmptyPage()?.apply {
                    placeholderDecorView?.flPlaceholder?.removeAllViews()
                    placeholderDecorView?.flPlaceholder?.addView(root, -1, -1)
                }
            }
            PlaceholderAction.ShowErrPage -> {
                getErrorPage()?.apply {
                    placeholderDecorView?.flPlaceholder?.removeAllViews()
                    placeholderDecorView?.flPlaceholder?.addView(root, -1, -1)
                }
            }
            PlaceholderAction.ShowLoadingPage -> {
                getLoadingPage()?.apply {
                    placeholderDecorView?.flPlaceholder?.removeAllViews()
                    placeholderDecorView?.flPlaceholder?.addView(root, -1, -1)
                }
            }
        }
    }

    private fun check() {
        if (placeholderDecorView == null) {
            throw Exception("请先初始化")
        }
    }
}

sealed class PlaceholderAction {
    object ShowLoadingPage : PlaceholderAction()
    object ShowEmptyPage : PlaceholderAction()
    object ShowErrPage : PlaceholderAction()
}