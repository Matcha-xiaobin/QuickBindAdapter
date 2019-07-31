package com.xiaobin.quickbindadapter;

import androidx.databinding.ViewDataBinding;

/**
 * @author 小斌
 * @data 2019/7/31
 **/
public interface QuickCovert {

    /**
     * 和 常规的adapter中的onBindViewHolder，基本相同的用法
     *
     * @param binding  databinding
     * @param itemData item数据
     */
    void onCovert(ViewDataBinding binding, Object itemData, int position);
}
