package learn.maoy.com.rxtest.rxbinding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import learn.maoy.com.rxtest.R;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class BindingActivity extends AppCompatActivity {

    private Button button, button2;
    private EditText editText;
    private TextView textView;
    private CheckBox checkBox;
    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        compositeSubscription = new CompositeSubscription();

        init();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
        }
    }

    private void init() {
        initView();
        initClick();
    }

    private void initView() {
        button = (Button) findViewById(R.id.binding_btn);
        editText = (EditText) findViewById(R.id.binding_et);
        textView = (TextView) findViewById(R.id.binding_tv);
        button2 = (Button) findViewById(R.id.button2);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    private void initClick() {
        setBtnClickRx();
        setBtn2ClickRx();
        setBtnLongClickRx();
        setEditTextChangedRx();
        setBtn2EnableRx();
    }

    private void setBtnClickRx() {
        Subscription s = RxView.clicks(button)
                .throttleFirst(1, TimeUnit.SECONDS) //一秒只能點一下
                .subscribe(aVoid -> {
                    Intent intent = new Intent(BindingActivity.this, BindingListActivity.class);
                    startActivity(intent);
                });
        compositeSubscription.add(s);
    }

    private void setBtnLongClickRx() {
        Subscription s = RxView.longClicks(button)
                .subscribe(aVoid -> Toast.makeText(BindingActivity.this, "Long Click!", Toast.LENGTH_SHORT).show());
        compositeSubscription.add(s);
    }

    private void setEditTextChangedRx() {
        Subscription s = RxTextView.textChanges(editText)
                .map(charSequence -> new StringBuilder(charSequence).reverse().toString())
                .subscribe(charSequence -> textView.setText(charSequence));
        compositeSubscription.add(s);
    }

    private void setBtn2EnableRx() {
        Subscription s = RxCompoundButton.checkedChanges(checkBox)
                .subscribe(aBoolean -> {
                    button2.setEnabled(aBoolean);
                    button2.setTextColor(aBoolean ? getResources().getColor(R.color.colorPrimary, null): getResources().getColor(android.R.color.darker_gray, null));
                });
        compositeSubscription.add(s);
    }

    private void setBtn2ClickRx() {
        Subscription s = RxView.clicks(button2)
                .subscribe(aVoid -> Toast.makeText(BindingActivity.this, "Btn2 Click!", Toast.LENGTH_SHORT).show());
        compositeSubscription.add(s);
    }

    private void setEditTextChanged() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(new StringBuilder(s).reverse().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void scrollListener() {
        textView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });

        // v.s.

        RxView.scrollChangeEvents(textView)
                .subscribe(new Action1<ViewScrollChangeEvent>() {
                    @Override
                    public void call(ViewScrollChangeEvent viewScrollChangeEvent) {
                        viewScrollChangeEvent.oldScrollX();
                    }
                });
    }
}
