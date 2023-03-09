package com.atocash.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atocash.R;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseItem> {

    private String status = "PENDING";
    public ExpenseAdapter(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public ExpenseItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseItem(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_expense_status, parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseItem holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ExpenseItem extends RecyclerView.ViewHolder {

        public ExpenseItem(@NonNull View itemView) {
            super(itemView);
        }

        public void onBind(int position) {

        }
    }
}
