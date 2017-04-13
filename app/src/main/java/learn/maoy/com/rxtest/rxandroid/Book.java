package learn.maoy.com.rxtest.rxandroid;

import java.util.List;

/**
 * Created by 10410303 on 2017/4/13.
 */

public class Book {
    private String name;
    private String[] chapters;
    private List<Integer> pages;

    public void setName(String name) {
        this.name = name;
    }

    public void setChapters(String[] chapters) {
        this.chapters = chapters;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public String[] getChapters() {
        return chapters;
    }

    public List<Integer> getPages() {
        return pages;
    }
}
