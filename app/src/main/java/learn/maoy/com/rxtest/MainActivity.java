package learn.maoy.com.rxtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import learn.maoy.com.rxtest.news.NewsActivity;
import learn.maoy.com.rxtest.rxandroid.MyRxActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.go_news_btn).setOnClickListener(l -> goNewsPage());
        findViewById(R.id.go_observer_btn).setOnClickListener(l -> goRxJava());
        findViewById(R.id.go_retrofit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NextActivity.class);
                startActivity(intent);
            }
        });
    }

    private void goNewsPage() {
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        startActivity(intent);
    }

    private void goRxJava() {
        Intent intent = new Intent(MainActivity.this, MyRxActivity.class);
        startActivity(intent);
    }
}
