package learn.maoy.com.rxtest.rxbinding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import learn.maoy.com.rxtest.R;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BindingActivity extends AppCompatActivity {

    private Button button;
    private EditText editText;
    private TextView textView;
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
    }

    private void initClick() {
        setBtnClickRx();
        setEditTextChangedRx();
    }

    private void setBtnClickRx() {
        Subscription s = RxView.clicks(button)
                .subscribe(aVoid -> Toast.makeText(BindingActivity.this, "Click!", Toast.LENGTH_SHORT).show());
        compositeSubscription.add(s);
    }

    private void setEditTextChangedRx() {
        Subscription s = RxTextView.textChanges(editText)
                .map(charSequence -> new StringBuilder(charSequence).reverse().toString())
                .subscribe(charSequence -> textView.setText(charSequence));
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
}
