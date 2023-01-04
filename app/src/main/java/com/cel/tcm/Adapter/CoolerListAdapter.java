package com.cel.tcm.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cel.tcm.Model.CoolerBasicResponse;
import com.cel.tcm.R;

import java.util.List;

public class CoolerListAdapter extends RecyclerView.Adapter<CoolerListAdapter.ViewHolder> {

    List<CoolerBasicResponse.Value> coolerList;

    public CoolerListAdapter(List<CoolerBasicResponse.Value> coolerList) {
        this.coolerList = coolerList;
    }

    @NonNull
    @Override
    public CoolerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cooler_info_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoolerListAdapter.ViewHolder holder, int position) {

        CoolerBasicResponse.Value response = coolerList.get(position);

        holder.assetCodeText.setText(response.assetCode);
        holder.brandText.setText(response.assetProperty1);
        holder.capacityText.setText(response.assetProperty2);
        holder.shelveNoText.setText(response.assetProperty3);
    }

    @Override
    public int getItemCount() {
        return coolerList.size();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetCodeText, brandText, capacityText, shelveNoText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            assetCodeText = itemView.findViewById(R.id.assetCodeText);
            brandText = itemView.findViewById(R.id.brandText);
            capacityText = itemView.findViewById(R.id.capacityText);
            shelveNoText = itemView.findViewById(R.id.shelveNoText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
