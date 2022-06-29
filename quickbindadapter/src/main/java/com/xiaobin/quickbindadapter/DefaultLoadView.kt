package com.xiaobin.quickbindadapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.ItemLoadmoreBinding

class DefaultLoadViewConfigsBean : Cloneable {
    var onLoadingText: String = ""
    var onFailedText: String = ""
    var noMoreDataText: String = ""
    var onSuccessText: String = ""

    @ColorInt
    var onLoadingTextColor: Int = Color.LTGRAY

    @ColorInt
    var onFailedTextColor: Int = Color.LTGRAY

    @ColorInt
    var noMoreDataTextColor: Int = Color.LTGRAY

    @ColorInt
    var onSuccessTextColor: Int = Color.LTGRAY

    public override fun clone(): DefaultLoadViewConfigsBean {
        return try {
            return super.clone() as? DefaultLoadViewConfigsBean
                ?: DefaultLoadViewConfigsBean()
        } catch (e: CloneNotSupportedException) {
            DefaultLoadViewConfigsBean()
        }
    }
}

/**
 * 默认的加载更多Item布局
 */
class DefaultLoadView(private val context: Context) :
    BaseLoadView<ItemLoadmoreBinding>(R.layout.item_loadmore) {

    companion object {
        //全局配置
        var globalConfig: DefaultLoadViewConfigsBean? = null
            private set

        //创建全局配置
        fun createGlobalConfig(result: () -> DefaultLoadViewConfigsBean) {
            globalConfig = result.invoke()
        }

        //创建全局配置
        fun createGlobalConfig(result: DefaultLoadViewConfigsBean) {
            globalConfig = result
        }
    }

    init {
        if (globalConfig == null) {
            createGlobalConfig(DefaultLoadViewConfigsBean().apply {
                onLoadingText = context.getString(R.string.load_onLoading)
                onFailedText = context.getString(R.string.load_onFailed)
                noMoreDataText = context.getString(R.string.load_noMoreData)
                onSuccessText = context.getString(R.string.load_onSuccess)
                onLoadingTextColor = Color.LTGRAY
                onFailedTextColor = Color.LTGRAY
                noMoreDataTextColor = Color.LTGRAY
                onSuccessTextColor = Color.LTGRAY
            })
        }
    }

    private var config: DefaultLoadViewConfigsBean = globalConfig!!.clone()
        set(value) {
            field = if (value === globalConfig) {
                //如果是设置的全局配置，则克隆一份
                value.clone()
            } else {
                value
            }
        }

    fun setOnLoadingText(text: String) {
        config.onLoadingText = text
    }

    fun setOnFailedText(text: String) {
        config.onFailedText = text
    }

    fun setNoMoreDataText(text: String) {
        config.noMoreDataText = text
    }

    fun setOnSuccessText(text: String) {
        config.onSuccessText = text
    }

    fun setOnLoadingTextColor(@ColorInt color: Int) {
        config.onLoadingTextColor = color
    }

    fun setOnFailedTextColor(@ColorInt color: Int) {
        config.onFailedTextColor = color
    }

    fun setNoMoreDataTextColor(@ColorInt color: Int) {
        config.noMoreDataTextColor = color
    }

    fun setOnSuccessTextColor(@ColorInt color: Int) {
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