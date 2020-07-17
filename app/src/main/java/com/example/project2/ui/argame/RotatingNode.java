package com.example.project2.ui.argame;

import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.animation.AnticipateOvershootInterpolator;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

import java.util.Random;

public class RotatingNode extends Node implements Node.OnTapListener {
    private ObjectAnimator rotateAnimation = null;
    private static Random rng = new Random();
    private int rotations;
    private int facingAngle;

    public RotatingNode() {
        this.facingAngle = 0;
        this.rotations = 10;
        setOnTapListener(this);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);
    }

    @Override
    public void onActivate() {
        startAnimation();
    }

    @Override
    public void onDeactivate() {
        stopAnimation();
    }

    public void startAnimation() {
        if (rotateAnimation != null)
            return;
        rotateAnimation = createAnimator();
        rotateAnimation.setTarget(this);
        rotateAnimation.setDuration(getAnimationDuration());
        rotateAnimation.start();
        rotateAnimation = null;
    }

    public void stopAnimation() {
        if (rotateAnimation == null)
            return;
        rotateAnimation.cancel();
        rotateAnimation = null;
    }

    private long getAnimationDuration() {
        return (long) (rng.nextInt(7) + 5) * 1000;
    }

    private ObjectAnimator createAnimator() {
        // 0, 1, 2
        int randSegment = rng.nextInt(3);
        int randAngle = rng.nextInt(120);

        Quaternion[] orientations = new Quaternion[3 * this.rotations + 1 + (randSegment + 1)];
        for (int i = 0; i < 3 * this.rotations + 1; i++) {
            int angle = facingAngle + i * 360 * this.rotations / (3 * this.rotations);
            orientations[i] = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), angle);
        }

        for (int i = 0; i < randSegment; i++) {
            int angle = facingAngle + 360 * this.rotations + 120 * (i + 1);
            orientations[3 * this.rotations + 1 + i] = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), angle);
        }
        orientations[orientations.length - 1] = Quaternion.axisAngle(
                new Vector3(0.0f, 1.0f, 0.0f),
                facingAngle + 360 * this.rotations + 120 * randSegment + randAngle
        );
        facingAngle = (facingAngle + 360 * this.rotations + 120 * randSegment + randAngle) % 360;
        ObjectAnimator animator = new ObjectAnimator();
        animator.setObjectValues((Object[])orientations);
        animator.setPropertyName("localRotation");
        animator.setEvaluator(new QuaternionEvaluator());
        animator.setInterpolator(new AnticipateOvershootInterpolator(1f, 0.3f));
        animator.setAutoCancel(true);

        return animator;
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        startAnimation();
    }
}
