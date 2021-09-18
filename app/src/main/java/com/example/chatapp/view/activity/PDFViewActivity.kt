package com.example.chatapp.view.activity

import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.helper.getFilePath
import com.example.chatapp.helper.transparentStatusBar
import com.example.chatapp.model.db.prefs.GlobalPref
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import kotlinx.android.synthetic.main.activity_pdfview.*
import org.jetbrains.anko.info
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection





class PDFViewActivity : AppCompatActivity() {
    private val onErrorListener: OnErrorListener = OnErrorListener {
        Log.e("errorListener", it.message.toString())
        Toast.makeText(this, it.message,Toast.LENGTH_SHORT).show()
    }
    private var pdfUrl: String? = null
    private var fileName: String? = "dgdgdfg"
    private var pageNo = 0
    private lateinit var globalPref: GlobalPref
    private lateinit var fetch: Fetch
    private val requestList: MutableList<Request> = ArrayList()

    private val onPageChangeListener: OnPageChangeListener =
        OnPageChangeListener { page, pageCount ->
            pageNo = page
            Log.i ("pageNo","$page of $pageCount" )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setContentView(R.layout.activity_pdfview)

        fileName = intent.getStringExtra("pdfName")
        pdfUrl = intent.getStringExtra("pdfUrl")
        Log.i("pdfArgs","arges => ${fileName+"...."+pdfUrl}")

        globalPref = GlobalPref(this)

        val fetchConfiguration = FetchConfiguration.Builder(this)
            .setDownloadConcurrentLimit(1)
            .setNotificationManager(object : DefaultFetchNotificationManager(this) {
                override fun getFetchInstanceForNamespace(namespace: String): Fetch {
                    return fetch
                }

            })
            .build()

        fetch = Fetch.getInstance(fetchConfiguration)

        fetch.addListener(fetchListener)

        if(fileName!=null){
            Log.d("pdfUrl","PDF url -> $pdfUrl")

            val file = getFilePath("pdf", fileName!!)
            if(file.exists()){
                viewPdfFromFile(fileName!!)
            }else{
                downloadNow(pdfUrl!!, file.absolutePath)
            }
            //viewPdfFromUrl()
        }
    }

    private fun downloadNow(url: String, filePath: String) {
        //val tokenValue = getToken(this)
        val request = Request(url, filePath)
        request.apply {
            priority = Priority.HIGH
            networkType = NetworkType.ALL
            //addHeader("Authorization", tokenValue)
        }

        requestList.add(request)

        fetch.enqueue(request, { result ->
            Log.i ( "result","result ->" + result.id )
        }, { error ->
            Log.i ( "error","error ->" + error.httpResponse?.code.toString() )
        })

    }


    private fun viewPdfFromFile(fileName: String) {
        val file = getFilePath("pdf", fileName)

        pageNo = globalPref.getIntData(fileName)

        pdfView.fromFile(file)
            .defaultPage(pageNo) // all pages are displayed by default
            .enableSwipe(true) // allows to block changing pages using swipe
            .swipeHorizontal(true)
            .enableDoubletap(true)
            // allows to draw something on the current page, usually visible in the middle of the screen
            //            .onDraw(onDrawListener)
            //            // allows to draw something on all pages, separately for every page. Called only for visible pages
            //            .onDrawAll(onDrawListener)
            //            .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
            .onPageChange(onPageChangeListener)
            //            .onPageScroll(onPageScrollListener)
            .onError(onErrorListener)
            //            .onPageError(onPageErrorListener)
            //            .onRender(onRenderListener) // called after document is rendered for the first time
            //            // called on single tap, return true if handled, false to toggle scroll handle visibility
            //            .onTap(onTapListener)
            //                .onLongPress(onLongPressListener)
            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
            .password(null)
            .scrollHandle(DefaultScrollHandle(this))
            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
            // spacing between pages in dp. To define spacing color, set view background
            .spacing(0)
            .autoSpacing(true) // add dynamic spacing to fit each page on its own on the screen
            .linkHandler(DefaultLinkHandler(pdfView))
            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
            .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
            .pageSnap(true) // snap pages to screen boundaries
            .pageFling(true) // make a fling change only a single page like ViewPager
            .nightMode(false) // toggle night mode
            .load()
    }


    override fun onPause() {
        super.onPause()

        fileName?.let { globalPref.saveData(it, pageNo) }
    }


    private val fetchListener = object : FetchListener {
        override fun onAdded(download: Download) {
            Log.i ("download","Added ${download.file}")
        }

        override fun onCancelled(download: Download) {
            fetch.delete(download.id)
        }

        override fun onCompleted(download: Download) {


            requestList.remove(download.request)

            val filePathList = download.file.split("/")
            val filePath = filePathList.last()
            viewPdfFromFile(filePath)

        }

        override fun onDeleted(download: Download) {

        }

        override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {

        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            Log.i  ("Error","Error -> ${error.throwable?.message}" )
        }

        override fun onPaused(download: Download) {

        }

        override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
            Log.i  ("ETA","ETA -> $etaInMilliSeconds" )

        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

        }

        override fun onRemoved(download: Download) {

        }

        override fun onResumed(download: Download) {

        }

        override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {

        }

        override fun onWaitingNetwork(download: Download) {

        }

    }

}