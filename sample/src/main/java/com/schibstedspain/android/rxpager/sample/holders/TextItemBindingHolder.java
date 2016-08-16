package com.schibstedspain.android.rxpager.sample.holders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.schibstedspain.android.rxpager.sample.databinding.TextItemBinding;

public class TextItemBindingHolder extends RecyclerView.ViewHolder {
  private final TextItemBinding binding;

  public static TextItemBindingHolder create(LayoutInflater inflater, ViewGroup parent) {
    TextItemBinding binding = TextItemBinding.inflate(inflater, parent, false);
    return new TextItemBindingHolder(binding);
  }

  private TextItemBindingHolder(TextItemBinding binding) {
    super(binding.getRoot());
    this.binding = binding;
  }

  public void bindTo(String text) {
    binding.setText(text);
    binding.executePendingBindings();
  }
}
