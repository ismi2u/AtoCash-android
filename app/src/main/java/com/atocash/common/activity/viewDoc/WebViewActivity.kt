package com.atocash.common.activity.viewDoc

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.atocash.R
import com.atocash.utils.AppHelper
import com.atocash.utils.Keys
import com.atocash.utils.extensions.getExtension
import com.atocash.utils.extensions.printLog
import com.atocash.utils.extensions.showFailureToast
import com.atocash.utils.extensions.showShortToast

class WebViewActivity : AppCompatActivity() {

    private var isImage = false

    private lateinit var webView: WebView
    private lateinit var progressBar: ContentLoadingProgressBar

    private var fileUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        findViewById<ImageView>(R.id.web_back_iv).setOnClickListener {
            onBackPressed()
        }

        val imageView = findViewById<ImageView>(R.id.preview_iv)

        webView = findViewById(R.id.web_view)
        progressBar = findViewById(R.id.progress_bar)

        val bundle = intent.extras
        bundle?.let {
            val url = it.getString(Keys.IntentData.URL)

            printLog("Extension: ${url?.getExtension()}")

            when (url?.getExtension()) {
                ".jpg", ".jpeg", ".png" -> {
                    isImage = true
                    AppHelper.loadImageWithGlide(this, imageView, url)
                }
                ".doc", ".docx", ".pdf", ".xpdf" -> {
                    isImage = false
                    fileUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=$url"
                    loadWebView()
                }
                else -> {
                    errorLoading()
                }
            }
        }

        if(isImage) {
            webView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            findViewById<TextView>(R.id.web_tool_tv).text = getString(R.string.title_documents)
        } else {
            imageView.visibility = View.GONE
            webView.visibility = View.VISIBLE
        }
    }

    private fun errorLoading() {
        showFailureToast(getString(R.string.file_format_not_supported))
        finish()
    }

    private fun loadWebView() {
        if (fileUrl.isEmpty()) {
            errorLoading()
            return
        }
        webView.clearHistory()
        webView.clearCache(true)
        webView.resumeTimers()
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.webViewClient = CustomWebClient()
        webView.loadUrl(fileUrl)
    }

    private var isPageStarted = false

    inner class CustomWebClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            onPageLoading()
            isPageStarted = true
            printLog("CustomWebClient onPageStarted")
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            printLog("CustomWebClient shouldOverrideUrlLoading")
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            //onPageLoading()
            //binding.webView.loadUrl(url)
            printLog("CustomWebClient deprecated shouldOverrideUrlLoading")
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            printLog("CustomWebClient onPageFinished")
            if (isPageStarted) {
                onPageLoaded()
                isPageStarted = false
            } else {
                loadWebView()
            }
        }
    }

    private fun onPageLoading() {
        progressBar.visibility = View.VISIBLE
        webView.visibility = View.GONE
        showShortToast("Loading file...")
    }

    private fun onPageLoaded() {
        progressBar.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }
}