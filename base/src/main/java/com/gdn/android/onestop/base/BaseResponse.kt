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

    fun isOk(): Boolean {
        return status != null && status.equals(OK_STATUS)
    }

    companion object {

        val OK_STATUS = "OK"
        val NOT_OK_STATUS = "NOT_OK"

        fun <T> success(data: T): BaseResponse<T> {
            return BaseResponse<T>().apply {
                this.data = data
                this.code = 200
                this.status = "OK"
            }
        }
    }
}
