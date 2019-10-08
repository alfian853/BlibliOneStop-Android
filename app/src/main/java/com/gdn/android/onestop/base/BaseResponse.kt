package com.gdn.android.onestop.base

class BaseResponse<T> {
    internal var code: Int? = null
    internal var status: String? = null
    internal var data: T? = null

    constructor() {}

    constructor(code: Int?, status: String, data: T) {
        this.code = code
        this.status = status
        this.data = data
    }
}
