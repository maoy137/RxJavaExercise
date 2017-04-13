package learn.maoy.com.rxtest.news;

import android.widget.TextView;

/**
 * Created by 10410303 on 2017/4/13.
 */

public class Customer implements IObserver {

    private TextView textView;

    public Customer(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void update(String message) {
        textView.setText(message);
    }
}