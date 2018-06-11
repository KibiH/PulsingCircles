package com.example.itsad.pulsingcircles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class CircleWithHolesView extends View {

    private Paint circlePaint, gapPaint;
    private static final float holeSweepAngle = 30;

    private int numHoles;
    private final int strokeWidth;
    private int holesAngles[] = new int[3];

    private int radius;
    private RedrawCallback callback;
    public boolean wasOutside = false;

    public CircleWithHolesView(Context context, int strokeWidth) {
        super(context);
        this.strokeWidth = strokeWidth;
        init();
    }

    public void setOneHole(int angleOfHole) {
        numHoles = 1;
        holesAngles[0] = angleOfHole;
    }

    public void setTwoHoles(int angleOfHole1, int angleOfHole2) {
        numHoles = 2;
        holesAngles[0] = angleOfHole1;
        holesAngles[1] = angleOfHole2;
    }

    public void setThreeHoles(int angleOfHole1, int angleOfHole2, int angleOfHole3) {
        numHoles = 3;
        holesAngles[0] = angleOfHole1;
        holesAngles[1] = angleOfHole2;
        holesAngles[2] = angleOfHole3;
    }

    public void setCallback(RedrawCallback callback) {
        this.callback = callback;
    }

    private void init() {
        circlePaint = new Paint();
        gapPaint = new Paint();


        circlePaint.setStyle(Paint.Style.STROKE);
        gapPaint.setStyle(Paint.Style.STROKE);

        circlePaint.setStrokeWidth(strokeWidth);
        gapPaint.setStrokeWidth(strokeWidth);

        circlePaint.setColor(Color.RED);
        gapPaint.setColor(Color.WHITE);

    }

    public int getRadius() {
        return radius;
    }

    public int getNumHoles() {
        return numHoles;
    }

    public int getHoleAngle(int numHole) {
        return holesAngles[numHole];
    }

    public float getHoleSweepAngle() {
        return holeSweepAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        radius = x / 2 - strokeWidth;

        int startTop = strokeWidth ;
        int startLeft = startTop;

        int endBottom = 2 * radius + strokeWidth ;
        int endRight = endBottom;

        RectF rect = new RectF(startTop, startLeft, endRight, endBottom);

        // this draw the main black circle
        canvas.drawCircle(x / 2, y / 2, radius, circlePaint);

        // now draw the hole(s)
        for (int i = 0; i < numHoles; i++) {
            canvas.drawArc(rect, holesAngles[i], holeSweepAngle, false, gapPaint);
        }

        if (null != callback) {
            callback.redrewCircle();
        }
    }

}