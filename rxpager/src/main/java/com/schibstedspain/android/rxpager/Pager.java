package com.schibstedspain.android.rxpager;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class Pager<RESULT, NEXT_PAGE_ID> {
  private PublishSubject<NEXT_PAGE_ID> pageIds;
  private NEXT_PAGE_ID nextPageId;

  private final Func2<NEXT_PAGE_ID, RESULT, NEXT_PAGE_ID> pagingFunction;
  private final Func1<NEXT_PAGE_ID, Observable<RESULT>> obtainFunction;
  private BehaviorSubject<Boolean> isLoading = BehaviorSubject.create(false);

  public Pager(NEXT_PAGE_ID firstPageId, Func2<NEXT_PAGE_ID, RESULT, NEXT_PAGE_ID> pagingFunction,
      Func1<NEXT_PAGE_ID, Observable<RESULT>> obtainFunction) {
    this.nextPageId = firstPageId;
    this.pagingFunction = pagingFunction;
    this.obtainFunction = obtainFunction;

    pageIds = PublishSubject.create();
  }

  public Observable<RESULT> getPageObservable() {
    return Observable.defer(() -> page(nextPageId));
  }

  public Observable<Boolean> getIsLoadingObservable() {
    return isLoading.distinctUntilChanged();
  }

  public boolean hasNext() {
    return nextPageId != null;
  }

  public void next() {
    if (pageIds.hasObservers() && hasNext() && !isLoading.getValue()) {
      pageIds.onNext(nextPageId);
    }
  }

  private Observable<RESULT> page(final NEXT_PAGE_ID source) {
    return pageIds.startWith(source)
        .concatMap(next_page_id ->
            obtainFunction.call(next_page_id)
                .doOnSubscribe(() -> isLoading.onNext(true))
                .doOnNext(next -> isLoading.onNext(false))
        )
        .doOnNext(page -> {
          nextPageId = pagingFunction.call(nextPageId, page);
          if (nextPageId == null) {
            pageIds.onCompleted();
          }
        });
  }
}