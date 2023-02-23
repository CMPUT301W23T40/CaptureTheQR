package com.cmput301w23t40.capturetheqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class QRCodeList extends RecyclerView.Adapter<QRCodeList.RecyclerViewHolder>{

    private ArrayList<QRCode> qrCodes;
    private Context context;

    public QRCodeList(Context context, ArrayList<QRCode> qrCodes){
        this.qrCodes = qrCodes;
        this.context = context;
    }


    /* Adapted code from the following resource for the swipe-to-delete functionality:
            author: https://auth.geeksforgeeks.org/user/chaitanyamunje
            url: https://www.geeksforgeeks.org/swipe-to-delete-and-undo-in-android-recyclerview/
            last updated: Oct 5, 2021
    */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView qrCodeName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeName = itemView.findViewById(R.id.txtvw_qrcodeName);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.library_content, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        QRCode code = qrCodes.get(position);
        holder.qrCodeName.setText(code.getCodeName());
    }

    @Override
    public int getItemCount() {
        return qrCodes.size();
    }
}
