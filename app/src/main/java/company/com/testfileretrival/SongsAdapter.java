package company.com.testfileretrival;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by John Drake on 11/15/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    public static final String SONG = "SONG";
    ArrayList<SongInfo> songs;
    Context context;
    ViewHolder viewHolder = null;

    public SongsAdapter(ArrayList<SongInfo> songs, Context context)
    {
        this.songs = songs;
        this.context = context;
    }

    /**
     * This function is called only enough times to cover the screen with views.  After
     * that point, it recycles the views when scrolling is done.
     * @param parent the intended parent object (our RecyclerView)
     * @param viewType unused in our function (enables having different kinds of views in the same RecyclerView)
     * @return the new ViewHolder we allocate
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // a LayoutInflater turns a layout XML resource into a View object.
        final View songListItem = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_song_list_item, parent, false);
        return new ViewHolder(songListItem);
    }

    /**
     * This function gets called each time a ViewHolder needs to hold data for a different
     * position in the list.
     * @param holder the ViewHolder that knows about the Views we need to update
     * @param position the index into the array of Movies
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SongInfo song = songs.get(position);

        holder.titleView.setText(song.getTitle());
        holder.titleView.setTextColor(Color.WHITE);

        holder.artistView.setText(song.getArtist());
        holder.artistView.setTextColor(Color.WHITE);

        holder.albumView.setText(song.getAlbum());
        holder.albumView.setTextColor(Color.WHITE);

        holder.by.setTextColor(Color.WHITE);

        holder.playpause.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (viewHolder != holder && viewHolder != null) {
                    viewHolder.titleView.setTextColor(Color.WHITE);
                    viewHolder.albumView.setTextColor(Color.WHITE);
                    viewHolder.artistView.setTextColor(Color.WHITE);
                    viewHolder.by.setTextColor(Color.WHITE);

                }

                if (MainActivity.isPlaying() && viewHolder == holder) {
                    holder.playpause.setText("Play");
                    holder.titleView.setTextColor(Color.WHITE);
                    holder.albumView.setTextColor(Color.WHITE);
                    holder.artistView.setTextColor(Color.WHITE);
                    holder.by.setTextColor(Color.WHITE);
                    MainActivity.pause();
                }
                else {
                    if (viewHolder != holder && viewHolder != null) {
                        viewHolder.playpause.setText("Play");
                    }
                    holder.playpause.setText("Pause");
                    holder.titleView.setTextColor(Color.CYAN);
                    holder.albumView.setTextColor(Color.CYAN);
                    holder.artistView.setTextColor(Color.CYAN);
                    holder.by.setTextColor(Color.CYAN);
                    MainActivity.play(position);
                }
                viewHolder = holder;
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Code for launching an Explicit Intent to go to another Activity in
                // the same App.
                /*MainActivity myActivity = (MainActivity) context;
                FragmentManager fm = myActivity.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Frag1 frag1 = new Frag1();
                ft.add (R.id.frag1, frag1);
                ft.commit();

                /* Pass data as a Parcelable Plain-Old Java Object (POJO) */
                /*intent.putExtra(SONG, song);

                v.getContext().startActivity(intent);*/
            }
        });
    }

    /**
     * RecyclerView wants to know how many list items there are, so it knows when it gets to the
     * end of the list and should stop scrolling.
     * @return the number of Movies in the array.
     */
    @Override
    public int getItemCount() {
        return songs.size();
    }

    /**
     * A ViewHolder class for our adapter that 'caches' the references to the
     * subviews, so we don't have to look them up each time.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView titleView;
        public TextView artistView;
        public TextView albumView;
        public TextView by;
        public Button playpause;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            titleView = (TextView) itemView.findViewById(R.id.songTitle);
            artistView = (TextView) itemView.findViewById(R.id.artist);
            albumView = (TextView)itemView.findViewById(R.id.album);
            playpause = (Button)itemView.findViewById(R.id.play);
            by = (TextView)itemView.findViewById(R.id.byText);
        }
    }
}
