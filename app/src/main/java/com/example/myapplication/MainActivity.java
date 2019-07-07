package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private int collapsedAdressHeight = 0;
    private MapBottomTouchGesture mapBottomTouchGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        binding.buttonShow.setOnClickListener(view -> showBottomSheet(0));
        binding.buttonShowATM.setOnClickListener(view -> showBottomSheet(1));
        binding.buttonHide.setOnClickListener(view -> hideBottomSheet());
        binding.buttonChangeText.setOnClickListener(view -> {
            String newText = "Gorkem Karadoğan";
            int lineCount = binding.tvAdresValue.getLineCount();
            binding.tvAdresValue.setText(newText);
            int lastLineCount = binding.tvAdresValue.getLineCount();
            int difference = (lastLineCount - lineCount) * binding.tvAdresValue.getLineHeight();
            if (lastLineCount > lineCount) {
                collapsedAdressHeight = collapsedAdressHeight + Math.abs(difference);
            } else {
                collapsedAdressHeight = collapsedAdressHeight - Math.abs(difference);
            }

            if (mapBottomTouchGesture != null)
                mapBottomTouchGesture.setMax(collapsedAdressHeight);

            int tag = (int) binding.llRoot.getTag();
            if (tag == 0 && mapBottomTouchGesture.getDirection() != null && mapBottomTouchGesture.getDirection() == MapBottomTouchGesture.Direction.up) {
                binding.llAdress.getLayoutParams().height = collapsedAdressHeight; //Aciksa !!
                binding.llAdress.requestLayout();
            }
        });

    }

    public void hideBottomSheet() {
        binding.llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = binding.llRoot.getMeasuredHeight();

        Animation animation = new TranslateAnimation(0, 0, 0, totalHeight);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.llRoot.clearAnimation();
                binding.llRoot.setVisibility(View.GONE);
                binding.tvAdresValue.setText("");
                binding.llAdress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                binding.llAdress.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation gpsAnim = new TranslateAnimation(0, 0, 0, totalHeight);
        gpsAnim.setDuration(300);
        gpsAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.gpsButton.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.llRoot.startAnimation(animation);
        binding.gpsButton.startAnimation(gpsAnim);
        mapBottomTouchGesture = null;

    }

    @SuppressLint("ClickableViewAccessibility")
    public void showBottomSheet(int type) { //0 Şube 1 ATM
        binding.llRoot.post(() -> {
            binding.llRoot.setTag(type);

            if (type == 0) {
                binding.mesafeConstraint.setVisibility(View.VISIBLE);
                binding.llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int totalHeight = binding.llRoot.getMeasuredHeight();

                binding.llAdress.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                collapsedAdressHeight = binding.llAdress.getMeasuredHeight();

                binding.llAdress.getLayoutParams().height = 0; //Daha sonra açıcaz
                binding.llAdress.requestLayout();

                Animation animation = new TranslateAnimation(0, 0, totalHeight - collapsedAdressHeight, 0);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //Adres Burada Güncellenecek !
                        String newText = "asdlsadmölsaşdölsşadösalşdölsaşdölsaöldöasld sadlsaödlsaö salşdösalşdösalşdösalsaöm sa asldlsaö lşsaö lsa sa ösaldösalşdösalşdöasd";
                        int lineCount = binding.tvAdresValue.getLineCount();
                        binding.tvAdresValue.setText(newText);
                        int lastLineCount = binding.tvAdresValue.getLineCount();
                        int difference = (lastLineCount - lineCount) * binding.tvAdresValue.getLineHeight();
                        if (lastLineCount > lineCount) {
                            collapsedAdressHeight = collapsedAdressHeight + Math.abs(difference);
                        } else {
                            collapsedAdressHeight = collapsedAdressHeight - Math.abs(difference);
                        }
                        mapBottomTouchGesture.setMax(collapsedAdressHeight);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animation.setDuration(300);
                binding.llRoot.setVisibility(View.VISIBLE);
                binding.llRoot.startAnimation(animation);
                binding.gpsButton.startAnimation(animation);


                mapBottomTouchGesture = new MapBottomTouchGesture(binding.llAdress, collapsedAdressHeight);
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
            } else {
                binding.mesafeConstraint.setVisibility(View.GONE);
                binding.llAdress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                binding.llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int totalHeight = binding.llRoot.getMeasuredHeight();
                String newText = "asdlsadmölsaşdölsşadösalşdölsaşdölsaöldöasld sadlsaödlsaö salşdösalşdösalşdösalsaöm sa asldlsaö lşsaö lsa sa ösaldösalşdösalşdöasd";
                binding.tvAdresValue.setText(newText);
                Animation animation = new TranslateAnimation(0, 0, totalHeight, 0);
                animation.setDuration(300);
                binding.llRoot.setOnTouchListener((view, motionEvent) -> true);
                binding.llRoot.setVisibility(View.VISIBLE);
                binding.llRoot.startAnimation(animation);
                binding.gpsButton.startAnimation(animation);
            }

        });
    }

    private int getTextHeight(TextView textView, String text) {
        textView.setText(text);
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }
}
