package com.the_roberto.kittengifs.kittens

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import com.the_roberto.kittengifs.App
import com.the_roberto.kittengifs.EventsTracker
import com.the_roberto.kittengifs.IntentStarter
import com.the_roberto.kittengifs.R
import com.the_roberto.kittengifs.model.Settings
import javax.inject.Inject

class RatingDialog : DialogFragment() {

    private val TAG = "RatingFragment"
    @Inject lateinit var settings: Settings

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        injectDependencies()
        return AlertDialog.Builder(activity).setTitle(getString(R.string.dialog_rating_title)).setMessage(getString(R.string.dialog_rating_message))
                .setPositiveButton(getString(R.string.dialog_rating_positive)) { dialog, which ->
                    neverAsk()
                    EventsTracker.trackRatingYes()
                    val appPackageName = "com.the_roberto.kittengifs"
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                    } catch (e: ActivityNotFoundException) {
                        Log.wtf(TAG, e)
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)))
                    }
                }.setNeutralButton(getString(R.string.dialog_rating_neutral)) { dialog, which ->
            EventsTracker.trackRatingLater(/*cancel*/ false)
            later()
        }.setNegativeButton(getString(R.string.dialog_rating_negative)) { dialog, which ->
            EventsTracker.trackRatingNah()
            neverAsk()
        }.create()
    }

    private fun injectDependencies() {
        (activity.application as App).appComponent.inject(this)
    }


    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        EventsTracker.trackRatingLater(/*cancel*/ true)
        later()
    }

    private fun later() {
        settings.setKittensBeforeAskingToRate(settings.getKittensBeforeAskingToRate() * 4)
    }

    private fun neverAsk() {
        settings.setKittensBeforeAskingToRate(Integer.MAX_VALUE)
    }
}
