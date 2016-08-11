# RxPager
RxPager is an Android library that helps handling paginated results in a reactive way

Is based on [this link](https://gist.github.com/mttkay/24881a0ce986f6ec4b4d) from [@mttkay](https://gist.github.com/mttkay)

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
being ITEM the tipe of your elements in the list.
