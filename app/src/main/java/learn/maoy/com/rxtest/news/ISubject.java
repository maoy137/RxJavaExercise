package learn.maoy.com.rxtest.news;

/**
 * Created by 10410303 on 2017/4/13.
 */

public interface ISubject {
    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(String content);
}