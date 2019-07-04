package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    RelativeLayout llHeader;
    LinearLayout llRoot;
    LinearLayout llContent;
    LinearLayout footer;

    Button buttonShow, buttonHide;
    private int contentHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llRoot = findViewById(R.id.llRoot);
        llHeader = findViewById(R.id.rlHeader);
        llContent = findViewById(R.id.llContent);
        footer = findViewById(R.id.footer);
        buttonShow = findViewById(R.id.buttonShow);
        buttonHide = findViewById(R.id.buttonHide);


        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });


        buttonHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePopup();
            }
        });

    }

    private GestureDetectorCompat mDetector;

    private void hidePopup() {
        llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = llRoot.getMeasuredHeight();

        Animation animation = new TranslateAnimation(0, 0, 0, totalHeight);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llRoot.setVisibility(View.INVISIBLE);
                llContent.getLayoutParams().height = contentHeight;
                llContent.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        llRoot.startAnimation(animation);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showPopup() {
        llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = llRoot.getMeasuredHeight();

        contentHeight = llContent.getHeight();
        llContent.getLayoutParams().height = 0; //Daha sonra açıcaz
        llContent.requestLayout();

        Animation animation = new TranslateAnimation(0, 0, totalHeight - contentHeight, 0);
        animation.setDuration(300);
        llRoot.setVisibility(View.VISIBLE);
        llRoot.startAnimation(animation);

        mDetector = new GestureDetectorCompat(this, new TouchGesture(llContent, contentHeight));
        llRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final int nowSize = llContent.getLayoutParams().height;
                    if (nowSize >= contentHeight / 2) {
                        //GoUp
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(nowSize, contentHeight);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int animatedValue = (int) valueAnimator.getAnimatedValue();
                                int valueDiff = animatedValue - nowSize;
                                int newSize = llContent.getLayoutParams().height + valueDiff;
                                if (newSize >= contentHeight) {
                                    llContent.getLayoutParams().height = contentHeight;
                                } else {
                                    llContent.getLayoutParams().height = newSize;
                                }
                                llContent.requestLayout();

                            }
                        });
                        valueAnimator.setDuration(300);
                        valueAnimator.start();
                    } else {
                        //GoDown
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(nowSize, 0);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int animatedValue = (int) valueAnimator.getAnimatedValue();
                                int valueDiff = nowSize - animatedValue;
                                int newSize = llContent.getLayoutParams().height - valueDiff;
                                if (newSize <= 0) {
                                    llContent.getLayoutParams().height = 0;
                                } else {
                                    llContent.getLayoutParams().height = newSize;
                                }
                                llContent.requestLayout();

                            }
                        });
                        valueAnimator.setDuration(300);
                        valueAnimator.start();
                    }
                    return false;
                }
                return mDetector.onTouchEvent(motionEvent);
            }
        });
    }
}
