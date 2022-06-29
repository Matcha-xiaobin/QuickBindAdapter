package com.xiaobin.quickbindadapter

import java.util.ArrayList

/**
 * @author 小斌
 * @data 2019/7/10
 */
class ItemData : ArrayList<Any?> {
    constructor() {}
    constructor(initialCapacity: Int) : super(initialCapacity) {}
    constructor(c: Collection<*>) : super(c) {}
}