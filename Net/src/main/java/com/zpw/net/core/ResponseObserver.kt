package com.zpw.net.core

import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils

abstract class ResponseObserver<T> : Observer<BaseResponse<T>> {

    final override fun onChanged(response: BaseResponse<T>?) {
        response?.let {
            when (response.dataState) {
                DataState.STATE_SUCCESS, DataState.STATE_EMPTY -> {
                    onSuccess(response.data)
                }
                DataState.STATE_FAILED -> {
                    onFailure(response.error_msg, response.error_code)
                }
                DataState.STATE_ERROR -> {
                    onException(response.exception)
                }
                else -> {
                }
            }
        }
    }

    private fun onException(exception: Throwable?) {
        ToastUtils.showLong(exception.toString())
    }

    /**
     * 请求成功
     *  @param  data 请求数据
     */
    abstract fun onSuccess(data: T?)

    /**
     * 请求失败
     *  @param  errorCode 错误码
     *  @param  errorMsg 错误信息
     */
    open fun onFailure(errorMsg: String?, errorCode: String?) {
        ToastUtils.showLong("Login Failed,errorCode:$errorCode,errorMsg:$errorMsg")
    }
}