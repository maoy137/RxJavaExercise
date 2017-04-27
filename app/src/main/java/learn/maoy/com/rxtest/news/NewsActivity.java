package learn.maoy.com.rxtest.news;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import learn.maoy.com.rxtest.R;

public class NewsActivity extends AppCompatActivity {
    private TextView minMessage, meiMessage;
    private Button minSubscribe, meiSubscribe, send;
    private NewspaperOffice newspaperOffice;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        minMessage = (TextView) findViewById(R.id.min_message_tv);
        meiMessage = (TextView) findViewById(R.id.mei_message_tv);
        minSubscribe = (Button) findViewById(R.id.min_subscribe_btn);
        meiSubscribe = (Button) findViewById(R.id.mei_subscribe_btn);
        send = (Button) findViewById(R.id.send_btn);

        newspaperOffice = new NewspaperOffice();
        final Customer min = new Customer(minMessage);
        final Customer mei = new Customer(meiMessage);

        minSubscribe.setOnClickListener(l -> subscribeClick(min));
        meiSubscribe.setOnClickListener(l -> subscribeClick(mei));
        send.setOnClickListener(l -> sendClick());
    }

    private void subscribeClick(Customer customer) {
        if (newspaperOffice.isSubscribeNewspaper(customer)) {
            newspaperOffice.unsubscribeNewspaper(customer);
        } else {
            newspaperOffice.subscribeNewspaper(customer);
        }
    }

    private void sendClick() {
        newspaperOffice.sendNewspaper("News " + count);
        count++;
    }
}
