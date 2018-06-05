package com.schibstedspain.android.rxpager.sample.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.schibstedspain.android.rxpager.sample.R;
import com.schibstedspain.android.rxpager.sample.holders.ProgressViewHolder;
import com.schibstedspain.android.rxpager.sample.holders.TextItemBindingHolder;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private LayoutInflater layoutInflater;
  private final List<String> textItems = new ArrayList<>();
  private boolean isLoading;

  public void addItems(List<String> newTextItems) {
    textItems.addAll(newTextItems);
    notifyDataSetChanged();
  }

  public void setIsLoading(boolean isLoading) {
    this.isLoading = isLoading;
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    if (layoutInflater == null) {
      layoutInflater = LayoutInflater.from(viewGroup.getContext());
    }
    switch (viewType) {
      case 0:
        return TextItemBindingHolder.create(layoutInflater, viewGroup);
      default:
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_item, viewGroup, false);

        return new ProgressViewHolder(v);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == textItems.size()) {
      return 1;
    } else {
      return 0;
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof TextItemBindingHolder) {
      ((TextItemBindingHolder) holder).bindTo(textItems.get(position));
    } else {
      ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
    }
  }

  @Override
  public int getItemCount() {
    if (isLoading) {
      return textItems.size() + 1;
    }
    return textItems.size();
  }
}
