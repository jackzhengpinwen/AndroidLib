package com.zpw.net.core.interceptor

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets


abstract class ResponseBodyInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val url: String = request.url.toString()
        val response: Response = chain.proceed(request)
        val responseBody: ResponseBody? = response.body
        if (responseBody != null) {
            val contentLength = responseBody.contentLength()
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            var buffer: Buffer = source.buffer
            val charset = StandardCharsets.UTF_8
            if (charset != null && contentLength != 0L) {
                return intercept(response, url, buffer.clone().readString(charset))
            }
        }
        return response
    }

    @Throws(IOException::class)
    abstract fun intercept(response: Response, url: String?, body: String?): Response
}