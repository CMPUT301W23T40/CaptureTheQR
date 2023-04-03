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

/**
 * This class is for displaying codes in the scoreboard activity
 */
public class ScoreboardCodeList extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> qrCodes;
    private Context context;

    public ScoreboardCodeList(Context context, ArrayList<QRCode> qrCodes) {
        super(context,0,qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
    }

    /**
     * get a view of a code
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     */
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
