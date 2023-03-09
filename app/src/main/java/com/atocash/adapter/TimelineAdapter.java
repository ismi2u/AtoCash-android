package com.atocash.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.atocash.R;
import com.atocash.network.response.TimeLineItem;
import com.atocash.utils.Keys;
import com.repsly.library.timelineview.LineType;
import com.repsly.library.timelineview.TimelineView;

import java.util.List;
import java.util.Objects;

/**
 * Adapter for RecyclerView with TimelineView
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private final List<TimeLineItem> items;

    public TimelineAdapter(List<TimeLineItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_time_line_vertical;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(v);
    }

    private int previousState = -1; //pending

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(items.get(position).getApproverName());
        holder.tvAddress.setText(items.get(position).getApproverRole());
        holder.timelineView.setLineType(getLineType(position));
        holder.timelineView.setNumber(position);

        holder.timelineView.setFillMarker(false);

        if (Objects.requireNonNull(items.get(position).getApprovalStatusType()).equals(Keys.StatusType.APPROVED)) {
            holder.timelineView.setDrawable(AppCompatResources
                    .getDrawable(holder.timelineView.getContext(),
                            R.drawable.item_approved));
        } else if (Objects.requireNonNull(items.get(position).getApprovalStatusType()).equals(Keys.StatusType.REJECTED)) {
            holder.timelineView.setDrawable(AppCompatResources
                    .getDrawable(holder.timelineView.getContext(),
                            R.drawable.item_rejected));
            holder.tvAddress.setTextColor(ContextCompat.getColor(holder.tvAddress.getContext(), R.color.rejected));
            holder.tvName.setTextColor(ContextCompat.getColor(holder.tvAddress.getContext(), R.color.rejected));
        } else if (Objects.requireNonNull(items.get(position).getApprovalStatusType()).equals(Keys.StatusType.PENDING)) {
            holder.timelineView.setDrawable(AppCompatResources
                    .getDrawable(holder.timelineView.getContext(),
                            R.drawable.item_pending));
        } else if (Objects.requireNonNull(items.get(position).getApprovalStatusType()).equals(Keys.StatusType.INITIATING)) {
            holder.timelineView.setDrawable(AppCompatResources
                    .getDrawable(holder.timelineView.getContext(),
                            R.drawable.item_pending_grey));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private LineType getLineType(int position) {
        if (getItemCount() == 1) {
            return LineType.ONLYONE;
        } else {
            if (position == 0) {
                return LineType.BEGIN;

            } else if (position == getItemCount() - 1) {
                return LineType.END;

            } else {
                return LineType.NORMAL;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TimelineView timelineView;
        TextView tvName;
        TextView tvAddress;

        ViewHolder(View view) {
            super(view);
            timelineView = (TimelineView) view.findViewById(R.id.timeline);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvAddress = (TextView) view.findViewById(R.id.tv_role);
        }
    }

}