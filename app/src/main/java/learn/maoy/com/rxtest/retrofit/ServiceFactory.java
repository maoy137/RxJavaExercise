package learn.maoy.com.rxtest.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 10410303 on 2017/4/26.
 */

public class ServiceFactory {
    public static <T> T createRetrofitService(final Class<T> tClass, final String endPoint) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        T httpService = retrofit.create(tClass);
        return httpService;
    }
}
