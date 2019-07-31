package com.xiaobin.quickbindadapter;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 小斌
 * @data 2019/6/24
 **/
public class BindHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public BindHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public BindHolder(View view) {
        super(view);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
