package brymian.bubbles.bryant.search;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import brymian.bubbles.R;
import brymian.bubbles.bryant.episodes.EpisodeActivity;


public class SearchRecyclerAdapterEpisodes extends RecyclerView.Adapter<SearchRecyclerAdapterEpisodes.RecyclerViewHolder> {
    private List<String> episodeTitle;
    private List<String> episodeHostUsername;
    private List<Integer> episodeEid;
    private List<String> episodeNum;
    private List<String> episodeImagePath;
    private Activity activity;

    public SearchRecyclerAdapterEpisodes(Activity activity, List<String> episodeTitle, List<String> episodeHostUsername, List<Integer> episodeEid, List<String> episodeNum, List<String> episodeImagePath){
        this.activity = activity;
        this.episodeTitle = episodeTitle;
        this.episodeHostUsername = episodeHostUsername;
        this.episodeEid = episodeEid;
        this.episodeNum = episodeNum;
        this.episodeImagePath = episodeImagePath;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_tab_episodes_recyclerview_row,parent, false );
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Picasso.with(activity).load(episodeImagePath.get(position)).fit().into(holder.ivEpisodeImage);
        holder.tvEpisodeTitle.setText(episodeTitle.get(position));
        holder.tvEpisodeHostUsername.setText(episodeHostUsername.get(position));
        holder.tvEpisodeNum.setText(episodeNum.get(position));
    }

    @Override
    public int getItemCount() {
        return episodeTitle.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView tvEpisodeTitle, tvEpisodeHostUsername, tvEpisodeNum;
        ImageView ivEpisodeImage;
        CardView cardView;
        public RecyclerViewHolder(View v){
            super(v);
            tvEpisodeTitle = (TextView) v.findViewById(R.id.tvEpisodeTitle);
            tvEpisodeHostUsername = (TextView) v.findViewById(R.id.tvEpisodeHostUsername);
            tvEpisodeNum = (TextView) v.findViewById(R.id.tvEpisodeNum);
            ivEpisodeImage = (ImageView) v.findViewById(R.id.ivEpisodeImage);
            cardView = (CardView) v.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(new Intent(activity, EpisodeActivity.class).putExtra("episodeTitle", episodeTitle.get(getAdapterPosition())).putExtra("eid", episodeEid.get(getAdapterPosition())));
                }
            });
        }
    }
}