package com.zpw.net.core

class ResponseMutableLiveData<T> : ResponseLiveData<T> {

    /**
     * Creates a MutableLiveData initialized with the given `value`.
     *
     *  @param  value initial value
     */
    constructor(value: BaseResponse<T>?) : super(value)

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    constructor() : super()

    public override fun postValue(value: BaseResponse<T>?) {
        super.postValue(value)
    }

    public override fun setValue(value: BaseResponse<T>?) {
        super.setValue(value)
    }
}