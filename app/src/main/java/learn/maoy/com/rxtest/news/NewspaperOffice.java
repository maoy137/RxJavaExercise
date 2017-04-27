package learn.maoy.com.rxtest.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10410303 on 2017/4/13.
 */

public class NewspaperOffice implements ISubject {

    private List<IObserver> lists;

    public NewspaperOffice() {
        lists = new ArrayList<>();
    }

    @Override
    public void registerObserver(IObserver observer) {
        lists.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        if (lists.indexOf(observer) >= 0) {
            lists.remove(observer);
        }
    }

    @Override
    public void notifyObservers(String content) {
        for (IObserver observer: lists) {
            observer.update(content);
        }
    }

    public void subscribeNewspaper(IObserver observer) {
        registerObserver(observer);
    }

    public void unsubscribeNewspaper(IObserver observer) {
        removeObserver(observer);
    }

    public boolean isSubscribeNewspaper(IObserver observer) {
        return lists.indexOf(observer) >= 0;
    }

    public void sendNewspaper(String content) {
        notifyObservers(content);
    }
}
