package learn.maoy.com.rxtest.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import learn.maoy.com.rxtest.R;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.AsyncOnSubscribe;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subscriptions.CompositeSubscription;

public class MyRxActivity extends AppCompatActivity {

    private final static String TAG = "MyRxActivity";
    private List<Integer> lists;
    private CompositeSubscription compositeSubscription;
    private TextView textView;
    private ScrollView scrollView;
    private StringBuilder s;

    private Observable<Long> intervalObservable;
    private Subscription intervalObserver1, intervalObserver2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rx);
        textView = (TextView) findViewById(R.id.rx_tv);
        scrollView = (ScrollView) findViewById(R.id.rx_tv_scroll);

        s = new StringBuilder();
        compositeSubscription = new CompositeSubscription();
        lists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lists.add(i);
        }

        findViewById(R.id.rx_clear).setOnClickListener(v -> clearMessage());
        findViewById(R.id.rx_just).setOnClickListener(v -> observableJust());
        findViewById(R.id.rx_from).setOnClickListener(v -> observableFrom());
        findViewById(R.id.rx_create).setOnClickListener(v -> observableCreate());
        findViewById(R.id.rx_interval).setOnClickListener(v -> observableInterval());
        findViewById(R.id.rx_interval_unsubscribe).setOnClickListener(v->observableIntervalClose());
        findViewById(R.id.rx_map).setOnClickListener(v -> observableMap());
        findViewById(R.id.rx_flat_map).setOnClickListener(v -> observableFlapMap());
        findViewById(R.id.rx_filter).setOnClickListener(v -> observableFilter());
        findViewById(R.id.rx_single_create).setOnClickListener(v -> singleCreate());
        findViewById(R.id.rx_single_just).setOnClickListener(v -> singleJust());
        findViewById(R.id.rx_async_subject).setOnClickListener(v -> asyncSubject());
        findViewById(R.id.rx_behavior_subject).setOnClickListener(v -> behaviorSubject());
        findViewById(R.id.rx_public_subject).setOnClickListener(v -> publishSubject());
        findViewById(R.id.rx_replay_subject).setOnClickListener(v -> replaySubject());
    }

    private void clearMessage() {
        s = new StringBuilder();
        textView.setText(s);
    }

    private void setMessage(final String tag, final String massage) {
        s.append(tag).append(": ").append(massage).append("\n");
        textView.setText(s);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void waitTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
    }

    // 1 : just
    private void observableJust() {
        Log.d(TAG, "observableJust");
        Subscriber<Integer> intSubscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.toString());
            }

            @Override
            public void onNext(Integer i) {
                setMessage("Just", i.toString());
            }
        };

        Subscription subscription = Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(intSubscriber);
        compositeSubscription.add(subscription);
    }

    // 2 : from
    private void observableFrom() {
        Subscription subscription = Observable.from(lists)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> setMessage("From", integer.toString()));
        compositeSubscription.add(subscription);
    }

    // 3 : create
    private void observableCreate() {
        Log.d(TAG, "observableCreate");
        Subscription subscription =
                Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        try {
                            for (int i = 1; i < 16; i++) {
                                subscriber.onNext(i);
                                Thread.sleep(500);
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Integer>() {

                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "Subscriber Sequence complete.");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "Subscriber Error: " + e.getMessage());
                            }

                            @Override
                            public void onNext(Integer integer) {
                                setMessage("Create", integer.toString());
                            }
                        });
        compositeSubscription.add(subscription);
    }

    private void observableInterval() {
        // cold
        intervalObservable = Observable.interval(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        intervalObserver1 = intervalObservable.subscribe(aLong -> setMessage("Interval First", aLong.toString()));
        waitTime(500);
        intervalObserver2 = intervalObservable.subscribe(aLong -> setMessage("Interval Second", aLong.toString()));

        compositeSubscription.add(intervalObserver1);
        compositeSubscription.add(intervalObserver2);
    }

    private void observableIntervalClose() {
        if (!intervalObserver1.isUnsubscribed()) intervalObserver1.unsubscribe();
        if (!intervalObserver2.isUnsubscribed()) intervalObserver2.unsubscribe();
    }

    private void observableIntervalToHot() {
        // publish() 把冷變熱
        // ConnectableObservable 如果不調用connect()則不會觸發數據流的執行
        ConnectableObservable<Long> cold = Observable.interval(10, TimeUnit.MILLISECONDS).publish();
        Subscription s = cold.connect();
        cold.subscribe(aLong -> Log.d(TAG, "First: " + aLong));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
        cold.subscribe(aLong -> Log.d(TAG, "Second: " + aLong));
        s.unsubscribe();
    }

    private void observableMap() {
        String ya = "YA";
        int hashCode = ya.hashCode();
        String result = Integer.toString(hashCode);
        Log.d(TAG, result);

        Subscription subscription = Observable.just("YA")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> s.hashCode())
                .map(integer -> Integer.toString(integer))
                .subscribe(s -> setMessage("Map", s));
        compositeSubscription.add(subscription);
    }

    private void futureSample() {
        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return null;
            }
        });
        Thread thread = new Thread(future);
    }

    private void iteratorSample(List<String> lists) {
        Iterator<String> i = lists.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }

    private void observableCreateAsync() {
        Log.d(TAG, "observableCreateAsync");
        Subscription subscription = Observable.create(new AsyncOnSubscribe<Void, Integer>() {
            @Override
            protected Void generateState() {
                return null;
            }

            @Override
            protected Void next(Void state, long requested, Observer<Observable<? extends Integer>> observer) {
                final Observable<Integer> asyncObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        try {
                            for (int i = 1; i < 5; i++) {
                                subscriber.onNext(i);
                                Thread.sleep(100);
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
                observer.onNext(asyncObservable);
                observer.onCompleted();
                return null;
            }
        }).observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Async Sequence complete.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Async Error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "Async Next: " + integer);
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void observableFlapMap() {

//        Observable.from(lists)
//                .flatMap(new Func1<Integer, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(Integer integer) {
//                        return Observable.just(Integer.toString(integer*2));
//                    }
//                }).subscribe(s -> {Log.d(TAG, s);});

        Book math = new Book();
        List<String> mathChapter = new ArrayList<>();
        mathChapter.add("math sin");
        mathChapter.add("math cos");
        List<Integer> mathPages = lists;
        math.setName("math");
        math.setChapters(mathChapter);
        math.setPages(mathPages);

        Book comic = new Book();
        List<String> comicChapter = new ArrayList<>();
        comicChapter.add("comic start");
        comicChapter.add("comic hit");
        comicChapter.add("comic continue");
        List<Integer> comicPages = lists;
        comic.setName("comic");
        comic.setChapters(comicChapter);
        comic.setPages(comicPages);

        Book[] books = {math, comic};
        Subscription subscription = Observable.from(books)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Book, Observable<String>>() {
                    @Override
                    public Observable<String> call(Book book) {
                        return Observable.from(book.getChapters());
                    }
                }).subscribe(s -> setMessage("FlatMap", s));
        compositeSubscription.add(subscription);
    }

    private void observableConcatMap() {

    }

    private void observableFilter() {

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i) > 4) {
                Log.d(TAG, Integer.toString(lists.get(i)));
            }
        }

        /*--------------------------------------*/

        Subscription subscription = Observable.from(lists)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 4;
                    }
                })
                .subscribe(integer -> setMessage("Filter", integer.toString()));
        compositeSubscription.add(subscription);
    }

    private void singleCreate() {
        Subscription subscription = Single.create(new Single.OnSubscribe<String>() {
            @Override
            public void call(SingleSubscriber<? super String> singleSubscriber) {
                try {
                    singleSubscriber.onSuccess("success");
                } catch (Exception e) {
                    singleSubscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                setMessage("Single Create", s);
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, error.toString());
            }
        });
        compositeSubscription.add(subscription);
    }

    private void singleJust() {
        Subscription subscription = Single.just("Hello world")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(s -> s.hashCode())
                .map(integer -> Integer.toString(integer))
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        setMessage("Single Just", s);
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.d(TAG, error.toString());
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void asyncSubject() {
        AsyncSubject<Integer> as = AsyncSubject.create();
        as.onNext(1);
        as.onNext(2);
        as.onNext(3);
        as.onCompleted();
        as.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                setMessage("asyncSubject", i.toString());
                // output 3
            }
        });
    }

    private void behaviorSubject() {
        BehaviorSubject<Integer> bs = BehaviorSubject.create(-1);
        // 這裡訂閱回傳-1, 1, 2, 3
        bs.onNext(1);
        // 這裡訂閱回傳1, 2, 3
        bs.onNext(2);
        // 這裡訂閱回傳2, 3
        bs.onNext(3);
        // 這裡訂閱回傳3
        bs.onCompleted();
        // 這裡訂閱不回傳
        bs.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                setMessage("behaviorSubject", i.toString());
            }
        });
    }

    private void publishSubject() {
        PublishSubject<Integer> ps = PublishSubject.create();
        // 這裡訂閱回傳1, 2, 3
        ps.onNext(1);
        // 這裡訂閱回傳2, 3
        ps.onNext(2);
        // 這裡訂閱回傳3
        ps.onNext(3);
        ps.onCompleted();
        // 這裡訂閱不回傳
        ps.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                setMessage("publishSubject", i.toString());
            }
        });
    }

    private void replaySubject() {
        ReplaySubject<Integer> rs = ReplaySubject.create();

        Observable.just(0)
                .subscribe(rs::onNext);

        rs.onNext(1);
        rs.onNext(2);
        rs.onNext(3);
        rs.onCompleted();
        rs.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                setMessage("replaySubject", i.toString());
                //無論何時訂閱，都會收到全部
            }
        });
    }

    private void schedulerSample() {
        Scheduler.Worker worker = Schedulers.newThread().createWorker();
        worker.schedule(new Action0() {
            @Override
            public void call() {
                /*your work*/
                // recurse until unsubscribed (schedule will do nothing if unsubscribed)
                worker.schedule(this);
            }
        });
        // some time later...
        worker.unsubscribe();
    }

    private void schedulers() {
        //background thread get data and main thread show data.

        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 發生在 IO thread
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的callback發生在main thread
                .subscribe(number -> Log.d(TAG, "number:" + number));

    }

    private void schedulersSwitch() {
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(integer -> integer.toString())
                .observeOn(Schedulers.io())
                .map(s -> s.hashCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> Log.d(TAG, integer.toString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
        }
    }
}
