package com.the_roberto.kittengifs;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

public class CounterView extends TextView {

    private long count;

    public CounterView(Context context) {
        super(context);
        init(context);
    }

    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CounterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        count = Settings.getViewsCount(context);
        setText(Long.toString(getCount()));
    }

    public void increment() {
        Settings.setViewsCount(getContext(), ++count);
        setText(Long.toString(count));
    }

    public long getCount() {
        return count;
    }

}
