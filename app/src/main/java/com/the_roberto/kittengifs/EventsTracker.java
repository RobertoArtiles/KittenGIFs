package com.the_roberto.kittengifs;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static com.google.android.gms.analytics.Logger.LogLevel.ERROR;
import static com.google.android.gms.analytics.Logger.LogLevel.VERBOSE;

public class EventsTracker {
    private static EventsTracker INSTANCE;
    private final GoogleAnalytics googleAnalytics;
    private final Tracker tracker;

    public static void init(Context context) {
        INSTANCE = new EventsTracker(context);
    }

    public static EventsTracker getInstance() {
        return INSTANCE;
    }

    public EventsTracker(Context context) {
        googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.getLogger().setLogLevel(BuildConfig.DEBUG ? VERBOSE : ERROR);
        tracker = BuildConfig.DEBUG ? googleAnalytics.newTracker("debug") : googleAnalytics.newTracker(R.xml.global_tracker);
    }

    public void trackNextKitten() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("User Actions")
                .setAction("Next Kitten")
                .build());
    }

    public void trackShare() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("User Actions")
                .setAction("Share")
                .build());
    }

    public void trackSuccessfulKitten() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action Results")
                .setAction("Kitten Download")
                .setLabel("success")
                .build());
    }

    public void trackFailedKitten() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action Results")
                .setAction("Kitten Download")
                .setLabel("fail")
                .build());
    }
}
