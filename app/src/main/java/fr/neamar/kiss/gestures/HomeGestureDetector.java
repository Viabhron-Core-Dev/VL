package fr.neamar.kiss.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Detects swipe, double tap, and long press gestures on the home screen.
 */
public class HomeGestureDetector extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public interface OnGestureListener {
        void onGestureDetected(GestureType type);
    }

    private final GestureDetector gestureDetector;
    private final OnGestureListener listener;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public HomeGestureDetector(Context context, OnGestureListener listener) {
        this.gestureDetector = new GestureDetector(context, this);
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        listener.onGestureDetected(GestureType.DOUBLE_TAP);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        listener.onGestureDetected(GestureType.LONG_PRESS);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;
        
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    listener.onGestureDetected(GestureType.SWIPE_RIGHT);
                } else {
                    listener.onGestureDetected(GestureType.SWIPE_LEFT);
                }
                return true;
            }
        } else {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    listener.onGestureDetected(GestureType.SWIPE_DOWN);
                } else {
                    listener.onGestureDetected(GestureType.SWIPE_UP);
                }
                return true;
            }
        }
        return false;
    }
}
