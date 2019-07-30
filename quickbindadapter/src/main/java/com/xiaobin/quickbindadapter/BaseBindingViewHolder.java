package com.xiaobin.quickbindadapter;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 小斌
 * @data 2019/6/24
 **/
public class BaseBindingViewHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public BaseBindingViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public BaseBindingViewHolder(View view) {
        super(view);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
