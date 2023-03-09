package com.atocash.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atocash.R;
import com.atocash.network.response.EmployeeDropDownResponse;

import java.util.ArrayList;

public class EmployeesDropDownAdapter extends ArrayAdapter {

    private final ArrayList<EmployeeDropDownResponse> statusDropDownResponse;
    private final Context context;

    public EmployeesDropDownAdapter(Context context, int textViewResourceId,
                                    ArrayList<EmployeeDropDownResponse> statusDropDownResponse) {
        super(context, textViewResourceId, statusDropDownResponse);
        this.statusDropDownResponse = statusDropDownResponse;
        this.context = context;
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.item_status_spinner_view, parent, false);

        TextView statusTv = (TextView) layout.findViewById(R.id.status_tv);
        statusTv.setText(statusDropDownResponse.get(position).getFullName());

        return layout;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view = getCustomView(position, convertView, parent);

        float density = context.getResources().getDisplayMetrics().density;
        int tenDp = (int) (10 * density + 0.5f);
        int fiveDp = (int) (5 * density + 0.5f);
        LinearLayout parentLayout = view.findViewById(R.id.parentView);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentLayout.setLayoutParams(layoutParams);

        TextView bankNameTv = (TextView) view.findViewById(R.id.status_tv);
        bankNameTv.setPadding(tenDp, fiveDp, tenDp, 0);

        bankNameTv.setText(statusDropDownResponse.get(position).getFullName());

        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}

