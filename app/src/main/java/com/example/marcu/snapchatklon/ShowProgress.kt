package com.example.marcu.snapchatklon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.util.Log
import android.view.View


@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
fun showProgress(show: Boolean, viewInput: View, viewProgress: View) {

    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

        val shortAnimTime = android.R.integer.config_shortAnimTime.toLong()

        viewInput.visibility = if (show) View.GONE else View.VISIBLE
        viewInput.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    viewInput.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        viewProgress.visibility = if (show) View.VISIBLE else View.GONE
        viewProgress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    viewProgress.visibility = if (show) View.VISIBLE else View.GONE
                    Log.d("show Progress", "angekommen")
                }
            })
    } else {
        // The ViewPropertyAnimator APIs are not available, so simply show
        // and hide the relevant UI components.
        viewProgress.visibility = if (show) View.VISIBLE else View.GONE
        viewInput.visibility = if (show) View.GONE else View.VISIBLE
    }
}
