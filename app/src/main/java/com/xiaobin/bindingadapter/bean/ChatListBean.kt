package com.xiaobin.bindingadapter.bean;

import com.xiaobin.bindingadapter.R;

/**
 * @author 小斌
 * @data 2019/7/30
 **/
public class ChatListBean {

    private String id;

    public int getDrawableId() {
        return R.mipmap.ic_launcher;
    }

    public String getName() {
        return "这个用户没有名字。ID：" + id;
    }

    public String getTime() {
        return "16:16";
    }

    public String getMessage() {
        return "这个人想和你聊天。";
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id == null ? "" : id;
    }
}
