package com.schibstedspain.android.rxpager;

import com.schibstedspain.android.rxpager.tokenpage.TokenPage;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PagerTest {
  private static final TokenPage<String> SINGLE_PAGE = new TokenPage<>(null, Collections.singletonList("first element"));
  private static final TokenPage<String> FIRST_PAGE = new TokenPage<>("1", Collections.singletonList("first page element"));
  private static final TokenPage<String> SECOND_PAGE = new TokenPage<>("2", Collections.singletonList("second page element"));
  private static final TokenPage<String> THIRD_PAGE = new TokenPage<>(null, Collections.singletonList("last page element"));
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  private Pager<TokenPage<String>, String> pager;
  @Mock
  private Function<String, Observable<TokenPage<String>>> getPageMock;

  @Before
  public void setUp() {
    pager = new Pager<>("", (s, stringTokenPage) -> stringTokenPage.getNextPageToken(), getPageMock);
  }

  @Test
  public void getPageObservableShouldReturnJustOnePageIfThereIsOnlyOne() throws Exception {
    givenThereIsOnePage();

    TokenPage<String> firstPage = pager.getPageObservable().blockingSingle();

    verify(getPageMock).apply(anyString());
    assertEquals(SINGLE_PAGE, firstPage);
  }

  @Test
  public void getPageObservableShouldReturnJustTheFirstPageEvenIfThereAreMore() throws Exception {
    givenThereAreThreePages();

    TokenPage<String> page = pager.getPageObservable().blockingFirst();

    verify(getPageMock).apply(anyString());
    assertEquals(FIRST_PAGE, page);
  }

  @Test
  public void hasMoreShouldReturnFalseWhenThereAreNoMore() throws Exception {
    givenThereIsOnePage();
    pager.getPageObservable().blockingSingle();

    boolean hasNext = pager.hasNext();

    assertFalse(hasNext);
  }

  @Test
  public void hasMoreShouldReturnTrueWhenThereAreMore() throws Exception {
    givenThereAreThreePages();
    pager.getPageObservable().blockingFirst();

    boolean hasNext = pager.hasNext();

    assertTrue(hasNext);
  }

  @Test
  public void nextShouldGiveTheSecondPage() throws Exception {
    givenThereAreThreePages();
    TestObserver<TokenPage<String>> testObserver = new TestObserver<>();

    waitUntilLoaded(() -> pager.getPageObservable().subscribe(testObserver));
    waitUntilLoaded(() -> pager.next());

    verify(getPageMock, times(2)).apply(anyString());
    testObserver.assertValueSequence(Arrays.asList(FIRST_PAGE, SECOND_PAGE));
  }

  private void waitUntilLoaded(Action action) throws Exception {
    TestObserver<Boolean> loadingSubscriber = pager.getIsLoadingObservable().take(3).test();
    action.run();
    loadingSubscriber.awaitTerminalEvent(2, TimeUnit.SECONDS);
  }

  @Test
  public void isLoadingObservableShouldReturnFalseBeforeAskingForOnePage() {

    Boolean isLoadingBeforeSubscribe = pager.getIsLoadingObservable().blockingFirst();

    assertFalse(isLoadingBeforeSubscribe);
  }

  @Test
  public void isLoadingObservableShouldReturnTrueWhileIsWaitingForAPage() throws Exception {
    PublishSubject<TokenPage<String>> getPageSubject = PublishSubject.create();
    given(getPageMock.apply(anyString())).willReturn(getPageSubject);
    pager.getPageObservable().subscribe();

    Boolean isLoading = pager.getIsLoadingObservable().blockingFirst();

    assertTrue(isLoading);
  }

  @Test
  public void isLoadingObservableShouldReturnFalseAfterReceivingOnePage() throws Exception {
    given(getPageMock.apply(anyString())).willReturn(Observable.just(FIRST_PAGE));
    pager.getPageObservable().take(1).subscribe();

    Boolean isLoading = pager.getIsLoadingObservable().blockingFirst();

    assertFalse(isLoading);
  }

  @Test
  public void isLoadingObservableShouldReturnFalseAfterAFailure() throws Exception {
    givenGetPageFails();
    TestObserver<TokenPage<String>> testSubscriber = pager.getPageObservable().test();
    testSubscriber.awaitTerminalEvent();

    Boolean isLoading = pager.getIsLoadingObservable().blockingFirst();

    testSubscriber.assertNotComplete();
    assertFalse(isLoading);
  }

  private void givenThereIsOnePage() throws Exception {
    given(getPageMock.apply(anyString())).willReturn(Observable.just(SINGLE_PAGE).subscribeOn(Schedulers.io()));
  }

  private void givenThereAreThreePages() throws Exception {
    given(getPageMock.apply("")).willReturn(
        Observable.just(FIRST_PAGE).subscribeOn(Schedulers.io()).delay(40, TimeUnit.MILLISECONDS));
    given(getPageMock.apply("1")).willReturn(
        Observable.just(SECOND_PAGE).subscribeOn(Schedulers.io()).delay(40, TimeUnit.MILLISECONDS));
    given(getPageMock.apply("2")).willReturn(Observable.just(THIRD_PAGE).subscribeOn(Schedulers.io()));
  }

  private void givenGetPageFails() throws Exception {
    given(getPageMock.apply(anyString())).willReturn(Observable.error(new Exception()));
  }
}
