package learn.maoy.com.rxtest.rxjava;

import java.util.List;

/**
 * Created by 10410303 on 2017/4/13.
 */

public class Book {
    private String name;
    private List<String> chapters;
    private List<Integer> pages;

    public void setName(String name) {
        this.name = name;
    }

    public void setChapters(List<String> chapters) {
        this.chapters = chapters;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public List<String> getChapters() {
        return chapters;
    }

    public List<Integer> getPages() {
        return pages;
    }
}
