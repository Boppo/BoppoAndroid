package com.brymian.boppo.bryant.main.mainTabEpisodesRecyclerAdapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.brymian.boppo.R;

public class MainTabEpisodesLiveMostViewsRecyclerAdapter extends RecyclerView.Adapter<MainTabEpisodesLiveMostViewsRecyclerAdapter.RecyclerViewHolder> {

    static Activity activity;
    List<String> episodeTitle;
    List<String> episodeHostName;
    static List<Integer> episodeEid;
    List<Long> episodeViewCount;

    public MainTabEpisodesLiveMostViewsRecyclerAdapter(Activity activity, List<String> episodeTitle, List<String> episodeHostName, List<Integer> episodeEid, List<Long> episodeViewCount){
        MainTabEpisodesLiveMostViewsRecyclerAdapter.activity = activity;
        this.episodeTitle = episodeTitle;
        this.episodeHostName = episodeHostName;
        MainTabEpisodesLiveMostViewsRecyclerAdapter.episodeEid = episodeEid;
        this.episodeViewCount = episodeViewCount;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_tab_episodes_recyclerview_row,parent, false );
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tvEpisodeTitle.setText(episodeTitle.get(position));
        holder.tvEpisodeHostName.setText(episodeHostName.get(position));
        holder.tvEpisodeNum.setText(String.valueOf(episodeViewCount.get(position)));
    }

    @Override
    public int getItemCount() {
        return episodeTitle.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView tvEpisodeTitle, tvEpisodeHostName, tvEpisodeNum;
        public RecyclerViewHolder(View v){
            super(v);
            tvEpisodeTitle = (TextView) v.findViewById(R.id.tvEpisodeTitle);
            tvEpisodeHostName = (TextView) v.findViewById(R.id.tvEpisodeHostName);
            tvEpisodeNum = (TextView) v.findViewById(R.id.tvEpisodeNum);
        }
    }
}
