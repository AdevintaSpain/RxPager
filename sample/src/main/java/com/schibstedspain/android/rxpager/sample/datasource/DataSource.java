package com.schibstedspain.android.rxpager.sample.datasource;

import com.schibstedspain.android.rxpager.tokenpage.TokenPage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DataSource {
  public static final String FIRST_PAGE_TOKEN = "first";

  private static final String[] PAGE_TOKENS = new String[] { FIRST_PAGE_TOKEN, "second", "third", "fourth", null };
  private final HashMap<String, String> pages = new HashMap<>();

  public DataSource() {
    for (int i = 0; i < PAGE_TOKENS.length - 1; i++) {
      pages.put(PAGE_TOKENS[i], PAGE_TOKENS[i + 1]);
    }
  }

  private static TokenPage<String> getPage(String token, String nextToken) {
    return new TokenPage<>(nextToken,
        Arrays.asList(
            token + " page - element one",
            token + " page - element two",
            token + " page - element three",
            token + " page - element four",
            token + " page - element five",
            token + " page - element six",
            token + " page - element seven",
            token + " page - element eight",
            token + " page - element nine",
            token + " page - element ten"
        )
    );
  }

  public Observable<TokenPage<String>> getPage(String s) {
    return Observable.fromCallable(() -> getPage(s, pages.get(s)))
        .delay(1, TimeUnit.SECONDS, Schedulers.io());
    // delay operates on Scheduler.computation()
    // if you are making a network call you should use: `.subscribeOn(Schedulers.io())`
  }
}
