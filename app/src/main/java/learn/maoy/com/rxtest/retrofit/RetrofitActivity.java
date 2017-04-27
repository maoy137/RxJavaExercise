package learn.maoy.com.rxtest.retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import learn.maoy.com.rxtest.R;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class RetrofitActivity extends AppCompatActivity {

    private final static String TAG = "RetrofitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CardAdapter mCardAdapter = new CardAdapter();
        mRecyclerView.setAdapter(mCardAdapter);

        Button bClear = (Button) findViewById(R.id.button_clear);
        Button bFetch = (Button) findViewById(R.id.button_fetch);
        bClear.setOnClickListener(v -> mCardAdapter.clear());

        List<String> githubList = new ArrayList<String>() {{
            add("linkedin");
            add("tumblr");
            add("square");
            add("google");
            add("stripe");
            add("angular");
            add("facebook");
            add("rails");
        }};

//        HttpService httpService = ServiceFactory.createRetrofitService(HttpService.class, HttpService.SERVICE_ENDPOINT);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpService.SERVICE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        HttpService httpService = retrofit.create(HttpService.class);

        bFetch.setOnClickListener(v -> {
            for (String login : githubList) {
                httpService.getUser(login)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Github>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Github github) {
                                mCardAdapter.addData(github);
                                Log.d(TAG, github.getLogin());
                            }
                        });
            }

//            httpService.getUser("facebook")
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<ResponseBody>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            try {
//                                Log.d(TAG, responseBody.string());
//                            } catch (IOException e) {
//                                Log.d(TAG, e.toString());
//                            }
//
//                        }
//                    });
        });
    }
}
