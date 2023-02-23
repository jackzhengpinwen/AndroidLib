package com.zpw.net.core.interceptor

import com.zpw.net.core.RetrofitManager
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class HandleDataInterceptor : ResponseBodyInterceptor() {

    override fun intercept(response: Response, url: String?, body: String?): Response {
        url?.let {
            if (it.startsWith(RetrofitManager.BASE_URL)) {
                val jsonObject = JSONObject("{}")
                jsonObject.put("data", body)

                val contentType = response.body?.contentType()
                val returnResponse = jsonObject.toString()
                    .replace("\"[", "[").replace("]\"", "]")
                    .replace("\\", "")
                    .replace("\"{", "{").replace("}\"", "}")
                val responseBody = returnResponse.toResponseBody(contentType)
                return response.newBuilder().body(responseBody).build() // 重新生成响应对象
            }
        }
        return response
    }
}
