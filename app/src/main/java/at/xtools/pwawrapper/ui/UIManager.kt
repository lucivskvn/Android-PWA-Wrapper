package at.xtools.pwawrapper.ui

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import at.xtools.pwawrapper.BuildConfig
import at.xtools.pwawrapper.R

class UIManager(  // Instance variables
    private val activity: Activity
) {
    private val webView: WebView = activity.findViewById(R.id.webView)
    private val progressSpinner: ProgressBar = activity.findViewById(R.id.progressSpinner)
    private val progressBar: ProgressBar = activity.findViewById(R.id.progressBarBottom)
    private val offlineContainer: LinearLayout = activity.findViewById(R.id.offlineContainer)
    private var pageLoaded = false

    // Set Loading Progress for ProgressBar
    fun setLoadingProgress(progress: Int) {
        // set progress in UI
        progressBar.setProgress(progress, true)

        // hide ProgressBar if not applicable
        if (progress in 0..99) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.INVISIBLE
        }

        // get app screen back if loading is almost complete
        if (progress >= BuildConfig.PROGRESS_THRESHOLD && !pageLoaded) {
            setLoading(false)
        }
    }

    // Show loading animation screen while app is loading/caching the first time
    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressSpinner.visibility = View.VISIBLE
            webView.animate().translationX(BuildConfig.SLIDE_EFFECT.toFloat()).alpha(0.5f)
                .setInterpolator(AccelerateInterpolator()).start()
        } else {
            webView.translationX = (BuildConfig.SLIDE_EFFECT * -1).toFloat()
            webView.animate().translationX(0f).alpha(1f).setInterpolator(DecelerateInterpolator())
                .start()
            progressSpinner.visibility = View.INVISIBLE
        }
        pageLoaded = !isLoading
    }

    // handle visibility of offline screen
    fun setOffline(offline: Boolean) {
        if (offline) {
            setLoadingProgress(100)
            webView.visibility = View.INVISIBLE
            offlineContainer.visibility = View.VISIBLE
        } else {
            webView.visibility = View.VISIBLE
            offlineContainer.visibility = View.INVISIBLE
        }
    }

    // set icon in recent activity view to a white one to be visible in the app bar
    fun changeRecentAppsIcon() {
        val iconWhite = BitmapFactory.decodeResource(activity.resources, R.drawable.ic_appbar)
        val typedValue = TypedValue()
        val theme = activity.theme
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val color = typedValue.data
        val description = TaskDescription(
            activity.resources.getString(R.string.app_name),
            iconWhite,
            color
        )
        activity.setTaskDescription(description)
        iconWhite.recycle()
    }

    init {

        // set click listener for offline-screen
        offlineContainer.setOnClickListener { v: View? ->
            webView.loadUrl(BuildConfig.WEBAPP_URL)
            setOffline(false)
        }
    }
}