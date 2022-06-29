package com.xiaobin.quickbindadapter

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.LayoutPageEmptyBinding
import com.xiaobin.quickbindadapter.databinding.LayoutPageErrorBinding
import com.xiaobin.quickbindadapter.databinding.LayoutPageLoadingBinding

class DefaultPlaceholderConfigsBean : Cloneable {
    var emptyText: String = ""
    var errorText: String = ""

    @ColorInt
    var emptyTextColor: Int = Color.LTGRAY

    @ColorInt
    var errorTextColor: Int = Color.RED

    public override fun clone(): DefaultPlaceholderConfigsBean {
        return try {
            return super.clone() as? DefaultPlaceholderConfigsBean
                ?: DefaultPlaceholderConfigsBean()
        } catch (e: CloneNotSupportedException) {
            DefaultPlaceholderConfigsBean()
        }
    }
}

/**
 * 默认的加载更多布局
 */
class DefaultPlaceholder(private val context: Context) :
    BasePlaceholder<LayoutPageEmptyBinding, LayoutPageErrorBinding, LayoutPageLoadingBinding>(
        R.layout.layout_page_empty,
        R.layout.layout_page_error,
        R.layout.layout_page_loading,
    ) {

    companion object {
        //全局配置
        var globalConfig: DefaultPlaceholderConfigsBean? = null
            private set

        //创建全局配置
        fun createGlobalConfig(result: () -> DefaultPlaceholderConfigsBean) {
            globalConfig = result.invoke()
        }

        //创建全局配置
        fun createGlobalConfig(result: DefaultPlaceholderConfigsBean) {
            globalConfig = result
        }
    }

    init {
        if (globalConfig == null) {
            createGlobalConfig(DefaultPlaceholderConfigsBean().apply {
                emptyText = context.getString(R.string.place_onEmpty)
                errorText = context.getString(R.string.place_onError)
                emptyTextColor = Color.LTGRAY
                errorTextColor = Color.RED
            })
        }
    }

    private var config: DefaultPlaceholderConfigsBean = globalConfig!!.clone()
        set(value) {
            field = if (value === globalConfig) {
                //如果是设置的全局配置，则克隆一份
                value.clone()
            } else {
                value
            }
        }

    override fun onPageCreate(action: PlaceholderAction) {
        when (action) {
            PlaceholderAction.ShowEmptyPage -> {
                emptyView?.apply {
                    tvEmpty.setTextColor(config.emptyTextColor)
                    tvEmpty.text = config.emptyText
                }

            }
            PlaceholderAction.ShowErrPage -> {
                errorView?.apply {
                    tvError.setTextColor(config.errorTextColor)
                    tvError.text = config.errorText
                }
            }
            PlaceholderAction.ShowLoadingPage -> {
            }
        }
    }

    override fun onActionCall(action: PlaceholderAction) {
    }

    fun setErrorPageTextColor(@ColorInt textColor: Int) {
        config.errorTextColor = textColor
        errorView?.apply {
            tvError.setTextColor(config.errorTextColor)
        }
    }

    fun setErrorPageText(text: String) {
        config.errorText = text
        errorView?.apply {
            tvError.text = config.errorText
        }
    }

    fun setEmptyPageTextColor(@ColorInt textColor: Int) {
        config.emptyTextColor = textColor
        emptyView?.apply {
            tvEmpty.setTextColor(config.emptyTextColor)
        }
    }

    fun setEmptyPageText(text: String) {
        config.emptyText = text
        emptyView?.apply {
            tvEmpty.text = config.emptyText
        }
    }

}