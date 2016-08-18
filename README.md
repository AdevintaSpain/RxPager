[ ![Download](https://api.bintray.com/packages/schibstedspain/maven/rxpager/images/download.svg) ](https://bintray.com/schibstedspain/maven/rxpager/_latestVersion)
# RxPager
RxPager is an Android library that helps handling paginated results in a reactive way

Is based on [this gist](https://gist.github.com/mttkay/24881a0ce986f6ec4b4d) from [@mttkay](https://gist.github.com/mttkay)

##Download
Grab the latest version from jCenter:

```gradle
dependencies {
  compile 'com.schibstedspain.android:rxpager:1.0.0'`
}
```

##Creation
`Pager pager = new Pager(initialPageToken, (oldPageToken, pageResult) -> pageResult.getNextPageToken(), token -> getPage(token) )`

or if you want to use offset instead of token:

`Pager pager = new Pager(0, (offset, pageResult) -> offset + pageResult.size(), offset -> getPageWithOffset(offset)`

The first 2 parameter are like rxjava [scan](http://reactivex.io/documentation/operators/scan.html).

The third parameter is a Func1 wich returns an Observable, is your repository/datasource call, and **you** are responsible to set subscribeOn() to this observable if you want it to be executed out of the main thread.

##Usage
To get the content, just subscribe to: `pager.getPageObservable()`
It will call onNext with the first page, and will continue giving to you the next pages when you call `pager.next()` and `complete` when the returned page have `null`as nextPageToken

To know if its loading, there is another observable available: `pager.getIsLoadingObservable()`
and last, there is `pager.hasNext()` wich returns a Boolean.

##Common data types
There is available a POJO called: TokenPage

`TokenPage(String nextPageToken, List<ITEM> results)`
being ITEM the type of your elements in the list.

##Example
This library includes tests to describe the behavior and also a Sample, in order to show you how it works I'll take the code from the Sample MainActivity.
```java
public class MainActivity extends AppCompatActivity {
  private final CompositeSubscription compositeSubscription = new CompositeSubscription();

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

    Subscription pageSubscription = pager.getPageObservable()
        .map(TokenPage::getResults)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter::addItems);
    compositeSubscription.add(pageSubscription);

    Subscription loadingSubscription = pager.getIsLoadingObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter::setIsLoading);
    compositeSubscription.add(loadingSubscription);
  }

  @Override
  protected void onDestroy() {
    compositeSubscription.clear();
    super.onDestroy();
  }
}
```

# License

```
Copyright 2016 Schibsted Classified Media Spain S.L.


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
