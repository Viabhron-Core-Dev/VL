package fr.neamar.kiss.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A custom transparent clock widget for the VibeForge Vian Launcher.
 */
public class TransparentClockWidget extends LinearLayout {
    private TextView timeTextView;
    private TextView dateTextView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            handler.postDelayed(this, 1000 * 60); // Update every minute
        }
    };

    public TransparentClockWidget(Context context) {
        super(context);
        init();
    }

    public TransparentClockWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.TRANSPARENT);
        setPadding(0, 60, 0, 60);

        timeTextView = new TextView(getContext());
        timeTextView.setTextColor(Color.WHITE);
        timeTextView.setTextSize(64);
        timeTextView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        timeTextView.setGravity(Gravity.CENTER);
        addView(timeTextView);

        dateTextView = new TextView(getContext());
        dateTextView.setTextColor(Color.parseColor("#BBBBBB"));
        dateTextView.setTextSize(18);
        dateTextView.setGravity(Gravity.CENTER);
        addView(dateTextView);

        updateTime();
    }

    private void updateTime() {
        Date now = new Date();
        timeTextView.setText(timeFormat.format(now));
        dateTextView.setText(dateFormat.format(now));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(updateTimeRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(updateTimeRunnable);
    }
}
