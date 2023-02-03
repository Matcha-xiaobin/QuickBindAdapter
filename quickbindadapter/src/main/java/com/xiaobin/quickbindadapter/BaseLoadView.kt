package com.xiaobin.quickbindadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

abstract class BaseLoadView<T : ViewDataBinding?>(private val layoutId: Int) {

    enum class LoadMoreState {
        LOADING, SUCCESS, FAILED, NO_MORE, WAIT_LOADING, NO_STATE
    }

    private var viewHolder: BindHolder? = null

    //加载更多
    var loadMoreState = LoadMoreState.NO_STATE
        private set

    private var loadView: T? = null

    fun createViewHolder(
        parent: ViewGroup,
        lifecycleOwner: LifecycleOwner? = null,
        onClickLoadMore: View.OnClickListener? = null
    ): BindHolder {
        loadView = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutId,
            parent, false
        )
        initView(loadView)
        loadView!!.root.setOnClickListener {
            if (loadMoreState == LoadMoreState.NO_MORE ||
                loadMoreState == LoadMoreState.LOADING ||
                loadMoreState == LoadMoreState.SUCCESS ||
                loadView == null
            ) {
                //加载中，加载结束并且没有更多数据时，不允许触发点击事件
                return@setOnClickListener
            }
            onClickLoadMore?.onClick(it)
        }
        //恢复状态
        when (loadMoreState) {
            LoadMoreState.LOADING -> isLoading(true)
            LoadMoreState.SUCCESS -> isLoadMoreSuccess(true)
            LoadMoreState.FAILED -> isLoadMoreFailed(true)
            LoadMoreState.NO_MORE -> isNoMoreData(true)
            LoadMoreState.WAIT_LOADING -> isWaitLoading(true)
            LoadMoreState.NO_STATE -> {}
        }
        viewHolder = BindHolder(loadView!!, lifecycleOwner)
        return viewHolder!!
    }

    /**
     * 变更为 等待用户点击加载更多 状态
     * 禁止了自动加载更多的时候，才会调用这个方法
     */
    fun isWaitLoading(ignore: Boolean = false) {
        if (!ignore && (loadMoreState == LoadMoreState.WAIT_LOADING || loadView == null)) return
        loadMoreState = LoadMoreState.WAIT_LOADING
        onStateChange(loadView!!, LoadMoreState.WAIT_LOADING)
    }

    /**
     * 变更为 正在加载更多 状态
     */
    fun isLoading(ignore: Boolean = false) {
        if (!ignore && (loadMoreState == LoadMoreState.LOADING || loadView == null)) return
        loadMoreState = LoadMoreState.LOADING
        onStateChange(loadView!!, LoadMoreState.LOADING)
    }

    /**
     * 变更为 加载更多完成且无更多数据 状态
     */
    fun isNoMoreData(ignore: Boolean = false) {
        if (!ignore && (loadMoreState == LoadMoreState.NO_MORE || loadView == null)) return
        loadMoreState = LoadMoreState.NO_MORE
        onStateChange(loadView!!, LoadMoreState.NO_MORE)
    }

    /**
     * 变更为 加载更多成功 状态
     */
    fun isLoadMoreSuccess(ignore: Boolean = false) {
        if (!ignore && (loadMoreState == LoadMoreState.SUCCESS || loadView == null)) return
        loadMoreState = LoadMoreState.SUCCESS
        onStateChange(loadView!!, LoadMoreState.SUCCESS)
    }

    /**
     * 变更为 加载更多失败了 状态
     */
    fun isLoadMoreFailed(ignore: Boolean = false) {
        if (!ignore && (loadMoreState == LoadMoreState.FAILED || loadView == null)) return
        loadMoreState = LoadMoreState.FAILED
        onStateChange(loadView!!, LoadMoreState.FAILED)
    }

    protected abstract fun initView(loadView: T?)

    /**
     * 状态变化
     */
    protected abstract fun onStateChange(loadView: T, state: LoadMoreState)

}