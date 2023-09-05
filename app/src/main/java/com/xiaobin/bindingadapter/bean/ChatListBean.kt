package com.xiaobin.bindingadapter.bean

import com.xiaobin.bindingadapter.R
import com.xiaobin.quickbindadapter.interfaces.StaggeredFullSpan

/**
 * @author 小斌
 * @data 2019/7/30
 */
class ChatListBean {
    private var id: String? = null
    val drawableId: Int
        get() = R.mipmap.ic_launcher
    val name: String
        get() = "这个用户没有名字。ID：$id"
    val time: String
        get() = "16:16"
    val message: String
        get() = "这个人想和你聊天。"

    fun getId(): String {
        return if (id == null) "" else id!!
    }

    fun setId(id: String?) {
        this.id = id ?: ""
    }
}

class ChatListBean2 {
    private var id: String? = null
    val drawableId: Int
        get() = R.mipmap.ic_launcher
    val name: String
        get() = "这个用户没有名字。ID：$id"
    val time: String
        get() = "16:16"
    val message: String
        get() = "这个人想和你聊天。"

    fun getId(): String {
        return if (id == null) "" else id!!
    }

    fun setId(id: String?) {
        this.id = id ?: ""
    }
}

data class StaggeredHeadBean(
    val title: String
) : StaggeredFullSpan