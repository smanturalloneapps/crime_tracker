package com.taranga.crimetracker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taranga.crimetracker.R;
import com.taranga.crimetracker.model.CrimeModel;
import com.taranga.crimetracker.model.event_bus.CrimeMasterModel;

import java.util.List;

public class CrimeHistoryAdapter extends RecyclerView.Adapter<CrimeHistoryAdapter.PickUpRequestsViewHolder> {

    private static final String TAG = CrimeHistoryAdapter.class.getSimpleName();
    // region Variable declarations

    private List<CrimeMasterModel> dataList;
    private RequestItemRemoveClickListener listener;
    private Context context;

    // endregion

    /**
     * Constructs PickUpRequestAdapter with ArrayList of type PickupRequestModel
     *
     * @param dataList ArrayList of PickupRequestModel
     */
    public CrimeHistoryAdapter(Context context, List<CrimeMasterModel> dataList, RequestItemRemoveClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public CrimeHistoryAdapter.PickUpRequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crime_history, parent, false);
        return new CrimeHistoryAdapter.PickUpRequestsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CrimeHistoryAdapter.PickUpRequestsViewHolder holder, int position) {
        holder.setItem(dataList.get(position).getCrimeModel());

        String key = dataList.get(position).getKey();
        CrimeModel crimeModel = dataList.get(position).getCrimeModel();
        holder.textCrimeType.setText(crimeModel.getCrime());
        holder.textCrimeDate.setText(crimeModel.getDate());
        holder.textCrimeTime.setText(crimeModel.getTime());
        holder.textCrimePlace.setText(crimeModel.getAddress());

        holder.imageDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeItemFromDatabase(position, dataList, key, crimeModel);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class PickUpRequestsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout buttonsLayout;
        TextView textCrimeType, textCrimeDate, textCrimeTime, textCrimePlace;
        ImageView imageDeleteItem;

        CrimeModel crimeModel;

        PickUpRequestsViewHolder(View itemView) {
            super(itemView);

            textCrimeType = (TextView) itemView.findViewById(R.id.text_crime_detail);
            textCrimeDate = (TextView) itemView.findViewById(R.id.text_crime_date);
            textCrimeTime = (TextView) itemView.findViewById(R.id.text_crime_time);
            textCrimePlace = (TextView) itemView.findViewById(R.id.text_crime_address);

            imageDeleteItem = (ImageView) itemView.findViewById(R.id.image_delete_item);
        }

        public void setItem(CrimeModel item) {
            crimeModel = item;
        }

        @Override
        public void onClick(View v) {
        }
    }

    public interface RequestItemRemoveClickListener {

        void removeItemFromDatabase(int position, List<CrimeMasterModel> crimeMasterModels, String key, CrimeModel crimeModel);

    }
}