package com.xiaobin.quickbindadapter.paging

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.Space
import androidx.lifecycle.LifecycleOwner
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xiaobin.quickbindadapter.BindHolder
import com.xiaobin.quickbindadapter.holder.EmptyViewHolder
import com.xiaobin.quickbindadapter.holder.LoadViewHolder
import com.xiaobin.quickbindadapter.view.BaseLoadView
import com.xiaobin.quickbindadapter.view.BasePageStateView
import com.xiaobin.quickbindadapter.view.DefaultEmptyStatePage
import com.xiaobin.quickbindadapter.view.DefaultLoadView

/**
 * 空数据占位布局
 */
class EmptyPageAdapter(
    val context: Context,
    val getCount: () -> Int,
    val pageView: BasePageStateView<*, *, *>? = null,
    val loadView: BaseLoadView<*>? = null,
    val lifecycleOwner: LifecycleOwner? = null,
    val enableEmptyPage: Boolean = true,
    val enableLoadMoreItem: Boolean = false,
    val retry: () -> Unit = {},
) : LoadStateAdapter<BindHolder>() {

    companion object {
        const val LOAD_VIEW_TYPE = -1
        const val EMPTY_VIEW_TYPE = -2
    }

    private var mRecyclerView: RecyclerView? = null

    private fun showLoadItem(): Boolean {
        return getCount.invoke() > 0
    }

    override fun getStateViewType(loadState: LoadState): Int {
        return if (showLoadItem()) LOAD_VIEW_TYPE else EMPTY_VIEW_TYPE
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return enableLoadMoreItem || (enableEmptyPage && !showLoadItem())
    }

    override fun onBindViewHolder(holder: BindHolder, loadState: LoadState) {
        //两种布局，分别绑定
        if (holder is LoadViewHolder) {
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
        } else if (holder is EmptyViewHolder) {
            when (loadState) {
                is LoadState.Error -> holder.showErrorPage()
                LoadState.Loading -> holder.showLoadingPage()
                is LoadState.NotLoading -> holder.showEmptyPage()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BindHolder {
        return if (showLoadItem() && enableLoadMoreItem) {
            loadView?.createViewHolder(parent, lifecycleOwner)
                ?: DefaultLoadView.defaultLoadView(parent.context)
                    .createViewHolder(parent, lifecycleOwner) {
                        retry()
                    }
        } else if (!showLoadItem() && enableEmptyPage) {
            pageView?.createViewHolder(parent, lifecycleOwner)
                ?: DefaultEmptyStatePage(parent.context).createViewHolder(parent, lifecycleOwner)
        } else {
            BindHolder(Space(parent.context))
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }
}