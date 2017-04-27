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

    public void setLogin(String login) {
        this.login = login;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public int getRepos() {
        return public_repos;
    }

    public void setRepos(int repos) {
        this.public_repos = repos;
    }
}
