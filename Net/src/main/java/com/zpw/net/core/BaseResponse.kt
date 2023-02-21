package com.zpw.net.core

class BaseResponse<T>(
    var error_code: String? = null,
    var error_msg: String? = null,
    var data: T? = null,
    var dataState: DataState? = null,
    var exception: Throwable? = null,
) {
    val success: Boolean
        get() = error_code == null
}

class LoadingState(
    var msg: String? = null,
    var dataState: DataState? = null)

/**
 * 网络请求状态
 */
enum class DataState {
    STATE_LOADING, // 开始请求
    STATE_SUCCESS, // 服务器请求成功
    STATE_EMPTY, // 服务器返回数据为null
    STATE_FAILED, // 接口请求成功但是服务器返回error
    STATE_ERROR, // 请求失败
    STATE_FINISH, // 请求结束
}