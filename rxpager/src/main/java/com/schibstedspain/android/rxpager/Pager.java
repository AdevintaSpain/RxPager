package com.schibstedspain.android.rxpager;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class Pager<RESULT, NEXT_PAGE_ID> {
  private final PublishSubject<NEXT_PAGE_ID> pageIds;
  private NEXT_PAGE_ID nextPageId;

  private final BiFunction<NEXT_PAGE_ID, RESULT, NEXT_PAGE_ID> pagingFunction;
  private final Function<NEXT_PAGE_ID, Observable<RESULT>> obtainFunction;
  private final BehaviorSubject<Boolean> isLoading;

  public Pager(NEXT_PAGE_ID firstPageId, BiFunction<NEXT_PAGE_ID, RESULT, NEXT_PAGE_ID> pagingFunction,
      Function<NEXT_PAGE_ID, Observable<RESULT>> obtainFunction) {
    this.nextPageId = firstPageId;
    this.pagingFunction = pagingFunction;
    this.obtainFunction = obtainFunction;

    pageIds = PublishSubject.create();
    isLoading = BehaviorSubject.create();
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
            obtainFunction.apply(next_page_id)
                .doOnSubscribe(onSubscribe -> isLoading.onNext(true))
                .doOnTerminate(() -> isLoading.onNext(false))
                .doOnDispose(() -> isLoading.onNext(false))
        )
        .doOnNext(page -> {
          nextPageId = pagingFunction.apply(nextPageId, page);
          if (nextPageId == null) {
            pageIds.onComplete();
          }
        });
  }
}