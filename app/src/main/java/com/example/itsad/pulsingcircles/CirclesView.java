package com.example.itsad.pulsingcircles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CirclesView extends View {

    private Paint circlePaint, gapPaint;
    private static final int STROKE_WIDTH = 3;

    private TouchCallback callback;


    private Point center;

    public CirclesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CirclesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclesView(Context context) {
        super(context);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        gapPaint = new Paint();


        circlePaint.setStyle(Paint.Style.STROKE);
        gapPaint.setStyle(Paint.Style.STROKE);

        circlePaint.setStrokeWidth(STROKE_WIDTH);
        gapPaint.setStrokeWidth(STROKE_WIDTH);

        circlePaint.setColor(Color.BLACK);
        gapPaint.setColor(Color.WHITE);

    }

    public void setCallback(TouchCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        center = new Point(x/2, y/2);
        int radius;
        radius = x / 2;
        canvas.drawPaint(gapPaint);

        canvas.drawCircle(x / 2, y / 2, radius, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xTouch = -1;
        float yTouch = -1;

        int actionIndex = event.getActionIndex();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xTouch =  event.getX();
                yTouch =  event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);
                break;
            case MotionEvent.ACTION_UP:
                callback.fingerUp();
                break;
            default:
                break;
        }
        // we check if the distance to the center is bigger than the radius
        if (xTouch == -1 && yTouch == -1) {
            return true;
        }
        float adjustedX = xTouch - center.x;
        float adjustedY = yTouch - center.y;
        double distanceFromCenter = Math.sqrt(Math.pow(adjustedX, 2) + Math.pow(adjustedY, 2));

        // we need to calculate the angle
        double angle = Math.atan2(adjustedY, adjustedX);

        // thi is in rad, need in degrees
        double angleDegrees = ((angle + Math.PI) / (Math.PI * 2)) * 360;

        // these degrees need to be adjusted to the drawing degrees we use
        // for making the holes
        double adjustedDegrees = (angleDegrees + 180) % 360;
        callback.touchPosition((float)distanceFromCenter, (float) adjustedDegrees);
        return true;
    }

}