package com.xiaobin.bindingadapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author 小斌
 * @data 2019/7/30
 **/
public class BindAdapterUtils {

    @BindingAdapter("circleImage")
    public static void circleImage(View view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .apply(RequestOptions.circleCropTransform()
                        .error(new ColorDrawable(Color.WHITE))
                        .fallback(new ColorDrawable(Color.RED)))
                .into((ImageView) view);
    }

    @BindingAdapter("circleImage")
    public static void circleImage(View view, @DrawableRes int ids) {
        Glide.with(view.getContext())
                .load(ContextCompat.getDrawable(view.getContext(), ids))
                .apply(RequestOptions.circleCropTransform()
                        .error(new ColorDrawable(Color.WHITE))
                        .fallback(new ColorDrawable(Color.RED)))
                .into((ImageView) view);
    }
}
