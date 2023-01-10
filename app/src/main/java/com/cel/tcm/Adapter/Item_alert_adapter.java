package com.cel.tcm.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cel.tcm.Model.CoolerPropertiesResponse;
import com.cel.tcm.Model.OutletsResponse;
import com.cel.tcm.Model.RoutesResponse;
import com.cel.tcm.Model.SalesPointsResponse;
import com.cel.tcm.R;
import com.cel.tcm.Utils.Constants;

import java.util.List;

public class Item_alert_adapter extends RecyclerView.Adapter<Item_alert_adapter.ViewHolder> {
    String type;
    List<SalesPointsResponse.Value> salesPointsList;
    List<RoutesResponse.Value> routesList;
    List<OutletsResponse.Value> outletsList;
    List<CoolerPropertiesResponse.Value> coolerPropertiesList;

    public Item_alert_adapter(List<SalesPointsResponse.Value> salesPointsList, List<RoutesResponse.Value> routesList, List<OutletsResponse.Value> outletsList, List<CoolerPropertiesResponse.Value> coolerPropertiesList, String type) {

        this.type = type;
        this.salesPointsList = salesPointsList;
        this.routesList = routesList;
        this.outletsList = outletsList;
        this.coolerPropertiesList = coolerPropertiesList;
    }

    @NonNull
    @Override
    public Item_alert_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item_alert_adapter.ViewHolder holder, int position) {
        String code = "", name = "";
        if (type.equals(Constants.SALES)) {
            List<SalesPointsResponse.Value> responses = salesPointsList;
            code = responses.get(position).salesPointCode.toString();
            name = responses.get(position).salesPointName.toString();
        } else if (type.equals(Constants.ROUTES)) {
            List<RoutesResponse.Value> responses = routesList;
            code = responses.get(position).code.toString();
            name = responses.get(position).name.toString();
        } else if (type.equals(Constants.OUTLETS)) {
            List<OutletsResponse.Value> responses = outletsList;
            code = responses.get(position).code.toString();
            name = responses.get(position).name.toString();
        } else if (type.equals(Constants.BRAND)) {
            List<CoolerPropertiesResponse.Value> responses = coolerPropertiesList;
            name = responses.get(position).item.toString();
        } else if (type.equals(Constants.CAPACITY)) {
            List<CoolerPropertiesResponse.Value> responses = coolerPropertiesList;
            name = responses.get(position).item.toString();
        } else if (type.equals(Constants.SHELVE)) {
            List<CoolerPropertiesResponse.Value> responses = coolerPropertiesList;
            name = responses.get(position).item.toString();
        }


        if (TextUtils.isEmpty(code)) {
            holder.textView1.setVisibility(View.GONE);
        } else {
            holder.textView1.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(name)) {
            holder.textView2.setVisibility(View.GONE);
        } else {
            holder.textView2.setVisibility(View.VISIBLE);
        }

        holder.textView1.setText("Code: " + code);
        holder.textView2.setText("Name: " + name);


    }

    @Override
    public int getItemCount() {
        //return 0;
        if (type.equals(Constants.SALES)) {
            return salesPointsList.size();
        } else if (type.equals(Constants.ROUTES)) {
            return routesList.size();
        } else if (type.equals(Constants.OUTLETS)) {
            return outletsList.size();
        } else if (type.equals(Constants.BRAND) || type.equals(Constants.CAPACITY) || type.equals(Constants.SHELVE)) {
            return coolerPropertiesList.size();
        }

        return 0;

    }

    private OnAlertItemClickListener onAlertItemClickListener;

    public interface OnAlertItemClickListener {
        void onAlertItemClick(int position, String type);
    }

    public void setOnAlertItemClickListener(OnAlertItemClickListener onAlertItemClickListener) {
        this.onAlertItemClickListener = onAlertItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView1, textView2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onAlertItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onAlertItemClickListener.onAlertItemClick(position, type);
                        }
                    }
                }
            });
        }
    }
}
