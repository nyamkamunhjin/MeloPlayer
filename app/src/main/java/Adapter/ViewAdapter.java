package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.munhj.meloplayer.MainActivity;
import com.munhj.meloplayer.MusicService;
import com.munhj.meloplayer.R;

import Fragments.musicFragment;
import Model.ListItem;
import java.util.List;

import static com.munhj.meloplayer.MainActivity.item;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private Context context;
    private BottomNavigationView bottomNavigationView;
    private List<ListItem> listItems;

    public ViewAdapter(Context context, List listItems, BottomNavigationView bottomNavigationView) {
        this.listItems = listItems;
        this.context = context;
        this.bottomNavigationView = bottomNavigationView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter.ViewHolder viewHolder, int i) {
        ListItem item = listItems.get(i);
        viewHolder.artist.setText(item.getArtist());
        viewHolder.song_name.setText(item.getSongName());
        viewHolder.albumCover.setImageBitmap(item.getAlbumCover());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void startMusicService() {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MusicService.class);
        ContextCompat.startForegroundService(context.getApplicationContext(), serviceIntent);
    }

    public void stopMusicService() {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MusicService.class);
        context.stopService(serviceIntent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView artist;
        public TextView song_name;
        public ImageView albumCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            artist = itemView.findViewById(R.id.artist);
            song_name = itemView.findViewById(R.id.song_name);
            albumCover = itemView.findViewById(R.id.album_cover);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            ListItem item = listItems.get(pos);
            if(item != MainActivity.savedItem || MainActivity.savedItem == null) {
                Intent serviceIntent = new Intent(v.getContext(), MusicService.class);
                serviceIntent.putExtra("songName", item.getSongName());
                serviceIntent.putExtra("artist", item.getArtist());
                serviceIntent.putExtra("songPath", item.getSongPath());
                ContextCompat.startForegroundService(v.getContext(), serviceIntent);
                musicFragment.isMusicChosen = true;
                MainActivity.item = item;
                MainActivity.savedItem = item;
            }

            bottomNavigationView.setSelectedItemId(R.id.nav_music_player);

        }
    }
}
