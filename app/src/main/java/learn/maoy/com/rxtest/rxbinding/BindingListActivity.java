package learn.maoy.com.rxtest.rxbinding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import learn.maoy.com.rxtest.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class BindingListActivity extends AppCompatActivity {

    private EditText search;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_list);

        search = (EditText) findViewById(R.id.search_list_et);
        listView = (ListView) findViewById(R.id.search_content_list);

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        listView.setAdapter(adapter);

        RxTextView.textChanges(search)
                .debounce(600, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(Schedulers.io())
                .map(key -> {
                    List<String> dataList = new ArrayList<>();
                    if (!TextUtils.isEmpty(key)) {
                        for (String s : list) {
                            if (s != null) {
                                if (s.contains(key)) dataList.add(s);
                            }
                        }
                    } else {
                        dataList = list;
                    }
                    return dataList;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    adapter.clear();
                    adapter.addAll(strings);
                    adapter.notifyDataSetChanged();
                });
    }
}
