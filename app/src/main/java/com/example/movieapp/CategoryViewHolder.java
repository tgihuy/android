package com.example.movieapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import models.Category;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private TextView categoryNameTextView;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryNameTextView = itemView.findViewById(R.id.category_name);
    }

    public void bind(Category category) {
        categoryNameTextView.setText(category.getName());
    }
}
