package com.example.assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private List<Alert.WeatherEntry> alertList;

    public void setAlertList(List<Alert.WeatherEntry> alertList) {
        this.alertList = alertList;
    }
    public AlertAdapter(List<Alert.WeatherEntry> alertList) {
        this.alertList = alertList;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_alert_list, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert.WeatherEntry alertItem = alertList.get(position);

        Context context = holder.itemView.getContext();

        holder.iconImageView.setImageDrawable(alertItem.getIconDrawable(context));
        holder.idTextView.setText("Alert: " + alertItem.getType());
        holder.timeTextView.setText("Time: " + alertItem.getTime());
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    static class AlertViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView idTextView;
        TextView timeTextView;

        public AlertViewHolder(final View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            idTextView = itemView.findViewById(R.id.typeTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
