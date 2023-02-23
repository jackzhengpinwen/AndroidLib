package com.zpw.net.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.lang.reflect.ParameterizedType

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

/**
 * 查找 Any 类上泛型为[cls]类型的class，如果不存在则返回null
 * @param cls 要查找的泛型的类型
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> Any.findActualGenericsClass(cls: Class<*>): Class<T>? {
    val genericSuperclass = javaClass.genericSuperclass
    if (genericSuperclass !is ParameterizedType) {
        return null
    }
    // 获取类的所有泛型参数数组
    val actualTypeArguments = genericSuperclass.actualTypeArguments
    // 遍历泛型数组
    actualTypeArguments.forEach {
        if (it is Class<*> && cls.isAssignableFrom(it)) {
            return it as Class<T>
        } else if (it is ParameterizedType) {
            val rawType = it.rawType
            if (rawType is Class<*> && cls.isAssignableFrom(rawType)) {
                return rawType as Class<T>
            }
        }
    }
    return null
}