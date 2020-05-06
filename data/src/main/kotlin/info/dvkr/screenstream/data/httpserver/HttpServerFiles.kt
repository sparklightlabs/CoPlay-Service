package info.dvkr.screenstream.data.httpserver

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import com.elvishew.xlog.XLog
import info.dvkr.screenstream.data.R
import info.dvkr.screenstream.data.other.getLog
import info.dvkr.screenstream.data.other.AndroidManifestService;
import info.dvkr.screenstream.data.other.randomString
import info.dvkr.screenstream.data.settings.SettingsReadOnly
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

class HttpServerFiles(context: Context, private val settingsReadOnly: SettingsReadOnly) {
    companion object {
        private const val FAVICON_PNG = "favicon.png"
        private const val LOGO_PNG = "logo.png"
        private const val FULLSCREEN_ON_PNG = "fullscreen-on.png"
        private const val FULLSCREEN_OFF_PNG = "fullscreen-off.png"
        private const val START_STOP_PNG = "start-stop.png"

        private const val PAUSE_PNG = "pause.png"
        private const val PLAY_PNG = "play.png"
        private const val HOME_PNG = "home.png"
        private const val FULLSCREEN_PNG = "fullscreen.png"
        private const val MENU_PNG = "menu.png"

        private const val CSS_STYLESHEET = "styles.css"
        private const val JQUERY_JAVASCRIPT = "jquery-3.3.1.slim.min.js"
        private const val POPPER_JAVASCRIPT = "popper.min.js"
        private const val BOOTSTRAP_JAVASCRIPT = "bootstrap.min.js"
        private const val BOOTSTRAP_CSS = "bootstrap.min.css"

        private const val INDEX_HTML = "index.html"
        private const val DIRECTOR_HTML = "director.html"

        private const val INDEX_HTML_BACKGROUND_COLOR = "BACKGROUND_COLOR"
        private const val INDEX_HTML_SCREEN_STREAM_ADDRESS = "SCREEN_STREAM_ADDRESS"
        private const val INDEX_HTML_APP_CONTROL_UI_ADDRESS = "APP_CONTROL_UI"
        private const val INDEX_HTML_APPLICATION_LIST = "APPLICATION_LIST"
        private const val INDEX_HTML_ENABLE_BUTTONS = "ENABLE_BUTTONS"
        private const val INDEX_HTML_CSS_STYLE= "CSS_STYLE"
        private const val INDEX_HTML_ADDITIONAL_SETTINGS= "ADDITIONAL_SETTINGS"

        private const val PINREQUEST_HTML = "pinrequest.html"
        private const val PINREQUEST_HTML_STREAM_REQUIRE_PIN = "STREAM_REQUIRE_PIN"
        private const val PINREQUEST_HTML_ENTER_PIN = "ENTER_PIN"
        private const val PINREQUEST_HTML_FOUR_DIGITS = "FOUR_DIGITS"
        private const val PINREQUEST_HTML_SUBMIT_TEXT = "SUBMIT_TEXT"
        private const val PINREQUEST_HTML_WRONG_PIN_MESSAGE = "WRONG_PIN_MESSAGE"

        const val DEFAULT_HTML_ADDRESS = "/"
        private const val DEFAULT_STREAM_ADDRESS = "/stream.mjpeg"
        const val DEFAULT_SYSTEM_ACTION_ADDRESS = "/system-action"
        private const val DEFAULT_APP_ACTION_ADDRESS = "/app-action"
        const val DEFAULT_PIN_ADDRESS = "/?pin="

        const val ICON_PNG_ADDRESS = "/favicon.ico"
        const val LOGO_PNG_ADDRESS = "/logo.png"
        const val FULLSCREEN_ON_PNG_ADDRESS = "/fullscreen-on.png"
        const val FULLSCREEN_OFF_PNG_ADDRESS = "/fullscreen-off.png"
        const val START_STOP_PNG_ADDRESS = "/start-stop.png"

        const val APP_ICON_ADDRESS = "/app-icon"
        const val UI_ICON_ADDRESS = "/icons"
        const val JAVASCRIPT_ADDRESS = "/js"
        const val TOGGLE_STREAM_ADDRESS = "toggle-stream"
        const val CHANGE_IMAGE_SIZE_ADDRESS = "image-size"
        const val CHANGE_IMAGE_COMPRESSION_ADDRESS = "image-compression"
        const val GET_JSON_ADDRESS = "/get-json"
        const val GO_HOME_ADDRESS = "go-home"
        const val GO_BACK_ADDRESS = "go-back"
        const val LAUNCH_APP_ADDRESS = "launch-app"
        const val LAUNCH_ACTION_ADDRESS = "launch-action"
        const val CSS_ADDRESS = "/css"
    }

    private val applicationContext: Context = context.applicationContext

    val faviconPng = getFileFromAssets(applicationContext, FAVICON_PNG)
    val logoPng = getFileFromAssets(applicationContext, LOGO_PNG)
    val applicationIconMap : MutableMap<String, ByteArray> = mutableMapOf();
    val fullScreenOnPng = getFileFromAssets(applicationContext, FULLSCREEN_ON_PNG)
    val fullScreenOffPng = getFileFromAssets(applicationContext, FULLSCREEN_OFF_PNG)
    val pausePng = getFileFromAssets(applicationContext, PAUSE_PNG)
    val playPng = getFileFromAssets(applicationContext, PLAY_PNG)
    val menuPng = getFileFromAssets(applicationContext, MENU_PNG)
    val homePng = getFileFromAssets(applicationContext, HOME_PNG)
    val fullscreenPng = getFileFromAssets(applicationContext, FULLSCREEN_PNG)

    private val baseIndexHtml =
        String(getFileFromAssets(applicationContext, INDEX_HTML), StandardCharsets.UTF_8)

    private val baseDirectorHtml =
        String(getFileFromAssets(applicationContext, DIRECTOR_HTML), StandardCharsets.UTF_8)

    private val baseStyleSheet =
            String(getFileFromAssets(applicationContext, CSS_STYLESHEET), StandardCharsets.UTF_8)

    private val jquery=
            String(getFileFromAssets(applicationContext, JQUERY_JAVASCRIPT), StandardCharsets.UTF_8)

    private val popper =
            String(getFileFromAssets(applicationContext, POPPER_JAVASCRIPT), StandardCharsets.UTF_8)

    private val bootstrapJS =
            String(getFileFromAssets(applicationContext, BOOTSTRAP_JAVASCRIPT), StandardCharsets.UTF_8)

    private val bootstrapCSS =
            String(getFileFromAssets(applicationContext, BOOTSTRAP_CSS), StandardCharsets.UTF_8)

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

    fun buildApplicationListHTML(address: String): String {
        var applicationList: String = ""
        this.applicationContext.packageManager.getInstalledApplications(0).forEach {
            //var icon = this.applicationContext.packageManager.getApplicationIcon(it.actionName).
            applicationIconMap.put(it.packageName, getAppIconFromDrawable(it.packageName))
            if ((it.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM)
                applicationList +=
            "<a onClick=launchApp(this,\"$address?$LAUNCH_APP_ADDRESS=${it.packageName}\")  class=\"pointerCursor container-fluid float-left px-4\">" +
                "<div class=\"row\">" +
                    "<div>" +
                        "<div class=\"appIcon\">" +
                            "<img src=$APP_ICON_ADDRESS?${it.packageName} class=\" mx-auto d-block\" style=\"height: 94px; width: auto\"></img>" +
                        "</div>" +
                    "</div>" +
                    "<div class=\"appDescription\">" +
                        "<div class=\"row no-gutters px-1 fixOverflow\"  style=\"max-height: 66%;\">" +
                            "<div class=\"fixOverflow\">" +
                                "<h5 class=\"card-title\" style=\"font-size: 1.15rem;\">${this.applicationContext.packageManager.getApplicationLabel(it).toString()}</h5>"+
                            "</div>"+
                        "</div>"+
                        "<div class=\"row no-gutters px-1 py-1\" style=\"height:  33%;\">"    +
                            "<h6 class=\"card-subtitle mb-2 text-muted\" style=\"font-size: .9rem;\">${it.packageName}</h6>" +
                       " </div>"+
                    "</div>"+
                "</div>"+
            "</a>";
        }
        return applicationList
    }

    fun buildControlBarHTML(appControlAddress: String, systemControlAddress: String): String{

        var appControl: String = "<div class=\"row no-gutters my-1\" style=\"text-align: center; height: 40px;\">" +
        "<a onClick=sendRequest(\"${appControlAddress}?${TOGGLE_STREAM_ADDRESS}\")  class=\"pointerCursor col-4 py-1 mx-auto\">"+
        "<img src=${UI_ICON_ADDRESS}?${PLAY_PNG} class=\"playPause\"></img><img src=${UI_ICON_ADDRESS}?${PAUSE_PNG} class=\"playPause\"></img>"+
        "</a>"+
        "<a onClick=toggleFullscreen() class=\"col-4 py-1 mx-auto\"> <img src=${UI_ICON_ADDRESS}?${FULLSCREEN_PNG} class=\"uiIcon\"></a>"+
        "<a onClick=sendRequest(\"${systemControlAddress}?${GO_HOME_ADDRESS}\") class=\"pointerCursor col-4 py-1 mx-auto\"> <img src=${UI_ICON_ADDRESS}?${HOME_PNG} class=\"uiIcon\"></i></a>"+
       "</div>"
        return appControl;
    }

    fun buildAdditionalSettingsHTML(appControlAddress: String) : String {
        var additionalSettings =
        "<div class=\"collapse overlap bg-dark\" id=\"navbarHeader\" style=\"right:0px;\">"+
            "<div class=\"container-fluid\">"+
                "<div class=\"row no-gutters\">"+
                    "<div class=\"col-md-7 mx-auto py-4\">"+
                        "<h4 class=\"text-white\">Additional Settings</h4>"+
                            "<ul class=\"list-unstyled\">"+
                                "<li>"+
                                "<div class=\"text-white\">Image Quality</div>"+
                                    "<input type=\"range\" min=\"10\" max=\"150\" class=\"custom-range\" onchange=sendRequest(\"${appControlAddress}?${CHANGE_IMAGE_SIZE_ADDRESS}=\"+this.value)>"+
                                "</li>"+
                                "<li>"+
                                    "<div h class=\"text-white\">Image Compression</div>"+
                                    "<input type=\"range\" min=\"10\" max=\"100\" class=\"custom-range\" onchange=sendRequest(\"${appControlAddress}?${CHANGE_IMAGE_COMPRESSION_ADDRESS}=\"+this.value)>"+
                                "</li>"+
                            "</ul>"+
                        "</div>"+
                    "</div>"+
                "</div>"+
                    "<div class=\"overlay\"> </div>"+
            "</div>"
        return additionalSettings;
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

    fun getUIIconPng(name: String): ByteArray {
        when (name) {
            HOME_PNG -> return homePng
            FULLSCREEN_PNG -> return fullscreenPng
            MENU_PNG -> return menuPng
            PLAY_PNG -> return playPng
            PAUSE_PNG -> return pausePng
        }
        return ByteArray(0);
    }

    fun getJavascript(scriptName: String): String {
        when (scriptName) {
            JQUERY_JAVASCRIPT -> return jquery
            POPPER_JAVASCRIPT -> return popper
            BOOTSTRAP_JAVASCRIPT -> return bootstrapJS
        }
        return ""
    }

    fun configureStreamAddress(): String =
        if (enablePin) "/" + randomString(16) + ".mjpeg"
        else HttpServerFiles.DEFAULT_STREAM_ADDRESS

    fun configureAppActionAddress(): String =
        if (enablePin) "/" + randomString(16)
        else HttpServerFiles.DEFAULT_APP_ACTION_ADDRESS

    fun configureSystemActionAddress(): String =
        if (enablePin) "/" + randomString(16)
        else HttpServerFiles.DEFAULT_SYSTEM_ACTION_ADDRESS

    fun configureIndexHtml(streamAddress: String, appControlUIAddress: String, systemActionAddress: String): String {
        var newStyleSheet =
            baseStyleSheet.replaceFirst(
                INDEX_HTML_BACKGROUND_COLOR.toRegex(),
                "#%06X".format(0xFFFFFF and htmlBackColor)
        )
        var appList = buildApplicationListHTML(systemActionAddress)
        return baseIndexHtml
                .replaceFirst(INDEX_HTML_SCREEN_STREAM_ADDRESS.toRegex(), streamAddress)
                .replaceFirst(INDEX_HTML_APP_CONTROL_UI_ADDRESS.toRegex(), buildControlBarHTML(appControlUIAddress, systemActionAddress))
                .replaceFirst(INDEX_HTML_ADDITIONAL_SETTINGS.toRegex(), buildAdditionalSettingsHTML(appControlUIAddress))
                .replaceFirst(INDEX_HTML_CSS_STYLE.toRegex(), newStyleSheet)
                .replaceFirst(INDEX_HTML_APPLICATION_LIST.toRegex(), appList)
    }

    //TODO: Store the json somewhere so we can check it in to hub
    fun configureDirectorHTML(streamAddress: String, appControlUIAddress: String, systemActionAddress: String): String {
      var coplayJson = configureCoPlayJSON(streamAddress, appControlUIAddress, systemActionAddress)
        return baseDirectorHtml.replaceFirst("COPLAY_OBJECT".toRegex(), "var coplay = "+ coplayJson.toString() +";")
    }

    fun configureCoPlayJSON(streamAddress: String, appControlUIAddress: String, systemActionAddress: String) : JSONObject {

        val coplayObject = JSONObject()
        coplayObject.put("streamAddress", streamAddress)
        coplayObject.put("systemActionAddress", systemActionAddress)
        coplayObject.put("appControlAddress", appControlUIAddress)
        coplayObject.put("APP_ICON_ADDRESS", APP_ICON_ADDRESS)
        coplayObject.put("UI_ICON_ADDRESS", UI_ICON_ADDRESS)
        coplayObject.put("TOGGLE_STREAM_ADDRESS", TOGGLE_STREAM_ADDRESS)
        coplayObject.put("CHANGE_IMAGE_SIZE_ADDRESS", CHANGE_IMAGE_SIZE_ADDRESS)
        coplayObject.put("GET_JSON_ADDRESS", GET_JSON_ADDRESS)
        coplayObject.put("GO_HOME_ADDRESS", GO_HOME_ADDRESS)
        coplayObject.put("GO_BACK_ADDRESS", GO_BACK_ADDRESS)
        coplayObject.put("LAUNCH_APP_ADDRESS", LAUNCH_APP_ADDRESS)
        coplayObject.put("LAUNCH_ACTION_ADDRESS", LAUNCH_ACTION_ADDRESS)


        var appsArray = JSONArray()

        this.applicationContext.packageManager.getInstalledApplications(0).forEach {
            if ((it.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM) {
                applicationIconMap.put(it.packageName, getAppIconFromDrawable(it.packageName))
                val appObject = JSONObject()
                appObject.put(
                    "name",
                    this.applicationContext.packageManager.getApplicationLabel(it).toString()
                )
                appObject.put("packageName", it.packageName);

                try {
                    val appInfo = applicationContext.packageManager.getPackageInfo(it.packageName, PackageManager.GET_META_DATA);
                    val coplayActions =
                        appInfo.applicationInfo.metaData.getString("Coplay.Actions")
                    var actionsArray = JSONArray()
                    if (coplayActions != null ) {
                        Log.d("APP INFO: ", coplayActions.toString())
                        val actionsArray = JSONArray(coplayActions.split(','))
                        appObject.put("actions", actionsArray)
                    }
                    else {
                        appObject.put("actions", "")
                    }
                    appObject.put("versionName", appInfo.versionName)
                    appObject.put("versionCode", appInfo.versionCode)
                    appObject.put("firstInstallTime", appInfo.firstInstallTime)
                    appObject.put("lastUpdateTime", appInfo.lastUpdateTime)
                }
                catch (throwable: Throwable) {
                    appObject.put("actions", "")
                    //Log.d("APP INFO ERROR",throwable.toString());
                }

                /*
                var actions = AndroidManifestService.getAppActions(
                    appInfo.applicationInfo,
                    applicationContext.packageManager
                )
                for (action in actions) {
                    actionsArray.put(action);
                }
                appObject.put("actions", actionsArray)
                */


                appsArray.put(appObject)
            }
        }
        coplayObject.put("appsList", appsArray)

        val batteryObject = JSONObject()
        val bm: BatteryManager = this.applicationContext.getSystemService("batterymanager") as BatteryManager
        batteryObject.put("level", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY))
        batteryObject.put("charging", bm.isCharging())
        coplayObject.put("battery", batteryObject)

        val sysInfo = JSONObject()
        sysInfo.put("serial", android.os.Build.SERIAL)
        sysInfo.put("osVersion", System.getProperty("os.version"))
        sysInfo.put("incrementalVersion", android.os.Build.VERSION.INCREMENTAL)
        sysInfo.put("osApiLevel", android.os.Build.VERSION.SDK_INT)
        sysInfo.put("device", android.os.Build.DEVICE)
        sysInfo.put("buildDisplay", android.os.Build.DISPLAY)
        sysInfo.put("board", android.os.Build.BOARD)
        sysInfo.put("brand", android.os.Build.BRAND)
        sysInfo.put("fingerprint", android.os.Build.FINGERPRINT)
        sysInfo.put("host", android.os.Build.HOST)
        sysInfo.put("id", android.os.Build.ID)
        sysInfo.put("hardware", android.os.Build.HARDWARE)
        sysInfo.put("model", android.os.Build.MODEL)
        sysInfo.put("product", android.os.Build.PRODUCT)
        sysInfo.put("releaseVersion", android.os.Build.VERSION.RELEASE)
        coplayObject.put("sysInfo", sysInfo)

        return coplayObject
    }

    fun configurePinAddress(): String =
        if (enablePin) DEFAULT_PIN_ADDRESS + pin else DEFAULT_PIN_ADDRESS

    fun configurePinRequestHtml(streamAddress: String): String {
        if (enablePin) {
            var newStyleSheet =
                    baseStyleSheet.replaceFirst(
                            INDEX_HTML_BACKGROUND_COLOR.toRegex (),
                            "#%06X".format(0xFFFFFF and htmlBackColor)
                    )
            return basePinRequestHtml
                    .replaceFirst(INDEX_HTML_CSS_STYLE.toRegex(), newStyleSheet)
                    .replaceFirst(INDEX_HTML_SCREEN_STREAM_ADDRESS.toRegex(), streamAddress)
                    .replaceFirst(PINREQUEST_HTML_WRONG_PIN_MESSAGE.toRegex(), "&nbsp")
        }
        else
            return ""
    }


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

    fun getCSS(stylesheet: String): String {
        when (stylesheet) {
            BOOTSTRAP_CSS -> return bootstrapCSS
        }
        return ""
    }
}