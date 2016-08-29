package org.nestling.guo.pictureselector;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {

    private Button openBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.openBtn = (Button)this.findViewById(R.id.open);

        this.openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PictureSelector.Creator(TestActivity.this)
                        .count(8)
                        .multi()
                        .requestCode(10)
                        .start(TestActivity.this);
            }
        });
    }
}
