package learn.maoy.com.rxtest.rxlifecycle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import learn.maoy.com.rxtest.R;

public class LCActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc);
    }
}
