package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        binding.buttonShow.setOnClickListener(view -> binding.bottomSheet.showBottomSheet(0));
        binding.buttonShowATM.setOnClickListener(view -> binding.bottomSheet.showBottomSheet(1));
        binding.buttonHide.setOnClickListener(view -> binding.bottomSheet.hideBottomSheet());


    }

}
