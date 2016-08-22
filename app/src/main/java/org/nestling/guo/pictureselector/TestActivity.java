package org.nestling.guo.pictureselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        new PictureSelector.Creator(this)
                .count(8)
                .multi()
                .requestCode(10)
                .start(this);


    }
}
