package com.atocash.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import com.atocash.BuildConfig
import com.atocash.R
import com.atocash.activities.splash.SplashActivity
import java.io.OutputStream
import kotlin.math.max


/**
 * Created by geniuS on 11/27/2019.
 */
class IntentHelper(private val context: Context) {

    fun openSplash(activity: Activity) {
        goTo(activity, SplashActivity::class.java, null, true)
        Handler().postDelayed({
            (context as Activity).finishAffinity()
        }, 1000)
    }

    fun closeAllAndOpen(from: Activity?, to: Class<*>?, bundle: Bundle?) {
        if (bundle != null) context.startActivity(Intent(from, to).putExtras(bundle))
        else context.startActivity(Intent(from, to))
        Handler().postDelayed({ (context as Activity).finishAffinity() }, 1000)
        (context as Activity).overridePendingTransition(R.anim.in_anim, R.anim.out_anim)
    }

    fun goTo(from: Activity?, to: Class<*>?, bundle: Bundle?, needToFinish: Boolean) {
        if (bundle != null) context.startActivity(Intent(from, to).putExtras(bundle))
        else context.startActivity(Intent(from, to))
        if (needToFinish) (context as Activity).finish()
        (context as Activity).overridePendingTransition(R.anim.in_anim, R.anim.out_anim)
    }

    fun goTo(from: Activity?, to: Class<*>?, bundle: Bundle) {
        context.startActivity(Intent(from, to).putExtras(bundle))
        (context as Activity).overridePendingTransition(R.anim.in_anim, R.anim.out_anim)
    }

    fun goTo(from: Activity?, to: Class<*>?, needToFinish: Boolean) {
        context.startActivity(Intent(from, to))
        if (needToFinish) (context as Activity).finish()
        (context as Activity).overridePendingTransition(R.anim.in_anim, R.anim.out_anim)
    }

    fun startActivityForResult(from: Activity, to: Class<*>, bundle: Bundle?, requestCode: Int) {
        if (bundle != null) from.startActivityForResult(
            Intent(from, to).putExtras(bundle),
            requestCode
        )
        else from.startActivityForResult(Intent(from, to), requestCode)
        (context as Activity).overridePendingTransition(R.anim.in_anim, R.anim.out_anim)
    }

    fun openWithTransition(
        from: Context?,
        to: Class<*>?,
        bundle: Bundle?,
        view: View?
    ) {
        val intent = Intent(from, to)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        val view2Pair =
            Pair.create(view, ViewCompat.getTransitionName(view!!))
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity), view2Pair)
        context.startActivity(intent, activityOptionsCompat.toBundle())
    }

    fun openWithTransition(
        from: Context?,
        to: Class<*>?,
        bundle: Bundle?,
        view: View?,
        canFinish: Boolean
    ) {
        val intent = Intent(from, to)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        val view2Pair =
            Pair.create(
                view,
                ViewCompat.getTransitionName(view!!)
            )
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity), view2Pair)
        context.startActivity(intent, activityOptionsCompat.toBundle())
        if (canFinish) context.finish()
    }

    fun openWithTransitionForResult(
        from: Activity,
        to: Class<*>?,
        bundle: Bundle?,
        view: View?
    ) {
        val intent = Intent(from, to)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        val view2Pair =
            Pair.create(
                view,
                ViewCompat.getTransitionName(view!!)
            )
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(from, view2Pair)
        from.startActivityForResult(intent, 152, activityOptionsCompat.toBundle())
    }

    //call this method to open activity with reveal on view click
    fun openWithReveal(
        from: Activity,
        to: Class<*>?,
        bundle: Bundle?,
        view: View,
        canFinish: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    from,
                    view,
                    Keys.IntentHelper.CIRCULAR_TRANSITION
                )
//            val revealX = (view.x + view.measuredWidth / 2f).toInt()
//            val revealY = (view.y + view.measuredHeight / 2f).toInt()

            //gets the absolute value of the view on the screen
            val point = IntArray(2)
            view.getLocationOnScreen(point)

            val revealX = point[0]
            val revealY = point[1]

            val intent = Intent(from, to)
            intent.putExtra(Keys.IntentHelper.CIRCULAR_REVEAL_X, revealX)
            intent.putExtra(Keys.IntentHelper.CIRCULAR_REVEAL_Y, revealY)
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            ActivityCompat.startActivity(from, intent, activityOptionsCompat.toBundle())
            if (canFinish) {
                from.finish()
            }
        } else {
            goTo(from, to, bundle, canFinish)
        }
    }

    fun getWithReveal(intent: Intent, bundle: Bundle?, view: View) {
        if (bundle == null) {
            if (intent.hasExtra(Keys.IntentHelper.CIRCULAR_REVEAL_X) && intent.hasExtra(Keys.IntentHelper.CIRCULAR_REVEAL_Y)) {
                view.visibility = View.INVISIBLE
                val revealX = intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_X, 0)
                val revealY = intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_Y, 0)
                val viewTreeObserver = view.viewTreeObserver
                if (viewTreeObserver.isAlive) {
                    view.post {
                        revealActivity(revealX, revealY, view)
                    }
                    /*viewTreeObserver.addOnGlobalLayoutListener {
                        AppHelper.printLog("getWithReveal addOnGlobalLayoutListener ")
                        view.viewTreeObserver.removeOnGlobalLayoutListener {
                            AppHelper.printLog("getWithReveal removeOnGlobalLayoutListener")
                        }
                    }*/
                }
            }
        } else {
            view.visibility = View.VISIBLE
        }
    }

    private fun revealActivity(revealX: Int, revealY: Int, rootView: View) {
        val endRadius = max(rootView.width, rootView.height).toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val circularReveal =
                ViewAnimationUtils.createCircularReveal(rootView, revealX, revealY, 0f, endRadius)
            circularReveal.duration = 500
            circularReveal.interpolator = AccelerateInterpolator()
            rootView.visibility = View.VISIBLE
            circularReveal.start()
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rootView.clearAnimation()
                }
            })
        }
    }

    //complete reveal on finish the activity with reveal
    fun unRevealActivity(
        activity: Activity,
        intent: Intent,
        view: View?
    ) {
        if (intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_X, 0) == 0 ||
            intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_Y, 0) == 0
        ) {
            activity.finish()
            return
        } else {
            val revealX = intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_X, 0)
            val revealY = intent.getIntExtra(Keys.IntentHelper.CIRCULAR_REVEAL_Y, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val endRadius = max(view!!.width, view.height).toFloat() * 1.2f
                val circularReveal = ViewAnimationUtils.createCircularReveal(
                    view,
                    revealX,
                    revealY,
                    endRadius,
                    view.x
                )
                circularReveal.duration = 500
                circularReveal.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.INVISIBLE
                        activity.finish()
                    }
                })
                circularReveal.start()
            } else {
                activity.finish()
            }
        }
    }


    //static methods
    companion object {
        @JvmStatic
        var imageUri: Uri? = null

        //used to open camera and take image
        @JvmStatic
        fun getCameraIntent(context: Context): Intent? {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "Image title")
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image description!")
            imageUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT, imageUri
            )
            //this extra OP function will not deliver result via intent in onActivityResult
            //the image will be stored in the "imageUri" variable and from this it can be used
            return intent
        }

        fun pickFilesIntent(): Intent {
            val intent = Intent(Intent.ACTION_PICK)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            return intent
        }

        fun getVideoPickerIntent(): Intent {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            return intent
        }

        @JvmStatic
        fun shareText(context: Context, message: String) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(shareIntent)
        }

        @JvmStatic
        fun pickContacts(context: Context) {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(
                ContactsContract.Contacts.CONTENT_URI,
                ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            )
            (context as Activity).startActivityForResult(
                Intent.createChooser(
                    pickIntent,
                    "Select Contact"
                ), Keys.IntentHelper.SELECT_CONTACT
            )
        }

        @JvmStatic
        fun getWhatsAppIntent(): Intent? {
            return try {
                val whatsAppIntent = Intent(Intent.ACTION_SEND)
                whatsAppIntent.type = "text/plain"
                whatsAppIntent.setPackage("com.whatsapp")
                whatsAppIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I want to talk about business!")
                whatsAppIntent
            } catch (e: Exception) {
                null
            }
        }

        @JvmStatic
        fun openDialPad(context: Context, mobileNumber: String) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$mobileNumber")
            context.startActivity(intent)
        }

//        @SuppressLint("QueryPermissionsNeeded")
//        @JvmStatic
//        fun sendWhatsappMessageWithoutContact(context: Context, phone: String, message: String){
//            val packageManager: PackageManager = context.packageManager
//            val i = Intent(Intent.ACTION_VIEW)
//
//            try {
//                val url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8")
//                i.setPackage("com.whatsapp")
//                i.data = Uri.parse(url)
//                if (i.resolveActivity(packageManager) != null) {
//                    context.startActivity(i)
//                }
//            } catch (e: java.lang.Exception) {
//                AppHelper.printStackTrace(e)
//                AppHelper.toastInfo(context, context.getString(R.string.whatsapp_not_installed))
//            }
//        }

        @JvmStatic
        fun shareAppToOthers(context: Context) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage = """
                    $shareMessage https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                context.startActivity(Intent.createChooser(shareIntent, "Share app via..."))
            } catch (e: java.lang.Exception) {
                AppHelper.toastInfo(context, context.getString(R.string.cannot_share_app))
            }
        }

        @JvmStatic
        fun openAppOnPlayStore(context: Context) {
            val packageName = BuildConfig.APPLICATION_ID
//            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val uri: Uri = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
            } else {
                goToMarket.addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                )
            }
            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
                //context.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        @JvmStatic
        fun openBrowser(context: Context, url: String?) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getGalleryUriIntent(): Intent? {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            return intent
        }

        @JvmStatic
        fun viewOnMap(context: Context, address: String) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.co.in/maps?q=$address")
                )
            )
            /*return Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    java.lang.String.format(
                        "geo:0,0?q=%s",
                        URLEncoder.encode(address ,"UTF-8")
                    )
                )
            )*/
        }

        fun viewOnMap(lat: String?, lng: String?): Intent? {
            return Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format("geo:%s,%s", lat, lng))
            )
        }

        @JvmStatic
        fun shareImage(context: Context, bitmap: Bitmap, failedMsg: String) {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "Shared image")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            val uri: Uri? = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            val outStream: OutputStream
            try {
                outStream = uri?.let { context.contentResolver.openOutputStream(it) }!!
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.close()

                share.putExtra(Intent.EXTRA_STREAM, uri)
                context.startActivity(Intent.createChooser(share, "Share Image"))
            } catch (e: Exception) {
                AppHelper.printLog(e.toString())
                AppHelper.toastInfo(context, failedMsg)
            }
        }

        @JvmStatic
        fun sendEmail(context: Context, recipient: String, subject: String, message: String) {
            val mIntent = Intent(Intent.ACTION_SEND)
            mIntent.data = Uri.parse("mailto:")
            mIntent.type = "text/plain"
            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            mIntent.putExtra(Intent.EXTRA_TEXT, message)
            try {
                context.startActivity(Intent.createChooser(mIntent, "Send email via..."))
            } catch (e: Exception) {
                AppHelper.printStackTrace(e)
                AppHelper.toastInfo(context, "No email clients found to send email")
            }
        }

//        fun pickMultipleImages(context: Context) {
//            FishBun.with(context as Activity)
//                .setImageAdapter(GlideAdapter())
//                .setMinCount(Keys.VideoOptions.MIN_IMAGE_COUNT)
//                .setMaxCount(Keys.VideoOptions.MAX_IMAGE_COUNT)
//                .textOnNothingSelected(context.getString(R.string.err_choose_min_img_for_blog))
//                .textOnImagesSelectionLimitReached(context.getString(R.string.err_choose_min_img_for_blog))
//                .exceptGif(true)
//                .setActionBarColor(
//                    Color.parseColor("#128c7e"),
//                    Color.parseColor("#128c7e"),
//                    false
//                )
//                .setActionBarTitleColor(Color.parseColor("#ffffff"))
//                .startAlbum()
//        }
    }
}