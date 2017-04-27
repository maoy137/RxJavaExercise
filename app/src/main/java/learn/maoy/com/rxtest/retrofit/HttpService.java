package learn.maoy.com.rxtest.retrofit;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 10410303 on 2017/4/26.
 */

public interface HttpService {
    String SERVICE_ENDPOINT = "https://api.github.com/";

    @GET("users/{login}")
    Observable<Github> getUser(@Path("login") String login);

//    @GET("users/{login}")
//    Observable<ResponseBody> getUser(@Path("login") String login);
}
