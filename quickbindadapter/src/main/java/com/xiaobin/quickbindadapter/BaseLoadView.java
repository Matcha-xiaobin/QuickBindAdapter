package com.xiaobin.quickbindadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseLoadView<T extends ViewDataBinding> {

    enum LoadMoreState {
        LOADING_MORE, LOAD_SUCCESS, LOAD_FAIL, LOAD_COMPLETE
    }

    //加载更多
    private LoadMoreState loadMoreState = LoadMoreState.LOAD_SUCCESS;

    private T loadView;
    private BindHolder bindHolder;

    public BindHolder get(ViewGroup parent) {
        createView(parent);
        if (bindHolder != null)
            return bindHolder;
        else
            return null;
    }

    public LoadMoreState getLoadMoreState() {
        return loadMoreState;
    }

    protected abstract int getLayoutId();

    public void createView(ViewGroup parent) {
        loadView = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                getLayoutId(),
                parent, false);
        bindHolder = new BindHolder(loadView.getRoot());
        initView(loadView);
    }

    public void isLoadMore() {
        if (loadMoreState == LoadMoreState.LOADING_MORE) return;
        loadMoreState = LoadMoreState.LOADING_MORE;
        onLoadMore(loadView);
    }

    public void isLoadMoreEnd() {
        if (loadMoreState == LoadMoreState.LOAD_COMPLETE) return;
        loadMoreState = LoadMoreState.LOAD_COMPLETE;
        onLoadEnd(loadView);
    }

    public void isLoadMoreSuccess() {
        if (loadMoreState == LoadMoreState.LOAD_SUCCESS) return;
        loadMoreState = LoadMoreState.LOAD_SUCCESS;
        onLoadSuccess(loadView);
    }

    public void isLoadMoreFail() {
        if (loadMoreState == LoadMoreState.LOAD_FAIL) return;
        loadMoreState = LoadMoreState.LOAD_FAIL;
        onLoadFail(loadView);
    }

    protected abstract void initView(T loadView);

    /**
     * 正在加载更多
     */
    protected abstract void onLoadMore(T loadView);

    /**
     * 加载完成，没有更多
     */
    protected abstract void onLoadEnd(T loadView);

    /**
     * 加载成功
     */
    protected abstract void onLoadSuccess(T loadView);

    /**
     * 加载失败
     */
    protected abstract void onLoadFail(T loadView);
}
