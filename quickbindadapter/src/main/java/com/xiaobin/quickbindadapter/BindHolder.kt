package com.xiaobin.quickbindadapter;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 小斌
 * @data 2019/6/24
 **/
public class BindHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding binding;

    public BindHolder(ViewDataBinding binding, LifecycleOwner lifecycleOwner) {
        super(binding.getRoot());
        this.binding = binding;
        if (lifecycleOwner != null) {
            binding.setLifecycleOwner(lifecycleOwner);
        }
    }

    public BindHolder(View view) {
        super(view);
    }

    public ViewDataBinding getBinding() {
        return binding;
    }
}
