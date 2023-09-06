package com.xiaobin.quickbindadapter.paging

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.xiaobin.quickbindadapter.holder.LoadViewHolder
import com.xiaobin.quickbindadapter.view.DefaultLoadView

/**
 * 加载更多布局
 */
class LoadMoreAdapter(val lifecycleOwner: LifecycleOwner? = null, val retry: () -> Unit = {}) :
    LoadStateAdapter<LoadViewHolder>() {

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error
                || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
    }

    override fun onBindViewHolder(holder: LoadViewHolder, loadState: LoadState) {
        when (loadState) {
            is LoadState.Loading -> holder.onLoading()
            is LoadState.Error -> holder.onError()
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    holder.noMore()
                } else {
                    holder.onWait()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadViewHolder {
        val loadMoreItemView = DefaultLoadView.defaultLoadView(parent.context)
        return loadMoreItemView.createViewHolder(parent, lifecycleOwner) {
            retry()
        }
    }
}