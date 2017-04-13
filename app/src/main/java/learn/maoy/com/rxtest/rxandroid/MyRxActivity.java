package learn.maoy.com.rxtest.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeSubscription = new CompositeSubscription();
        lists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            lists.add(i);
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
                Log.d(TAG, "onNext : " + i);
            }
        };

        Subscription subscription = Observable.just(1, 2, 3, 4)
                .subscribe(intSubscriber);
        compositeSubscription.add(subscription);
    }

    // 2 : from
    private void observableFrom() {
        Subscription subscription = Observable.from(lists)
                .subscribe(integer -> {
                    Log.d(TAG, "List " + integer);
                });
        compositeSubscription.add(subscription);
    }

    // 3 : create
    private void observableCreate() {
        Log.d(TAG, "observableCreate");

        Subscription subscription = Observable.create(new Observable.OnSubscribe<Integer>() {
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
        }).subscribe(new Subscriber<Integer>() {

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
                Log.d(TAG, "Subscriber Next: " + integer);
            }
        });
        compositeSubscription.add(subscription);
    }

    private void observableInterval() {
        // cold
        Observable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS);
        cold.subscribe(aLong -> {Log.d(TAG, "First: " + aLong);});
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
        cold.subscribe(aLong -> {Log.d(TAG, "Second: " + aLong);});
    }

    private void observableIntervalToHot() {
        // publish() 把冷變熱
        // ConnectableObservable 如果不調用connect()則不會觸發數據流的執行
        ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        Subscription s = cold.connect();
        cold.subscribe(aLong -> {Log.d(TAG, "First: " + aLong);});
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.d(TAG, e.toString());
        }
        cold.subscribe(aLong -> {Log.d(TAG, "Second: " + aLong);});
        s.unsubscribe();
    }

    private void observableMap() {
        String ya = "YA";
        int hashCode = ya.hashCode();
        String result = Integer.toString(hashCode);
        Log.d(TAG, result);

        Subscription subscription = Observable.just("YA")
                .map(s -> s.hashCode())
                .map(integer -> Integer.toString(integer))
                .subscribe(s -> {
                    Log.d(TAG, s);
                });
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
        String[] mathChapter = {"sin", "cos"};
        List<Integer> mathPages = lists;
        math.setName("math");
        math.setChapters(mathChapter);
        math.setPages(mathPages);

        Book comic = new Book();
        String[] comicChapter = {"start", "hit", "continue"};
        List<Integer> comicPages = lists;
        comic.setName("comic");
        comic.setChapters(comicChapter);
        comic.setPages(comicPages);

        Book[] books = {math, comic};
        Subscription subscription = Observable.from(books)
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<Book, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Book book) {
                        return Observable.from(book.getPages());
                    }
                }).subscribe(i -> {
                    Log.d(TAG, Integer.toString(i));
                });
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
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 4;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, Integer.toString(integer));
                    }
                });
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
        }).subscribe(new SingleSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
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
                .map(s -> s.hashCode())
                .map(integer -> Integer.toString(integer))
                .subscribe(new SingleSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, s);
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
                Log.d(TAG, Integer.toString(i));
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
                Log.d(TAG, Integer.toString(i));
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
                Log.d(TAG, Integer.toString(i));
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
                Log.d(TAG, Integer.toString(i));
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
                .subscribe(number -> {
                    Log.d(TAG, "number:" + number);
                });

    }

    private void schedulersSwitch() {
        Observable.just(1, 2, 3, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map(integer -> integer.toString())
                .observeOn(Schedulers.io())
                .map(s -> s.hashCode())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    Log.d(TAG, integer.toString());
                });

    }

    private void rxAndroid() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.unsubscribe();
        }
    }
}
