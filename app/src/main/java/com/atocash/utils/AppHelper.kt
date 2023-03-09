package com.atocash.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.atocash.BuildConfig
import com.atocash.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by geniuS on 9/4/2019.
 */
object AppHelper {
    fun isNetworkConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun checkNetworkConnection(context: Context): Boolean {
        var result = false
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                val capabilities =
                    cm.getNetworkCapabilities(cm.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = true
                    }
                }
            }
        } else {
            if (cm != null) {
                val activeNetwork = cm.activeNetworkInfo
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    @JvmStatic
    fun printLog(msg: String) {
        if (BuildConfig.DEBUG) {
            val chunkSize = 2048
            if (msg.length >= chunkSize) {
                var i = 0
                while (i < msg.length) {
                    Log.e(
                        "Debug ",
                        msg.substring(i, Math.min(msg.length, i + chunkSize))
                    )
                    i += chunkSize
                }
            } else {
                Log.e("Debug ", msg)
            }
        }
    }

    fun printStackTrace(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(
                "Debug ", """${e.message}$e""".trimIndent()
            )
        }
    }

    fun toastInfo(context: Context?, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }


    fun getVersionCodeOfApp(context: Context): String {
        var version: String
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            version = pInfo.versionCode.toString() + "." + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            version = BuildConfig.VERSION_CODE.toString() + "." + BuildConfig.VERSION_NAME
            e.printStackTrace()
        }
        return "Version: $version"
    }

    fun hideKeyboard(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val imm =
                context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideKeyboard(context: Context) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(
                Objects.requireNonNull((context as AppCompatActivity).currentFocus)?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun showImageChooseDialog(context: Context?, imageChooserCallback: ImageChooserCallback) {
        val items = arrayOf<CharSequence>(
            "Take Photo",
            "Open Gallery", "Cancel"
        )
        val builder = AlertDialog.Builder(context!!)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            when {
                items[item] == "Take Photo" -> {
                    imageChooserCallback.onChosen(0)
                }
                items[item] == "Open Gallery" -> {
                    imageChooserCallback.onChosen(1)
                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    /*public static void showImageChooseDialog(Context context, final ImageChooserCallback imageChooserCallback) {
        Dialog dialog = new Dialog(context, R.style.DialogSlideUpAndDown);
        dialog.setContentView(R.layout.item_image_picker);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        if (!dialog.isShowing() && !((Activity) context).isFinishing()) dialog.show();

        MaterialRippleLayout takePhoto = dialog.findViewById(R.id.takePhoto);
        MaterialRippleLayout chooseGallery = dialog.findViewById(R.id.chooseGallery);
        MaterialRippleLayout cancel = dialog.findViewById(R.id.cancel);

        takePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            imageChooserCallback.onChosen(0);
        });
        chooseGallery.setOnClickListener(v -> {
            dialog.dismiss();
            imageChooserCallback.onChosen(1);
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
    }*/

    fun getDate(calendar: Calendar): String {
        val df = getDateFormat()
        return df.format(calendar.time)
    }

    fun getDateFormat() : SimpleDateFormat {
//        TimeZone.getTimeZone("UTC")
        val dateFormat = "dd-MM-yyyy"
        return SimpleDateFormat(dateFormat, Locale.getDefault())
    }

    fun getDateOnly(time: Long): String {
        val date: String
        val calendar = Calendar.getInstance()
        val sdf =
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        calendar.timeInMillis = time * 1000
        val currenTimeZone = calendar.time
        date = sdf.format(currenTimeZone)
        return date
    }

    fun convertDate(dateInMilliseconds: Long): String {
        return DateFormat.format("MM/dd/yyyy", dateInMilliseconds * 1000)
            .toString()
    }

    fun showLoadingDialog(context: Context?, msg: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.dialog_creating_prgress);

        progressDialog.findViewById<TextView>(R.id.progress_text).text = msg

        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    fun loadImageInCircleView(
        context: Context,
        imageUrl: String,
        imageView: ImageView
    ) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions.circleCropTransform())
            .load(imageUrl)
            .thumbnail(
                Glide.with(context)
                    .load(imageUrl)
            )
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    fun loadImageWithGlide(
        context: Context,
        imageView: ImageView,
        image: String
    ) {
        val thumbNailReq = Glide.with(context).load(image).apply(RequestOptions().override(60, 60))

        printLog("ImageLink: $image")
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.app_place_holder)
                    .dontAnimate()
                    .fitCenter()
            )
            .load(image)
            .thumbnail(thumbNailReq)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /* public static void showWhiteToast(Context context, String msg) {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);

        TextView toastTv = layout.findViewById(R.id.custom_toast_text);
        toastTv.setText(msg);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 75);
        toast.setView(layout);
        toast.show();
    }*/
    fun takeScreenShot(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val b1 = view.drawingCache
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val width = activity.windowManager.defaultDisplay.width
        val height = activity.windowManager.defaultDisplay.height
        val b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return b
    }

    fun getBlurredBitmap(sentBitmap: Bitmap, radius: Int): Bitmap? {
        val bitmap = sentBitmap.copy(sentBitmap.config, true)
        if (radius < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack =
            Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        Log.e("pix", w.toString() + " " + h + " " + pix.size)
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    interface ImageChooserCallback {
        fun onChosen(chosenItem: Int)
    }
}