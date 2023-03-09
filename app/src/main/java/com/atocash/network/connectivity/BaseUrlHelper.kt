package com.atocash.network.connectivity

import com.atocash.BuildConfig
import com.atocash.utils.extensions.printLog

/**
 * Created by Thulasi Rajan P on 10/11/22.
 */
class BaseUrlHelper {

    companion object {
        val urlMap: Map<String, Array<String>> = getUrls()
    }
}

fun getUrls(): Map<String, Array<String>> {
    return if (BuildConfig.DEBUG) {
        mapOf(
            "gmail.atocash.tk" to arrayOf(
                "atocash.com",
            ),
            "fwserver.atocash.tk" to arrayOf(
                "gmail.com", "gmail.tk",
                "foodunitco.com", "foodunitco.tk",
                "2eat.com.sa", "2eat.com.tk",
                "2eat.sa", "2eat.tk",
                "alzadalyawmi.com", "alzadalyawmi.tk",
                "dhyoof.com", "dhyoof.tk",
                "estilo.sa", "estilo.tk",
                "foodunit.uk", "foodunit.tk",
                "foodunitco.onmicrosoft.com", "foodunitco.onmicrosoft.tk",
                "janburger.com", "janburger.tk",
                "luluatnajd.com", "luluatnajd.tk",
                "pizzaratti.com", "pizzaratti.tk",
                "shawarma-plus.com", "shawarma-plus.tk",
                "shawarmaplus.sa", "shawarmaplus.tk",
                "signburger.com", "signburger.tk",
                "signsa.com", "signsa.tk",
                "tameesa.com", "tameesa.tk",
                "tameesa.com.sa", "tameesa.com.tk",
                "thouq.sa", "thouq.tk",
            ),
        )
    } else {
        mapOf(
            "gmailserver.atocash.com" to arrayOf(
                // "atocash.com",
                "gmail.com",
            ),
            "fwserver.atocash.com" to arrayOf(
                "foodunitco.com",
                "2eat.com.sa",
                "2eat.sa",
                "alzadalyawmi.com",
                "dhyoof.com",
                "estilo.sa",
                "foodunit.uk",
                "foodunitco.onmicrosoft.com",
                "janburger.com",
                "luluatnajd.com",
                "pizzaratti.com",
                "shawarma-plus.com",
                "shawarmaplus.sa",
                "signburger.com",
                "signsa.com",
                "tameesa.com",
                "tameesa.com.sa",
                "thouq.sa"
            ),
        )
    }
}

fun String.getBaseUrl(): String {
    val items = BaseUrlHelper.urlMap
    var baseUrl = "server.atocash.com"
    var isUrlFormed = false
    run breaking@{
        items.forEach { (url, urlList) ->
            if (urlList.contains(this)) {
                baseUrl = url
                isUrlFormed = true;
                return@breaking
            }
        }
    }
    //if the URL list does not contain the domain name
    //then append the domain name with server.atocash.com
    //and make it as the base URL
    if (!isUrlFormed) {
        var url = this
        if(url.contains(".com")) {
            url = url.replace(".com", "")
            printLog("URL reformed as $url")
        }
        baseUrl = "${url}server.atocash.com"
        printLog("base URL reformed as $baseUrl")
    }
    printLog("base URL formed as $baseUrl")
    return baseUrl
}
