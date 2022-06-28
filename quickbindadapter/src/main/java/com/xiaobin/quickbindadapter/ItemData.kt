package com.xiaobin.quickbindadapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author 小斌
 * @data 2019/7/10
 **/
public class ItemData extends ArrayList<Object> {
    public ItemData() {
    }

    public ItemData(int initialCapacity) {
        super(initialCapacity);
    }

    public ItemData(@NonNull Collection<?> c) {
        super(c);
    }
}
