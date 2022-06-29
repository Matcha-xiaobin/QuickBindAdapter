package com.xiaobin.quickbindadapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.ItemLoadmoreBinding

/**
 * 默认的加载更多Item布局
 */
class DefaultLoadView : BaseLoadView<ItemLoadmoreBinding>(R.layout.item_loadmore) {

    companion object {
        class DefaultLoadViewConfigsBean(
            var onLoadingText: String = "努力加载中...",
            var onFailedText: String = "加载失败了!",
            var noMoreDataText: String = "没有更多数据",
            var onSuccessText: String = "加载成功",
            @ColorInt
            var onLoadingTextColor: Int = Color.LTGRAY,
            @ColorInt
            var onFailedTextColor: Int = Color.LTGRAY,
            @ColorInt
            var noMoreDataTextColor: Int = Color.LTGRAY,
            @ColorInt
            var onSuccessTextColor: Int = Color.LTGRAY
        )

        var globalConfig: DefaultLoadViewConfigsBean? = null
            get() {
                if (field == null) {
                    field = DefaultLoadViewConfigsBean()
                }
                return field
            }
            private set

        fun createGlobalConfig(result: (DefaultLoadViewConfigsBean) -> Unit) {
            result.invoke(globalConfig!!)
        }
    }

    var config: DefaultLoadViewConfigsBean

    init {
        config = globalConfig!!
    }

    private fun isUserConfig() {
        if (config == globalConfig) {
            config = DefaultLoadViewConfigsBean()
        }
    }

    fun setOnLoadingText(text: String) {
        isUserConfig()
        config.onLoadingText = text
    }

    fun setOnFailedText(text: String) {
        isUserConfig()
        config.onFailedText = text
    }

    fun setNoMoreDataText(text: String) {
        isUserConfig()
        config.noMoreDataText = text
    }

    fun setOnSuccessText(text: String) {
        isUserConfig()
        config.onSuccessText = text
    }

    fun setOnLoadingTextColor(@ColorInt color: Int) {
        isUserConfig()
        config.onLoadingTextColor = color
    }

    fun setOnFailedTextColor(@ColorInt color: Int) {
        isUserConfig()
        config.onFailedTextColor = color
    }

    fun setNoMoreDataTextColor(@ColorInt color: Int) {
        isUserConfig()
        config.noMoreDataTextColor = color
    }

    fun setOnSuccessTextColor(@ColorInt color: Int) {
        isUserConfig()
        config.onSuccessTextColor = color
    }

    override fun onLoading(loadView: ItemLoadmoreBinding) {
        loadView.loading = true
        loadView.text = config.onLoadingText
        loadView.tvText.setTextColor(config.onLoadingTextColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(config.onLoadingTextColor)
        }
    }

    override fun onNoMoreData(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = config.noMoreDataText
        loadView.tvText.setTextColor(config.noMoreDataTextColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(config.noMoreDataTextColor)
        }
    }

    override fun onLoadSuccess(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = config.onSuccessText
        loadView.tvText.setTextColor(config.onSuccessTextColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(config.onSuccessTextColor)
        }
    }

    override fun onLoadFailed(loadView: ItemLoadmoreBinding) {
        loadView.loading = false
        loadView.text = config.onFailedText
        loadView.tvText.setTextColor(config.onFailedTextColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.progressTintList = ColorStateList.valueOf(config.onFailedTextColor)
        }
    }

    override fun initView(loadView: ItemLoadmoreBinding?) {
    }
}