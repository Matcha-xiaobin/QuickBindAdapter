package com.xiaobin.quickbindadapter

import androidx.databinding.ViewDataBinding

/**
 * @author 小斌
 * @data 2019/7/31
 */
interface QuickBind {
    /**
     * 和 常规的adapter中的onBindViewHolder，基本相同的用法
     *
     * @param binding  databinding
     * @param itemData item数据
     */
    fun onBind(binding: ViewDataBinding, itemData: Any, position: Int)
}