package com.xiaobin.quickbindadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

abstract class BaseLoadView<T : ViewDataBinding?>(private val layoutId: Int) {

    enum class LoadMoreState {
        LOADING, SUCCESS, FAILED, NO_MORE
    }

    //加载更多
    var loadMoreState = LoadMoreState.SUCCESS
        private set

    private var loadView: T? = null

    fun createViewHolder(parent: ViewGroup, lifecycleOwner: LifecycleOwner? = null): BindHolder {
        loadView = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId,
            parent, false
        )
        initView(loadView)
        isNoMoreData()
        return BindHolder(loadView!!, lifecycleOwner)
    }

    fun isLoading() {
        if (loadMoreState == LoadMoreState.LOADING || loadView == null) return
        loadMoreState = LoadMoreState.LOADING
        onLoading(loadView!!)
    }

    fun isNoMoreData() {
        if (loadMoreState == LoadMoreState.NO_MORE || loadView == null) return
        loadMoreState = LoadMoreState.NO_MORE
        onNoMoreData(loadView!!)
    }

    fun isLoadMoreSuccess() {
        if (loadMoreState == LoadMoreState.SUCCESS || loadView == null) return
        loadMoreState = LoadMoreState.SUCCESS
        onLoadSuccess(loadView!!)
    }

    fun isLoadMoreFailed() {
        if (loadMoreState == LoadMoreState.FAILED || loadView == null) return
        loadMoreState = LoadMoreState.FAILED
        onLoadFailed(loadView!!)
    }

    protected abstract fun initView(loadView: T?)

    /**
     * 正在加载更多
     */
    protected abstract fun onLoading(loadView: T)

    /**
     * 加载完成，没有更多
     */
    protected abstract fun onNoMoreData(loadView: T)

    /**
     * 加载成功
     */
    protected abstract fun onLoadSuccess(loadView: T)

    /**
     * 加载失败
     */
    protected abstract fun onLoadFailed(loadView: T)
}