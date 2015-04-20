package com.the_roberto.kittengifs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import static com.the_roberto.kittengifs.Settings.getKittensBeforeAskingToRate;
import static com.the_roberto.kittengifs.Settings.setKittensBeforeAskingToRate;

public class RatingFragment extends DialogFragment {

    private final static String TAG = "RatingFragment";
    private EventsTracker eventsTracker = EventsTracker.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_rating_title))
                .setMessage(getString(R.string.dialog_rating_message))
                .setPositiveButton(getString(R.string.dialog_rating_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventsTracker.trackRatingYes();
                        final String appPackageName = "com.the_roberto.kittengifs";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (ActivityNotFoundException e) {
                            Log.wtf(TAG, e);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .setNeutralButton(getString(R.string.dialog_rating_neutral), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventsTracker.trackRatingLater(/*cancel*/ false);
                        later();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_rating_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventsTracker.trackRatingNah();
                        neverAsk();
                    }
                })
                .create();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        eventsTracker.trackRatingLater(/*cancel*/ true);
    }

    private void later() {
        setKittensBeforeAskingToRate(getActivity(), getKittensBeforeAskingToRate(getActivity()) * 4);
    }

    private void neverAsk() {
        setKittensBeforeAskingToRate(getActivity(), Integer.MAX_VALUE);
    }
}
