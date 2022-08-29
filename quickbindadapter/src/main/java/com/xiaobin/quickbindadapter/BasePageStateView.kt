package com.xiaobin.quickbindadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.xiaobin.quickbindadapter.databinding.McXbLayoutPlacePageBinding

abstract class BasePageStateView<
        EmptyPage : ViewDataBinding,
        ErrorPage : ViewDataBinding,
        LoadingPage : ViewDataBinding
        >(
    private val emptyPageLayoutId: Int = 0,
    private val errorPageLayoutId: Int = 0,
    private val loadingPageLayoutId: Int = 0,
) {

    private var viewHolder: BindHolder? = null
    private var emptyPageDecorView: McXbLayoutPlacePageBinding? = null
    private var lifecycleOwner: LifecycleOwner? = null

    private var currentPageState: PageState = PageState.Loading

    var emptyView: EmptyPage? = null
    var loadingView: LoadingPage? = null
    var errorView: ErrorPage? = null

    /**
     * 三种状态的页面不是一开始就创建了的，所以这个方法可以监听到哪个页面被首次创建
     */
    protected abstract fun onPageCreate(action: PageState)

    /**
     * 当切换状态的时候，这里可以监听到当前是哪个页面
     */
    protected abstract fun onActionCall(action: PageState)

    /**
     * 设置默认的页面，并切换到这个页面
     */
    fun setDefaultPage(pageState: PageState) {
        currentPageState = pageState
    }

    /**
     * 获取当前的页面状态
     */
    fun getCurrentPageState(): PageState {
        return currentPageState
    }

    fun createViewHolder(parent: ViewGroup, mLifecycleOwner: LifecycleOwner? = null): BindHolder {
        if (viewHolder != null) return viewHolder!!
        emptyPageDecorView = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.mc_xb_layout_place_page,
            parent,
            false
        )
        mLifecycleOwner?.apply {
            lifecycleOwner = this
            emptyPageDecorView!!.lifecycleOwner = this
        }
        setPageState(currentPageState)
        viewHolder = BindHolder(emptyPageDecorView!!, mLifecycleOwner)
        return viewHolder!!
    }

    private fun <T : ViewDataBinding> createPageViewBinding(layoutId: Int): T? {
        if (layoutId == 0) return null
        val parent = emptyPageDecorView!!.flPlaceholder
        val pageBindingView: T = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId, parent, false
        )
        lifecycleOwner?.let {
            pageBindingView.lifecycleOwner = it
        }
        return pageBindingView
    }

    private fun getEmptyPage(): EmptyPage? {
        if (!check()) return null
        return if (emptyPageLayoutId == 0) {
            null
        } else if (emptyView != null) {
            lifecycleOwner?.let {
                emptyView!!.lifecycleOwner = it
            }
            emptyView
        } else {
            emptyView = createPageViewBinding(emptyPageLayoutId)
            onPageCreate(PageState.Empty)
            emptyView
        }
    }

    private fun getLoadingPage(): LoadingPage? {
        if (!check()) return null
        return if (loadingPageLayoutId == 0) {
            null
        } else if (loadingView != null) {
            lifecycleOwner?.let {
                loadingView!!.lifecycleOwner = it
            }
            loadingView
        } else {
            loadingView = createPageViewBinding(loadingPageLayoutId)
            onPageCreate(PageState.Loading)
            loadingView
        }
    }

    private fun getErrorPage(): ErrorPage? {
        if (!check()) return null
        return if (errorPageLayoutId == 0) {
            null
        } else if (errorView != null) {
            lifecycleOwner?.let {
                errorView!!.lifecycleOwner = it
            }
            errorView
        } else {
            errorView = createPageViewBinding(errorPageLayoutId)
            onPageCreate(PageState.Error)
            errorView
        }
    }

    fun refreshPage() {
        setPageState(currentPageState)
    }

    fun setPageState(action: PageState) {
        if (!check() && currentPageState != action) {
            setDefaultPage(action)
            return
        }
        val view: View? = when (action) {
            PageState.Empty -> {
                getEmptyPage()?.root
            }
            PageState.Error -> {
                getErrorPage()?.root
            }
            PageState.Loading -> {
                getLoadingPage()?.root
            }
        }
        emptyPageDecorView?.flPlaceholder?.removeAllViews()
        view?.apply {
            emptyPageDecorView?.flPlaceholder?.addView(this, -1, -1)
        }
        currentPageState = action
        onActionCall(action)
    }

    private fun check(): Boolean {
        if (emptyPageDecorView == null) {
            return false
        }
        return true
    }
}

enum class PageState {
    Loading, Empty, Error
}