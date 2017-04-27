package learn.maoy.com.rxtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import learn.maoy.com.rxtest.news.NewsActivity;
import learn.maoy.com.rxtest.retrofit.RetrofitActivity;
import learn.maoy.com.rxtest.rxbinding.BindingActivity;
import learn.maoy.com.rxtest.rxjava.MyRxActivity;
import learn.maoy.com.rxtest.rxlifecycle.LCActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.go_news_btn).setOnClickListener(l -> goNewsPage());
        findViewById(R.id.go_rxjava_btn).setOnClickListener(l -> goRxJava());
        findViewById(R.id.go_rxbinding_btn).setOnClickListener(l -> goRxBinding());
        findViewById(R.id.go_rxlifecycle_btn).setOnClickListener(l -> goRxLifecycle());
        findViewById(R.id.go_retrofit_btn).setOnClickListener(l -> goRetrofit());
    }

    private void goNewsPage() {
        Intent intent = new Intent(MainActivity.this, NewsActivity.class);
        startActivity(intent);
    }

    private void goRxJava() {
        Intent intent = new Intent(MainActivity.this, MyRxActivity.class);
        startActivity(intent);
    }

    private void goRxBinding() {
        Intent intent = new Intent(MainActivity.this, BindingActivity.class);
        startActivity(intent);
    }

    private void goRxLifecycle() {
        Intent intent = new Intent(MainActivity.this, LCActivity.class);
        startActivity(intent);
    }

    private void goRetrofit() {
        Intent intent = new Intent(MainActivity.this, RetrofitActivity.class);
        startActivity(intent);
    }
}
