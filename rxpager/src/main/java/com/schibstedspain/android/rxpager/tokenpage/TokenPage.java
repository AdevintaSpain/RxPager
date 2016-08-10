package com.schibstedspain.android.rxpager.tokenpage;

import java.util.List;

public class TokenPage<ITEM> {
  private final String nextPageToken;
  private final List<ITEM> results;

  public TokenPage(String nextPageToken, List<ITEM> results) {
    this.nextPageToken = nextPageToken;
    this.results = results;
  }

  public String getNextPageToken() {
    return nextPageToken;
  }

  public List<ITEM> getResults() {
    return results;
  }
}
