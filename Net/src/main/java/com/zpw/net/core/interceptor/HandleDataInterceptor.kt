package com.zpw.net.core.interceptor

import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class HandleDataInterceptor : ResponseBodyInterceptor() {

    override fun intercept(response: Response, url: String?, body: String?): Response {
        var jsonObject: JSONObject?
        var returnJsonObject = JSONObject()
        try {
            jsonObject = JSONObject(body)
            if (!jsonObject.has("error_code") && !jsonObject.has("error_msg")) {
                val json = jsonObject.toString();
                returnJsonObject.put("data", json)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val contentType = response.body?.contentType()
        val responseBody = returnJsonObject.toString().replace("\\", "").replace("\"{", "{").replace("}\"","}").toResponseBody(contentType)
        return response.newBuilder().body(responseBody).build() // 重新生成响应对象
    }
}
