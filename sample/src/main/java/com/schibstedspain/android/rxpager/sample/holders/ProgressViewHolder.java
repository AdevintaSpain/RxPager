package com.schibstedspain.android.rxpager.sample.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.schibstedspain.android.rxpager.sample.R;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
  public final ProgressBar progressBar;

  public ProgressViewHolder(View v) {
    super(v);
    progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
  }
}
