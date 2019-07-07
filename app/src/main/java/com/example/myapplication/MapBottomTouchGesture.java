package com.example.myapplication;

import android.animation.ValueAnimator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MapBottomTouchGesture extends GestureDetector.SimpleOnGestureListener {
    private final View view;
    private int max;
    private Direction direction = Direction.down;

    public MapBottomTouchGesture(View view, int max) {

        this.view = view;
        this.max = max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1, y1, x2, y2);
        int nowSize = view.getLayoutParams().height;
        if (direction == Direction.up) {
            int newSize = (int) (nowSize + Math.abs(distanceY));
            if (newSize >= max) {
                view.getLayoutParams().height = max;
                return true;
            } else {
                view.getLayoutParams().height = newSize;
            }
        } else {
            int newSize = (int) (nowSize - Math.abs(distanceY));
            if (newSize <= 0) {
                view.getLayoutParams().height = 0;
                return true;
            } else {
                view.getLayoutParams().height = newSize;
            }
        }

        view.requestLayout();

        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

      /*  float x1 = e1.getX();
        float y1 = e1.getY();

        float x2 = e2.getX();
        float y2 = e2.getY();

        Direction direction = getDirection(x1, y1, x2, y2);
        Log.v("TAG", "onFling");*/
        return true;
    }

    public void onActionUp() {
        final int nowSize = view.getLayoutParams().height;
        if (nowSize >= max / 2) {
            //GoUp
            ValueAnimator valueAnimator = ValueAnimator.ofInt(nowSize, max);
            valueAnimator.addUpdateListener(valueAnimator1 -> {
                int animatedValue = (int) valueAnimator1.getAnimatedValue();
                int valueDiff = animatedValue - nowSize;
                int newSize = view.getLayoutParams().height + valueDiff;
                if (newSize >= max) {
                    view.getLayoutParams().height = max;
                } else {
                    view.getLayoutParams().height = newSize;
                }
                view.requestLayout();

            });
            valueAnimator.setDuration(300);
            valueAnimator.start();
            direction = Direction.up;
        } else {
            //GoDown
            ValueAnimator valueAnimator = ValueAnimator.ofInt(nowSize, 0);
            valueAnimator.addUpdateListener(valueAnimator12 -> {
                int animatedValue = (int) valueAnimator12.getAnimatedValue();
                int valueDiff = nowSize - animatedValue;
                int newSize = view.getLayoutParams().height - valueDiff;
                if (newSize <= 0) {
                    view.getLayoutParams().height = 0;
                } else {
                    view.getLayoutParams().height = newSize;
                }
                view.requestLayout();

            });
            valueAnimator.setDuration(300);
            valueAnimator.start();
            direction = Direction.down;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    public Direction getDirection(float x1, float y1, float x2, float y2) {
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.fromAngle(angle);
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }


    public enum Direction {
        up,
        down,
        left,
        right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         * <p>
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        public static Direction fromAngle(double angle) {
            if (inRange(angle, 45, 135)) {
                return Direction.up;
            } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
                return Direction.right;
            } else if (inRange(angle, 225, 315)) {
                return Direction.down;
            } else {
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init  the initial bound
         * @param end   the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end) {
            return (angle >= init) && (angle < end);
        }
    }
}
