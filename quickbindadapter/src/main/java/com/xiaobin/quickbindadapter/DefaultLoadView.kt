package com.xiaobin.quickbindadapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.xiaobin.quickbindadapter.databinding.McXbItemLoadmoreBinding

class DefaultLoadViewConfigsBean : Cloneable {
    var onLoadingText: String = ""
    var onFailedText: String = ""
    var onWaitLoadingText: String = ""
    var noMoreDataText: String = ""
    var onSuccessText: String = ""

    @ColorInt
    var onLoadingTextColor: Int = Color.LTGRAY

    @ColorInt
    var onWaitLoadingTextColor: Int = Color.LTGRAY

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
    BaseLoadView<McXbItemLoadmoreBinding>(R.layout.mc_xb_item_loadmore) {

    companion object {
        //全局配置
        var globalConfig: DefaultLoadViewConfigsBean? = null
            private set

        /**
         * 调用set方法，全局替换默认加载更多样式为自定义的加载更多样式
         * 如果统一设置为自定义的加载更多样式，那么就不需要配置全局配置项了，因为那个是给默认加载更多样式用的
         */
        var defaultLoadView: (Context) -> BaseLoadView<*> = {
            DefaultLoadView(it)
        }

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
                onWaitLoadingText = context.getString(R.string.load_onWaitLoading)
                noMoreDataText = context.getString(R.string.load_noMoreData)
                onSuccessText = context.getString(R.string.load_onSuccess)
                onLoadingTextColor = Color.LTGRAY
                onWaitLoadingTextColor = Color.LTGRAY
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

    fun setOnWaitLoadingText(text: String) {
        config.onWaitLoadingText = text
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

    fun setOnWaitLoadingTextColor(@ColorInt color: Int) {
        config.onWaitLoadingTextColor = color
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

    override fun initView(loadView: McXbItemLoadmoreBinding?) {
    }

    override fun onStateChange(loadView: McXbItemLoadmoreBinding, state: LoadMoreState) {
        when (state) {
            LoadMoreState.LOADING -> {
                loadView.loading = true
                loadView.text = config.onLoadingText
                loadView.tvText.setTextColor(config.onLoadingTextColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadView.progress.progressTintList =
                        ColorStateList.valueOf(config.onLoadingTextColor)
                }
            }
            LoadMoreState.SUCCESS -> {
                loadView.loading = false
                var text = config.onSuccessText
                if (text.isBlank()) {
                    text = context.getString(R.string.load_onSuccess)
                }
                loadView.text = text
                loadView.tvText.setTextColor(config.onSuccessTextColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadView.progress.progressTintList =
                        ColorStateList.valueOf(config.onSuccessTextColor)
                }
            }
            LoadMoreState.FAILED -> {
                loadView.loading = false
                var text = config.onFailedText
                if (text.isBlank()) {
                    text = context.getString(R.string.load_onFailed)
                }
                loadView.text = text
                loadView.tvText.setTextColor(config.onFailedTextColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadView.progress.progressTintList =
                        ColorStateList.valueOf(config.onFailedTextColor)
                }
            }
            LoadMoreState.NO_MORE -> {
                loadView.loading = false
                var text = config.noMoreDataText
                if (text.isBlank()) {
                    text = context.getString(R.string.load_noMoreData)
                }
                loadView.text = text
                loadView.tvText.setTextColor(config.noMoreDataTextColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadView.progress.progressTintList =
                        ColorStateList.valueOf(config.noMoreDataTextColor)
                }
            }
            LoadMoreState.WAIT_LOADING -> {
                loadView.loading = false
                var text = config.onWaitLoadingText
                if (text.isBlank()) {
                    text = context.getString(R.string.load_onWaitLoading)
                }
                loadView.text = text
                loadView.tvText.setTextColor(config.onWaitLoadingTextColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadView.progress.progressTintList =
                        ColorStateList.valueOf(config.onWaitLoadingTextColor)
                }
            }
            else -> {}
        }
    }

}