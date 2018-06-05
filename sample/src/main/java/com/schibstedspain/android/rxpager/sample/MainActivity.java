package com.schibstedspain.android.rxpager.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import com.schibstedspain.android.rxpager.Pager;
import com.schibstedspain.android.rxpager.sample.adapter.Adapter;
import com.schibstedspain.android.rxpager.sample.datasource.DataSource;
import com.schibstedspain.android.rxpager.sample.databinding.MainActivityBinding;
import com.schibstedspain.android.rxpager.tokenpage.TokenPage;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
  private final CompositeDisposable compositeSubscription = new CompositeDisposable();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DataSource dataSource = new DataSource();
    Pager<TokenPage<String>, String> pager = new Pager<>(
            DataSource.FIRST_PAGE_TOKEN,
            (oldToken, tokenPage) -> tokenPage.getNextPageToken(),
            dataSource::getPage);

    Adapter adapter = new Adapter();
    MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
    binding.list.setLayoutManager(new LinearLayoutManager(this));
    binding.list.setAdapter(adapter);
    binding.list.addOnScrollListener(new OnScrollToBottomListener(pager::next));

    Disposable pageSubscription = pager.getPageObservable()
        .map(TokenPage::getResults)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter::addItems, Throwable::printStackTrace);
    compositeSubscription.add(pageSubscription);

    Disposable loadingSubscription = pager.getIsLoadingObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter::setIsLoading, Throwable::printStackTrace);
    compositeSubscription.add(loadingSubscription);
  }

  @Override
  protected void onDestroy() {
    compositeSubscription.clear();
    super.onDestroy();
  }
}
