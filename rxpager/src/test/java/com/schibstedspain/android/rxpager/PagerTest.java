package com.schibstedspain.android.rxpager;

import com.schibstedspain.android.rxpager.tokenpage.TokenPage;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PagerTest {
  private static final TokenPage<String> FIRST_PAGE = new TokenPage<>("1", Collections.singletonList("first page element"));
  private static final TokenPage<String> SECOND_PAGE = new TokenPage<>("2", Collections.singletonList("second page element"));
  private static final TokenPage<String> THIRD_PAGE = new TokenPage<>(null, Collections.singletonList("last page element"));
  private static final TokenPage<String> SINGLE_PAGE = new TokenPage<>(null, Collections.singletonList("first element"));
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  private Pager<TokenPage<String>, String> pager;
  @Mock
  private Func1<String, Observable<TokenPage<String>>> getPageMock;

  @Before
  public void setUp() {
    pager = new Pager<>(null, (s, stringTokenPage) -> stringTokenPage.getNextPageToken(), getPageMock);
  }

  @Test
  public void getPageObservableShoulReturnJustOnePageIfThereIsOnlyOne() throws Exception {
    givenThereIsOnePage();

    TokenPage<String> firstPage = pager.getPageObservable()
        .toBlocking()
        .single();

    verify(getPageMock).call(anyString());
    assertEquals(SINGLE_PAGE, firstPage);
  }

  @Test
  public void getPageObservableShouldReturnJustTheFirstPageEvenIfThereAreMore() throws Exception {
    givenThereAreThreePages();

    TokenPage<String> page = pager.getPageObservable().toBlocking().first();

    verify(getPageMock).call(anyString());
    assertEquals(FIRST_PAGE, page);
  }

  @Test
  public void hasMoreShouldReturnFalseWhenThereAreNoMore() throws Exception {
    givenThereIsOnePage();
    pager.getPageObservable()
        .toBlocking()
        .single();

    boolean hasNext = pager.hasNext();

    assertFalse(hasNext);
  }

  @Test
  public void hasMoreShouldReturnTrueWhenThereAreMore() throws Exception {
    givenThereAreThreePages();
    pager.getPageObservable()
        .toBlocking()
        .first();

    boolean hasNext = pager.hasNext();

    assertTrue(hasNext);
  }

  @Test
  public void nextShouldGiveTheSecondPage() throws Exception {
    givenThereAreThreePages();
    TestSubscriber<TokenPage<String>> testSubscriber = new TestSubscriber<>();
    pager.getPageObservable().subscribe(testSubscriber);

    pager.next();

    verify(getPageMock, times(2)).call(anyString());
    testSubscriber.assertReceivedOnNext(Arrays.asList(FIRST_PAGE, SECOND_PAGE));
  }

  @Test
  public void isLoadingObservableShouldReturnFalseBeforeAskingForOnePage() {

    Boolean isLoadingBeforeSubscribe = pager.getIsLoadingObservable().toBlocking().first();

    assertFalse(isLoadingBeforeSubscribe);
  }

  @Test
  public void isLoadingObservableShouldReturnTrueWhileIsWaitingForAPage() {
    PublishSubject<TokenPage<String>> getPageSubject = PublishSubject.<TokenPage<String>>create();
    given(getPageMock.call(anyString())).willReturn(getPageSubject);
    pager.getPageObservable().subscribe();

    Boolean isLoading = pager.getIsLoadingObservable().toBlocking().first();

    assertTrue(isLoading);
  }

  @Test
  public void isLoadingObservableShouldReturnFalseAfterReceivingOnePage() throws Exception {
    given(getPageMock.call(anyString())).willReturn(Observable.just(FIRST_PAGE));
    pager.getPageObservable().take(1).subscribe();

    Boolean isLoading = pager.getIsLoadingObservable().toBlocking().first();

    assertFalse(isLoading);
  }

  private void givenThereIsOnePage() {
    given(getPageMock.call(anyString())).willReturn(Observable.just(SINGLE_PAGE));
  }

  private void givenThereAreThreePages() {
    given(getPageMock.call(null)).willReturn(Observable.just(FIRST_PAGE));
    given(getPageMock.call("1")).willReturn(Observable.just(SECOND_PAGE));
    given(getPageMock.call("2")).willReturn(Observable.just(THIRD_PAGE));
  }
}
