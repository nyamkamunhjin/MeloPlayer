package MusicHandler;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import com.munhj.meloplayer.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import Model.ListItem;

public class MyMusicHandler  {
    private List<ListItem> listItems;
    private MediaMetadataRetriever parser;
    private Context context;
    public MyMusicHandler(Context context) {
        this.context = context;
        listItems = new ArrayList<>();
        ScanDirForMusic(Environment.getExternalStorageDirectory(), listItems, 0);

    }

    private void ScanDirForMusic(File dir, List<ListItem> files, int count) {

        for(File file : dir.listFiles()) {
            if(file.isDirectory())
                ScanDirForMusic(file, files, count);
            else {
                String name = file.getName();

                if(name.toLowerCase().endsWith("mp3") || name.toLowerCase().endsWith("m4a") ) {
                    Log.d("reading: ", name);
                    parser = new MediaMetadataRetriever();
                    parser.setDataSource(file.getPath());

                    // check if music file has an album cover (image) or not to prevent getting a null error
                    Bitmap coverImage;
                    if(parser.getEmbeddedPicture() != null) {
                        coverImage = BitmapFactory.decodeByteArray(parser.getEmbeddedPicture(),
                                0, parser.getEmbeddedPicture().length);
                    } else {
                        coverImage = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.music_icon);
                    }

                    files.add(new ListItem(parser.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                            parser.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), file.getPath(),
                            coverImage, count));
                    count++;
                }
            }

        }
    }

    public List<ListItem> getListItems() {
        return listItems;
    }

}
