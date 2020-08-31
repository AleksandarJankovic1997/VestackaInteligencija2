package com.example.vestackainteligencija2;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ImageClassificationTensorFlowFragment imageClassificationTensorFlowFragment=new ImageClassificationTensorFlowFragment();
    private AnimalCustomModelFragment animalCustomModelFragment=new AnimalCustomModelFragment();

    private FragmentManager fm;
    private FragmentTransaction ft;

    private int trenutniTab=0;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout=findViewById(R.id.tablayout_id);
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.activityFrameLayout,imageClassificationTensorFlowFragment).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(trenutniTab!=tab.getPosition()){
                    if(tab.getPosition()==0){
                        ft=fm.beginTransaction();
                        ft.replace(R.id.activityFrameLayout,imageClassificationTensorFlowFragment);
                        trenutniTab=0;
                    }
                   else if(tab.getPosition()==1) {
                        ft=fm.beginTransaction();
                        ft.replace(R.id.activityFrameLayout,animalCustomModelFragment);
                        trenutniTab=1;
                    }
                   ft.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(trenutniTab==0){
            imageClassificationTensorFlowFragment.onActivityResult(requestCode,resultCode,data);
        }
        else{
            animalCustomModelFragment.onActivityResult(requestCode,resultCode,data);
        }

    }
}