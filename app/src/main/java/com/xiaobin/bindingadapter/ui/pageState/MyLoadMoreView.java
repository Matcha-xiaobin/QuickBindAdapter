package com.xiaobin.bindingadapter.ui.pageState;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaobin.bindingadapter.R;
import com.xiaobin.bindingadapter.databinding.LayoutMyLoadingMoreBinding;
import com.xiaobin.quickbindadapter.BaseLoadView;

/**
 * 自定义加载更多item
 */
public class MyLoadMoreView extends BaseLoadView<LayoutMyLoadingMoreBinding> {

    public MyLoadMoreView() {
        super(R.layout.layout_my_loading_more);
    }

    @Override
    protected void initView(@Nullable LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.GONE);
        loadView.progress.setVisibility(View.VISIBLE);
        loadView.tvLabel.setText("正在努力加载中。。。");
    }

    protected void onLoading(LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.GONE);
        loadView.progress.setVisibility(View.VISIBLE);
        loadView.tvLabel.setText("正在努力加载中。。。");
    }

    protected void onNoMoreData(LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.VISIBLE);
        loadView.ivImage.setImageResource(R.drawable.ic_svg_ok);
        loadView.progress.setVisibility(View.GONE);
        loadView.tvLabel.setText("全都加载完了");
    }

    protected void onLoadSuccess(LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.VISIBLE);
        loadView.ivImage.setImageResource(R.drawable.ic_svg_ok);
        loadView.progress.setVisibility(View.GONE);
        loadView.tvLabel.setText("加载成功");
    }

    protected void onLoadFailed(LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.VISIBLE);
        loadView.ivImage.setImageResource(R.drawable.error_image);
        loadView.progress.setVisibility(View.GONE);
        loadView.tvLabel.setText("加载失败");
    }

    protected void onWaitLoading(LayoutMyLoadingMoreBinding loadView) {
        if (loadView == null) return;
        loadView.ivImage.setVisibility(View.VISIBLE);
        loadView.ivImage.setImageResource(R.drawable.error_image);
        loadView.progress.setVisibility(View.GONE);
        loadView.tvLabel.setText("点击加载更多");
    }

    @Override
    protected void onStateChange(LayoutMyLoadingMoreBinding loadView, @NonNull LoadMoreState state) {
        switch (state) {
            case LOADING:
                onLoading(loadView);
                break;
            case SUCCESS:
                onLoadSuccess(loadView);
                break;
            case FAILED:
                onLoadFailed(loadView);
                break;
            case NO_MORE:
                onNoMoreData(loadView);
                break;
            case WAIT_LOADING:
                onWaitLoading(loadView);
                break;
        }
    }
}
