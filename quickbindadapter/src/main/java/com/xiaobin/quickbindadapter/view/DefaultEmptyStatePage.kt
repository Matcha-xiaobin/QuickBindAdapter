package com.xiaobin.quickbindadapter.view

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.R
import com.xiaobin.quickbindadapter.databinding.*

class DefaultPlacePageConfigsBean : Cloneable {
    var emptyText: String = ""
    var errorText: String = ""

    @ColorInt
    var emptyTextColor: Int = Color.LTGRAY

    @ColorInt
    var errorTextColor: Int = Color.RED

    public override fun clone(): DefaultPlacePageConfigsBean {
        return try {
            return super.clone() as? DefaultPlacePageConfigsBean
                ?: DefaultPlacePageConfigsBean()
        } catch (e: CloneNotSupportedException) {
            DefaultPlacePageConfigsBean()
        }
    }
}

/**
 * 默认的 状态 页面
 */
class DefaultEmptyStatePage(private val context: Context) :
    BasePageStateView<McXbLayoutPageEmptyBinding, McXbLayoutPageErrorBinding, McXbLayoutPageLoadingBinding>(
        R.layout.mc_xb_layout_page_empty,
        R.layout.mc_xb_layout_page_error,
        R.layout.mc_xb_layout_page_loading,
    ) {

    companion object {
        //全局配置
        var globalConfig: DefaultPlacePageConfigsBean? = null
            private set

        /**
         * 调用set方法，全局替换默认状态页为自定义的状态页
         * 如果统一设置为自定义的状态页，那么就不需要配置全局配置项了，因为那个是给默认状态页用的
         */
        var defaultStatePage: (Context) -> BasePageStateView<*, *, *> = {
            DefaultEmptyStatePage(it)
        }

        //创建全局配置
        fun createGlobalConfig(result: () -> DefaultPlacePageConfigsBean) {
            globalConfig = result.invoke()
        }

        //创建全局配置
        fun createGlobalConfig(result: DefaultPlacePageConfigsBean) {
            globalConfig = result
        }
    }

    init {
        if (globalConfig == null) {
            createGlobalConfig(DefaultPlacePageConfigsBean().apply {
                emptyText = context.getString(R.string.place_onEmpty)
                errorText = context.getString(R.string.place_onError)
                emptyTextColor = Color.LTGRAY
                errorTextColor = Color.RED
            })
        }
    }

    private var config: DefaultPlacePageConfigsBean = globalConfig!!.clone()
        set(value) {
            field = if (value === globalConfig) {
                //如果是设置的全局配置，则克隆一份
                value.clone()
            } else {
                value
            }
        }

    override fun onPageCreate(action: PageState) {
        when (action) {
            PageState.Empty -> {
                emptyView?.apply {
                    tvEmpty.setTextColor(config.emptyTextColor)
                    tvEmpty.text = config.emptyText
                }

            }
            PageState.Error -> {
                errorView?.apply {
                    tvError.setTextColor(config.errorTextColor)
                    tvError.text = config.errorText
                }
            }
            PageState.Loading -> {
            }
            PageState.Finish -> {
            }
        }
    }

    override fun onActionCall(action: PageState) {
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