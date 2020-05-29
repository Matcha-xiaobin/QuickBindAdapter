package com.xiaobin.quickbindadapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorInt;

import com.xiaobin.quickbindadapter.databinding.ItemLoadmoreBinding;

public class DefaultLoadView extends BaseLoadView<ItemLoadmoreBinding> {

    private String loadMoreText = "努力加载中...";
    private String loadFailText = "加载失败了!";
    private String loadCompleteText = "没有更多数据";
    private String loadSuccessText = "加载成功";
    private @ColorInt
    int textColor = Color.LTGRAY;

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
    }

    public void setLoadMoreText(String loadMoreText) {
        this.loadMoreText = loadMoreText == null ? "" : loadMoreText;
    }

    public void setLoadFailText(String loadFailText) {
        this.loadFailText = loadFailText == null ? "" : loadFailText;
    }

    public void setLoadCompleteText(String loadCompleteText) {
        this.loadCompleteText = loadCompleteText == null ? "" : loadCompleteText;
    }

    public void setLoadSuccessText(String loadSuccessText) {
        this.loadSuccessText = loadSuccessText == null ? "" : loadSuccessText;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_loadmore;
    }

    @Override
    protected void initView(ItemLoadmoreBinding loadView) {
    }

    @Override
    protected void onLoadMore(ItemLoadmoreBinding loadView) {
        loadView.setLoading(true);
        loadView.setText(loadMoreText);
        loadView.tvText.setTextColor(textColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.setProgressTintList(ColorStateList.valueOf(textColor));
        }
    }

    @Override
    protected void onLoadEnd(ItemLoadmoreBinding loadView) {
        loadView.setLoading(false);
        loadView.setText(loadCompleteText);
        loadView.tvText.setTextColor(textColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.setProgressTintList(ColorStateList.valueOf(textColor));
        }
    }

    @Override
    protected void onLoadSuccess(ItemLoadmoreBinding loadView) {
        loadView.setLoading(false);
        loadView.setText(loadSuccessText);
        loadView.tvText.setTextColor(textColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.setProgressTintList(ColorStateList.valueOf(textColor));
        }
    }

    @Override
    protected void onLoadFail(ItemLoadmoreBinding loadView) {
        loadView.setLoading(false);
        loadView.setText(loadFailText);
        loadView.tvText.setTextColor(textColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadView.progress.setProgressTintList(ColorStateList.valueOf(textColor));
        }
    }

}
