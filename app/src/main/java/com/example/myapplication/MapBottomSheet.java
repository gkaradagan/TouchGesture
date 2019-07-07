package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.view.GestureDetectorCompat;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.MapBottomSheetBinding;

public class MapBottomSheet extends FrameLayout {
    MapBottomSheetBinding binding;
    private MapBottomTouchGesture mapBottomTouchGesture;
    private int collapsedAdressHeight = 0;


    public MapBottomSheet(Context context) {
        super(context);
        init(null);
    }

    public MapBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MapBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MapBottomSheet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.map_bottom_sheet, this, true);
        binding.llRoot.setTag(-1);
    }

    public void hideBottomSheet() {
        if (binding.llRoot.getVisibility() != View.GONE) {
            int difference = 0;
            int tag = (int) binding.llRoot.getTag();
            if (tag == 1) {
                int lineCount = 1;
                int lastLineCount = binding.tvAdresValue.getLineCount();
                difference = (lastLineCount - lineCount) * binding.tvAdresValue.getLineHeight();
            }


            binding.llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int totalHeight = binding.llRoot.getMeasuredHeight() + difference;


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
                    binding.llAdress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    binding.llAdress.requestLayout();
                    binding.mesafeConstraint.setVisibility(View.VISIBLE);
                    binding.tvAdresValue.setText("");

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
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showBottomSheet(int type) { //0 Şube 1 ATM
        binding.llRoot.post(() -> {

            if (binding.llRoot.getVisibility() == View.GONE) {
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
                    GestureDetectorCompat mDetector = new GestureDetectorCompat(getContext(), mapBottomTouchGesture);
                    binding.llRoot.setOnTouchListener((v, motionEvent) -> {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            mapBottomTouchGesture.onActionUp();
                            return false;
                        }
                        return mDetector.onTouchEvent(motionEvent);
                    });
                } else {
                    binding.mesafeConstraint.setVisibility(View.GONE);
                    binding.llAdress.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    String newText = "asdlsadmölsaşdölsşadösalşdölsaşdölsaöldöasld sadlsaödlsaö salşdösalşdösalşdösalsaöm sa asldlsaö lşsaö lsa sa ösaldösalşdösalşdöasd";
                    int lineCount = binding.tvAdresValue.getLineCount();
                    binding.tvAdresValue.setText(newText);
                    int lastLineCount = binding.tvAdresValue.getLineCount();
                    int difference = (lastLineCount - lineCount) * binding.tvAdresValue.getLineHeight();

                    binding.llRoot.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int totalHeight = binding.llRoot.getMeasuredHeight() + difference;
                    Animation animation = new TranslateAnimation(0, 0, totalHeight, 0);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    animation.setDuration(300);
                    binding.llRoot.setOnTouchListener((view, motionEvent) -> true);
                    binding.llRoot.setVisibility(View.VISIBLE);
                    binding.llRoot.startAnimation(animation);
                    binding.gpsButton.startAnimation(animation);
                }
            } else {
                //Data Değiştir
                String newText = "Gorkem Karadoğan";
                int lineCount = binding.tvAdresValue.getLineCount();
                binding.tvAdresValue.setText(newText);
                int tag = (int) binding.llRoot.getTag();
                if (tag == 0) {
                    int lastLineCount = binding.tvAdresValue.getLineCount();
                    int difference = (lastLineCount - lineCount) * binding.tvAdresValue.getLineHeight();
                    if (lastLineCount > lineCount) {
                        collapsedAdressHeight = collapsedAdressHeight + Math.abs(difference);
                    } else {
                        collapsedAdressHeight = collapsedAdressHeight - Math.abs(difference);
                    }

                    if (mapBottomTouchGesture != null) {
                        mapBottomTouchGesture.setMax(collapsedAdressHeight);
                        if (mapBottomTouchGesture.getDirection() != null && mapBottomTouchGesture.getDirection() == MapBottomTouchGesture.Direction.up) {
                            binding.llAdress.getLayoutParams().height = collapsedAdressHeight; //Aciksa !!
                            binding.llAdress.requestLayout();
                        }
                    }
                }
            }

        });
    }
}
