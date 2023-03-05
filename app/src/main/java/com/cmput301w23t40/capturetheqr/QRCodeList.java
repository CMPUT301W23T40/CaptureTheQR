package com.cmput301w23t40.capturetheqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


/**
 * This class keeps track of the list of QR Code objects
 */

public class QRCodeList extends RecyclerView.Adapter<QRCodeList.RecyclerViewHolder>{

    private ArrayList<QRCode> qrCodes;
    private Context context;
    private static OnItemClickListener onItemClickListener;

    public QRCodeList(Context context, ArrayList<QRCode> qrCodes){
        this.qrCodes = qrCodes;
        this.context = context;
    }

    public QRCode getCode(int position) {
        return qrCodes.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener =  onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    /* Adapted code from the following resource for the swipe-to-delete functionality:
            author: https://auth.geeksforgeeks.org/user/chaitanyamunje
            url: https://www.geeksforgeeks.org/swipe-to-delete-and-undo-in-android-recyclerview/
            last updated: Oct 5, 2021
    */
    /**
     * This class keeps track of and deletes the UI element for displaying QR Codes
     */
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView qrCodeName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            qrCodeName = itemView.findViewById(R.id.txtvw_qrcodeName);

            itemView.setOnClickListener(view -> {
                int position  = RecyclerViewHolder.super.getBindingAdapterPosition();
                onItemClickListener.onItemClick(view,position);
            });
        }
    }

    /**
     * This inflates card layout items for the View
     * @param parent
     * @param viewType
     * @return RecylerViewHolder
     */
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.library_content, parent, false);
        return new RecyclerViewHolder(v);
    }

    /**
     * This sets up the item's content in given position used by the View
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        QRCode code = qrCodes.get(position);
        holder.qrCodeName.setText(code.getCodeName());
    }

    /**
     * This returns the length of the RecyclerView
     * @return size of qr code list
     */
    @Override
    public int getItemCount() {
        return qrCodes.size();
    }
}
