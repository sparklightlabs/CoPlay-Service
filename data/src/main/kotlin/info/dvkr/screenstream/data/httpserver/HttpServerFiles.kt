package info.dvkr.screenstream.data.httpserver

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.elvishew.xlog.XLog
import info.dvkr.screenstream.data.R
import info.dvkr.screenstream.data.other.getLog
import info.dvkr.screenstream.data.other.randomString
import info.dvkr.screenstream.data.settings.SettingsReadOnly
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class HttpServerFiles(context: Context, private val settingsReadOnly: SettingsReadOnly) {
    companion object {
        private const val FAVICON_PNG = "favicon.png"
        private const val LOGO_PNG = "logo.png"
        private const val FULLSCREEN_ON_PNG = "fullscreen-on.png"
        private const val FULLSCREEN_OFF_PNG = "fullscreen-off.png"
        private const val START_STOP_PNG = "start-stop.png"

        private const val CSS_STYLESHEET = "styles.css"

        private const val INDEX_HTML = "index.html"
        private const val INDEX_HTML_BACKGROUND_COLOR = "BACKGROUND_COLOR"
        private const val INDEX_HTML_SCREEN_STREAM_ADDRESS = "SCREEN_STREAM_ADDRESS"
        private const val INDEX_HTML_START_STOP_ADDRESS = "START_STOP_ADDRESS"
        private const val INDEX_HTML_APPLICATION_LIST = "APPLICATION_LIST"
        private const val INDEX_HTML_ENABLE_BUTTONS = "ENABLE_BUTTONS"
        private const val INDEX_HTML_CSS_STYLE= "CSS_STYLE"

        private const val PINREQUEST_HTML = "pinrequest.html"
        private const val PINREQUEST_HTML_STREAM_REQUIRE_PIN = "STREAM_REQUIRE_PIN"
        private const val PINREQUEST_HTML_ENTER_PIN = "ENTER_PIN"
        private const val PINREQUEST_HTML_FOUR_DIGITS = "FOUR_DIGITS"
        private const val PINREQUEST_HTML_SUBMIT_TEXT = "SUBMIT_TEXT"
        private const val PINREQUEST_HTML_WRONG_PIN_MESSAGE = "WRONG_PIN_MESSAGE"

        const val DEFAULT_HTML_ADDRESS = "/"
        private const val DEFAULT_STREAM_ADDRESS = "/stream.mjpeg"
        private const val DEFAULT_START_STOP_ADDRESS = "/start-stop"
        const val DEFAULT_PIN_ADDRESS = "/?pin="

        const val ICON_PNG_ADDRESS = "/favicon.ico"
        const val LOGO_PNG_ADDRESS = "/logo.png"
        const val FULLSCREEN_ON_PNG_ADDRESS = "/fullscreen-on.png"
        const val FULLSCREEN_OFF_PNG_ADDRESS = "/fullscreen-off.png"
        const val START_STOP_PNG_ADDRESS = "/start-stop.png"

        const val LAUNCH_APP_ADDRESS = "/launch-app"
        const val APP_ICON_ADDRESS = "/app-icon"
    }

    private val applicationContext: Context = context.applicationContext

    val faviconPng = getFileFromAssets(applicationContext, FAVICON_PNG)
    val logoPng = getFileFromAssets(applicationContext, LOGO_PNG)
    val applicationIconMap : MutableMap<String, ByteArray> = mutableMapOf();
    val fullScreenOnPng = getFileFromAssets(applicationContext, FULLSCREEN_ON_PNG)
    val fullScreenOffPng = getFileFromAssets(applicationContext, FULLSCREEN_OFF_PNG)
    val startStopPng = getFileFromAssets(applicationContext, START_STOP_PNG)

    private val baseIndexHtml =
        String(getFileFromAssets(applicationContext, INDEX_HTML), StandardCharsets.UTF_8)

    private val baseStyleSheet =
            String(getFileFromAssets(applicationContext, CSS_STYLESHEET), StandardCharsets.UTF_8)

    private val basePinRequestHtml =
        String(getFileFromAssets(applicationContext, PINREQUEST_HTML), StandardCharsets.UTF_8)
            .replaceFirst(
                PINREQUEST_HTML_STREAM_REQUIRE_PIN.toRegex(),
                applicationContext.getString(R.string.html_stream_require_pin)
            )
            .replaceFirst(
                PINREQUEST_HTML_ENTER_PIN.toRegex(),
                applicationContext.getString(R.string.html_enter_pin)
            )
            .replaceFirst(
                PINREQUEST_HTML_FOUR_DIGITS.toRegex(),
                applicationContext.getString(R.string.html_four_digits)
            )
            .replaceFirst(
                PINREQUEST_HTML_SUBMIT_TEXT.toRegex(),
                applicationContext.getString(R.string.html_submit_text)
            )
    private var htmlEnableButtons = settingsReadOnly.htmlEnableButtons
    private var htmlBackColor = settingsReadOnly.htmlBackColor
    private var enablePin = settingsReadOnly.enablePin
    private var pin = settingsReadOnly.pin

    fun prepareForConfigure(): Pair<Boolean, Boolean> {
        htmlEnableButtons = settingsReadOnly.htmlEnableButtons
        htmlBackColor = settingsReadOnly.htmlBackColor
        enablePin = settingsReadOnly.enablePin
        pin = settingsReadOnly.pin

        return Pair(htmlEnableButtons, enablePin)
    }

    fun buildApplicationList(): String {
        var applicationList: String = ""
        this.applicationContext.packageManager.getInstalledApplications(0).forEach {
            //var icon = this.applicationContext.packageManager.getApplicationIcon(it.packageName).
            applicationIconMap.put(it.packageName, getAppIconFromDrawable(it.packageName))
            if ((it.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM)
                applicationList +=
                    "<li class=\"item\">" +
                            "<a href = /launch-app?${it.packageName}>" +
                            "<img class=\"imageItem\" src = /app-icon?${it.packageName}>" +
                            "${this.applicationContext.packageManager.getApplicationLabel(it).toString()}" +
                            "</img>" +
                            "</a>" +
                    "</li>"
        }
        return applicationList
    }

    // Gets app Icon and returns it in PNG ByteArray
    private fun getAppIconFromDrawable(packageName: String): ByteArray {
        var drawable : Drawable = this.applicationContext.packageManager.getApplicationIcon(packageName)
        var bitmap : Bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        var canvas : Canvas = Canvas(bitmap);
        drawable.setBounds(0,0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        var os : ByteArrayOutputStream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, os)
        return os.toByteArray()
    }

    // Returns an Empty array if not found
    fun getAppIconPng(packageName: String): ByteArray {
        return if (applicationIconMap.get(packageName) != null)
            applicationIconMap.get(packageName) as ByteArray
        else
            ByteArray(0)
    }

    fun configureStreamAddress(): String =
        if (enablePin) "/" + randomString(16) + ".mjpeg"
        else HttpServerFiles.DEFAULT_STREAM_ADDRESS

    fun configureStartStopAddress(): String =
        if (enablePin) "/" + randomString(16)
        else HttpServerFiles.DEFAULT_START_STOP_ADDRESS

    fun configureIndexHtml(streamAddress: String, startStopAddress: String, appList: String): String {
        var newStyleSheet =
            baseStyleSheet.replaceFirst(
                INDEX_HTML_BACKGROUND_COLOR.toRegex(),
                "#%06X".format(0xFFFFFF and htmlBackColor)
        )
        return baseIndexHtml
                .replaceFirst(
                        INDEX_HTML_ENABLE_BUTTONS.toRegex(),
                        htmlEnableButtons.toString()
                )
                .replaceFirst(INDEX_HTML_CSS_STYLE.toRegex(), newStyleSheet)
                .replaceFirst(INDEX_HTML_SCREEN_STREAM_ADDRESS.toRegex(), streamAddress)
                .replaceFirst(INDEX_HTML_START_STOP_ADDRESS.toRegex(), startStopAddress)
                .replaceFirst(INDEX_HTML_APPLICATION_LIST.toRegex(), appList)
    }

    fun configurePinAddress(): String =
        if (enablePin) DEFAULT_PIN_ADDRESS + pin else DEFAULT_PIN_ADDRESS

    fun configurePinRequestHtml(): String =
        if (enablePin)
            basePinRequestHtml.replaceFirst(PINREQUEST_HTML_WRONG_PIN_MESSAGE.toRegex(), "&nbsp")
        else
            ""

    fun configurePinRequestErrorHtml(): String =
        if (enablePin)
            basePinRequestHtml
                .replaceFirst(
                    PINREQUEST_HTML_WRONG_PIN_MESSAGE.toRegex(),
                    applicationContext.getString(R.string.html_wrong_pin)
                )
        else
            ""

    private fun getFileFromAssets(context: Context, fileName: String): ByteArray {
        XLog.d(getLog("getFileFromAssets", fileName))
        context.assets.open(fileName).use { inputStream ->
            val fileBytes = ByteArray(inputStream.available())
            inputStream.read(fileBytes)
            fileBytes.isNotEmpty() || throw IllegalStateException("$fileName is empty")
            return fileBytes
        }
    }
}