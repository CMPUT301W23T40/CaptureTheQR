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

public class ScoreboardList extends ArrayAdapter<Player> {
    private ArrayList<Player> players;
    private Context context;
    private SortBy sortBy;


    public ScoreboardList(Context context, ArrayList<Player> players, SortBy sortBy) {
        super(context,0,players);
        this.players = players;
        this.context = context;
        this.sortBy = sortBy;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.scoreboard_content, parent,false);
        }
        Player player = players.get(position);
        TextView rankText = view.findViewById(R.id.txtvw_rank);
        TextView playerText = view.findViewById(R.id.txtvw_playerORCode);
        TextView scoreText = view.findViewById(R.id.txtvw_score);
        rankText.setText(String.valueOf(player.getRank()));
        playerText.setText(player.getUsername());
        if (sortBy.equals(SortBy.HIGHEST_SCORE)){
            scoreText.setText(String.valueOf(player.getHighScore()));
        } else if (sortBy.equals(SortBy.NUMBER_OF_CODES)){
            scoreText.setText(String.valueOf(player.getNumberOfCodes()));
        } else if (sortBy.equals(SortBy.SCORE_SUM)){
            scoreText.setText(String.valueOf(player.getScoreSum()));
        }



        return view;
    }
}
