package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private int collapsedAdressHeight = 0;
    private int totalHeight = 0;
    private int totalShowingPart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        ViewTreeObserver vtoAdress = binding.adressConstraint.getViewTreeObserver();
        vtoAdress.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                binding.adressConstraint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                collapsedAdressHeight = binding.adressConstraint.getMeasuredHeight();
            }
        });


        ViewTreeObserver vto = binding.llRoot.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                binding.adressConstraint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                totalShowingPart = binding.viewTop.getHeight() + binding.gpsButton.getHeight();
                totalHeight = binding.llRoot.getMeasuredHeight();
                binding.llRoot.getLayoutParams().height = 0;
                binding.llRoot.requestLayout();

            }
        });

        binding.buttonShow.setOnClickListener(view -> showBottomSheet());

    }

    public void hideBottomSheet() {
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showBottomSheet() {
        binding.llRoot.post(() -> {



            binding.adressConstraint.getLayoutParams().height = 0; //Daha sonra açıcaz
            binding.adressConstraint.requestLayout();

            final int collapsedHeight = totalHeight - collapsedAdressHeight;
            Animation openAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    binding.llRoot.getLayoutParams().height = (int) (collapsedHeight * interpolatedTime);
                    binding.llRoot.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            openAnimation.setDuration(300);
            binding.llRoot.startAnimation(openAnimation);


            final MapBottomTouchGesture mapBottomTouchGesture = new MapBottomTouchGesture(binding.adressConstraint, collapsedAdressHeight);
            GestureDetectorCompat mDetector = new GestureDetectorCompat(this, mapBottomTouchGesture);
            binding.llRoot.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mapBottomTouchGesture.onActionUp();
                        return false;
                    }
                    return mDetector.onTouchEvent(motionEvent);
                }
            });
        });
    }

}
