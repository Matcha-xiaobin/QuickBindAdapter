package com.xiaobin.bindingadapter.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.xiaobin.bindingadapter.R;

/**
 * @author 小斌
 * @data 2019/7/31
 **/
public abstract class BaseActivity<VD extends ViewDataBinding> extends AppCompatActivity {

    protected VD binding;

    protected boolean showBackButton() {
        return false;
    }

    protected abstract int getLayoutId();

    protected String getActionTitle() {
        return getString(R.string.app_name);
    }

    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), getLayoutId(), null, false);
        setContentView(binding.getRoot());

        initView(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getActionTitle());
            if (showBackButton()) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
