package learn.maoy.com.rxtest.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10410303 on 2017/4/26.
 */

public class Github {
    private @SerializedName("login") String login;
    private @SerializedName("blog") String blog;
    private @SerializedName("public_repos") int public_repos;

    public String getLogin() {
        return login;
    }
    public String getBlog() {
        return blog;
    }
    public int getRepos() {
        return public_repos;
    }
}
