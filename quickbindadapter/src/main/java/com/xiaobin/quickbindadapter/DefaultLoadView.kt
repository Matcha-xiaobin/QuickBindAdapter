package com.xiaobin.quickbindadapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.ItemLoadmoreBinding

class DefaultLoadView : BaseLoadView<ItemLoadmoreBinding>(R.layout.item_loadmore) {

    private var loadMoreText = "努力加载中..."
    private var loadFailText = "加载失败了!"
    private var loadCompleteText = "没有更多数据"
    private var loadSuccessText = "加载成功"

    @ColorInt
    private var textColor = Color.LTGRAY
    fun setTextColor(@ColorInt textColor: Int) {
        this.textColor = textColor
    }

    fun setLoadMoreText(loadMoreText: String?) {
        this.loadMoreText = loadMoreText ?: ""
    }

    fun setLoadFailText(loadFailText: String?) {
        this.loadFailText = loadFailText ?: ""
    }

    fun setLoadCompleteText(loadCompleteText: String?) {
        this.loadCompleteText = loadCompleteText ?: ""
    }

    fun setLoadSuccessText(loadSuccessText: String?) {
        this.loadSuccessText = loadSuccessText ?: ""
    }

    override fun onLoadMore(loadView: ItemLoadmoreBinding) {
        loadView.loading = true
        loadView.text = loadMoreText
        loadView.tvText.setTextColor(textColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(textColor)
        }
    }

    override fun onLoadEnd(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = loadCompleteText
        loadView.tvText.setTextColor(textColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(textColor)
        }
    }

    override fun onLoadSuccess(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = loadSuccessText
        loadView.tvText.setTextColor(textColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(textColor)
        }
    }

    override fun onLoadFail(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = loadFailText
        loadView.tvText.setTextColor(textColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(textColor)
        }
    }

    override fun initView(loadView: ItemLoadmoreBinding?) {
    }
}