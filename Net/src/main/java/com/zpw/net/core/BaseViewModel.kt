package com.zpw.net.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T : BaseRepository> : ViewModel() {
    val repository: T by lazy {
        createRepository()
    }

    val loadingDataState: LiveData<LoadingState> by lazy {
        repository.loadingStateLiveData
    }

    /**
     * 创建Repository
     */
    @Suppress("UNCHECKED_CAST")
    open fun createRepository(): T {
        val baseRepository = findActualGenericsClass<T>(BaseRepository::class.java)
            ?: throw NullPointerException("Can not find a BaseRepository Generics in ${javaClass.simpleName}")
        return baseRepository.newInstance()
    }
}