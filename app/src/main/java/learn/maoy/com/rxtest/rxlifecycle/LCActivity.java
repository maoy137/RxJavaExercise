package learn.maoy.com.rxtest.rxlifecycle;

import android.os.Bundle;
import android.util.Log;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import learn.maoy.com.rxtest.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LCActivity extends RxAppCompatActivity {

    private final static String TAG = "LCActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lc);

        bindOnDestroy();

    }

    private void bindOnDestroy() {
        // 循環發送數字
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.newThread())
                // 這個訂閱關係與Activity綁定，observable和activity生命週期同步
                // 沒加的話還是會一直發送訊息，訂閱不會解除
                // onDestroy()後自動解除
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> Log.d(TAG, Long.toString(aLong)));
    }

    private void bindOnStop() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                // 指定在onStop在onStop的時候取消訂閱
                .compose(bindUntilEvent(ActivityEvent.STOP))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> Log.d(TAG, Long.toString(aLong)));
    }
}
