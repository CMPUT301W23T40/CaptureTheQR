package com.cmput301w23t40.capturetheqr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ScoreboardCodeList extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> qrCodes;
    private Context context;

    public ScoreboardCodeList(Context context, ArrayList<QRCode> qrCodes) {
        super(context,0,qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.scoreboard_content, parent,false);
        }
        QRCode qrCode = qrCodes.get(position);
        TextView rankText = view.findViewById(R.id.txtvw_rank);
        TextView codeText = view.findViewById(R.id.txtvw_playerORCode);
        TextView scoreText = view.findViewById(R.id.txtvw_score);
        rankText.setText(String.valueOf(qrCode.getRank()));
        codeText.setText(qrCode.getCodeName());
        scoreText.setText(String.valueOf(qrCode.getScore()));
        return view;
    }
}
