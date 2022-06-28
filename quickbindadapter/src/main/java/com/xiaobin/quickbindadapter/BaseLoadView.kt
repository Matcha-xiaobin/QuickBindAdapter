package com.xiaobin.quickbindadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

abstract class BaseLoadView<T : ViewDataBinding?>(private val layoutId: Int) {

    enum class LoadMoreState {
        LOADING_MORE, LOAD_SUCCESS, LOAD_FAIL, LOAD_COMPLETE
    }

    //加载更多
    var loadMoreState = LoadMoreState.LOAD_SUCCESS
        private set

    protected var loadView: T? = null

    fun createViewHolder(parent: ViewGroup, lifecycleOwner: LifecycleOwner? = null): BindHolder {
        loadView = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId,
            parent, false
        )
        initView(loadView)
        isLoadMoreEnd()
        return BindHolder(loadView!!, lifecycleOwner)
    }

    fun isLoadMore() {
        if (loadMoreState == LoadMoreState.LOADING_MORE || loadView == null) return
        loadMoreState = LoadMoreState.LOADING_MORE
        onLoadMore(loadView!!)
    }

    fun isLoadMoreEnd() {
        if (loadMoreState == LoadMoreState.LOAD_COMPLETE || loadView == null) return
        loadMoreState = LoadMoreState.LOAD_COMPLETE
        onLoadEnd(loadView!!)
    }

    fun isLoadMoreSuccess() {
        if (loadMoreState == LoadMoreState.LOAD_SUCCESS || loadView == null) return
        loadMoreState = LoadMoreState.LOAD_SUCCESS
        onLoadSuccess(loadView!!)
    }

    fun isLoadMoreFail() {
        if (loadMoreState == LoadMoreState.LOAD_FAIL || loadView == null) return
        loadMoreState = LoadMoreState.LOAD_FAIL
        onLoadFail(loadView!!)
    }

    protected abstract fun initView(loadView: T?)

    /**
     * 正在加载更多
     */
    protected abstract fun onLoadMore(loadView: T)

    /**
     * 加载完成，没有更多
     */
    protected abstract fun onLoadEnd(loadView: T)

    /**
     * 加载成功
     */
    protected abstract fun onLoadSuccess(loadView: T)

    /**
     * 加载失败
     */
    protected abstract fun onLoadFail(loadView: T)
}