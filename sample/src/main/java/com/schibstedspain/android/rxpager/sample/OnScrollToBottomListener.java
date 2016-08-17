package com.schibstedspain.android.rxpager.sample;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import rx.functions.Action0;

public class OnScrollToBottomListener extends RecyclerView.OnScrollListener {
  private final Action0 action;

  public OnScrollToBottomListener(Action0 action) {
    this.action = action;
  }

  @Override
  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    int visibleItemCount = layoutManager.getChildCount();
    int totalItemCount = layoutManager.getItemCount();
    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
      action.call();
    }
  }
}
