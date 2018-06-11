package com.example.itsad.pulsingcircles;

public interface TouchCallback {
    void touchPosition(float radius, float adjustedAngle);
    void fingerUp();
}
