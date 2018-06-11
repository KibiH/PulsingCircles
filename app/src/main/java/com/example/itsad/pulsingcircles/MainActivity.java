package com.example.itsad.pulsingcircles;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements TouchCallback, RedrawCallback {

    private static final int STROKE_WIDTH = 10;

    private static final Random random = new Random();
    private ConstraintLayout mainView;
    private CirclesView mainCircle;
    private TextView scoreText;
    private Button startButton;
    private Timer secondTimer;
    boolean gameOn = false;

    private float lastDistance;
    private float lastAngle;

    private ArrayList<CircleWithHolesView> holeyCircles = new ArrayList();

    private int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = findViewById(R.id.mainView);
        mainCircle = findViewById(R.id.circleview);
        mainCircle.setCallback(this);
        scoreText = findViewById(R.id.scoretext);

        scoreText.setText(getString(R.string.score) + score);

        startButton = findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                runNewGame();
            }
        });

    }

    private void runNewGame() {
        score = 0;
        scoreText.setText(getString(R.string.score) + score);
        for(CircleWithHolesView circle : holeyCircles) {
            mainView.removeView(circle);
        }
        holeyCircles.clear();
        gameOn = true;

        if (secondTimer != null) {
            secondTimer.cancel();
            secondTimer = null;
        }

        secondTimer = new Timer();
        secondTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                animateCircleGrowing();
            }
        }, 0, 1000);

    }

    private void animateCircleGrowing() {
        // each second we will increase the size of all the list of circles and animate them through
        // the size growing
        final int maxHeight = mainCircle.getWidth(); //since height and width are the same

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // each second we add a new circle
                CircleWithHolesView cwh = new CircleWithHolesView(MainActivity.this, STROKE_WIDTH);
                cwh.setCallback(MainActivity.this);
                int numHoles = 1 + random.nextInt(3 ); // min of one
                switch (numHoles) {
                    case 1:
                        cwh.setOneHole(random.nextInt(360));
                        break;
                    case 2:
                        cwh.setTwoHoles(random.nextInt(150), 180 + random.nextInt(150));
                        break;
                    case 3:
                        cwh.setThreeHoles(random.nextInt(90), 120 + random.nextInt(90),
                                240 + random.nextInt(90));
                        break;
                }
                cwh.setLayoutParams(new ConstraintLayout.LayoutParams(100, 100));
                mainView.addView(cwh);

                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) cwh.getLayoutParams();
                lp.leftToLeft = R.id.mainView;
                lp.rightToRight = R.id.mainView;
                lp.topToTop = R.id.mainView;
                lp.bottomToBottom = R.id.mainView;
                cwh.setLayoutParams(lp);
                cwh.requestLayout();

                holeyCircles.add(cwh);
                for(CircleWithHolesView circle : holeyCircles) {
                    int height = circle.getLayoutParams().height;
                    if (height >= maxHeight - STROKE_WIDTH) {
                        mainView.removeView(circle);
                        continue;
                    }
                    int newHeight = Math.min(maxHeight, height + 400);

                    ExpandingAnimation ani = new ExpandingAnimation(circle, newHeight, height);
                    ani.setDuration(1000);
                    circle.startAnimation(ani);
                }

                Iterator<CircleWithHolesView> i = holeyCircles.iterator();
                while (i.hasNext()) {
                    CircleWithHolesView cwhHere = i.next(); // must be called before you can call i.remove()
                    if (cwhHere.getLayoutParams().height >= maxHeight - STROKE_WIDTH) {
                        i.remove();
                    }
                }
                for(CircleWithHolesView circle : holeyCircles) {
                    int height = circle.getLayoutParams().height;
                    if (height >= maxHeight - STROKE_WIDTH) {
                        holeyCircles.remove(circle);
                    }
                }

                // update the score
                scoreText.setText(getString(R.string.score) + score);
            }
        });

    }

    @Override
    public void touchPosition(float distance, float adjustedAngle) {
        if (!gameOn) {
            return;
        }
        lastDistance = distance;
        lastAngle = adjustedAngle;
        checkCollisions();
    }

    private void checkCollisions() {
        // while in the game we need to check through the circles to see current
        // position and if we are indide, outdie or on the edge (within a certain tolerance)
        for(CircleWithHolesView circle : holeyCircles) {
            float sweepAngle = circle.getHoleSweepAngle();
            int radius = circle.getRadius();
            if (lastDistance > radius + (STROKE_WIDTH / 2)) {
                circle.wasOutside = true;
            }
            if (circle.wasOutside && lastDistance < radius - (STROKE_WIDTH / 2)) {
                // we moved from outside to inside, add a point
                score++;
                circle.wasOutside = false; // don't count points twice
            }
            // check if we hit the circle
            if (lastDistance > radius - (STROKE_WIDTH / 2) && lastDistance < radius + (STROKE_WIDTH / 2)) {
                // on the edge of the circle - now check the angle against the holes
                boolean safe = false;
                for(int i = 0; i < circle.getNumHoles(); i++) {
                    int angle = circle.getHoleAngle(i);
                    if (lastAngle > angle && lastAngle < (angle + sweepAngle)) {
                        safe = true;
                        break;
                    }
                }
                if (!safe) {
                    // we hit the circle
                    gameOn = false;
                    if (secondTimer != null) {
                        secondTimer.cancel();
                        secondTimer = null;
                    }
                    startButton.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void fingerUp() {
        if (!gameOn) {
            return;
        }
        // lifting your finger also ends the game
        gameOn = false;
        if (secondTimer != null) {
            secondTimer.cancel();
            secondTimer = null;
        }
        startButton.setEnabled(true);

    }

    @Override
    public void redrewCircle() {
        checkCollisions();
    }
}