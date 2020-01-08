package com.gdn.android.onestop.base

class BaseResponse<T> {
    var code: Int? = null
    var status: String? = null
    var data: T? = null

    constructor() {}

    constructor(code: Int?, status: String, data: T) {
        this.code = code
        this.status = status
        this.data = data
    }
}
