package MusicHandler;


        import android.graphics.BitmapFactory;
        import android.media.MediaMetadataRetriever;
        import android.os.Environment;
        import android.util.Log;

        import java.io.File;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

        import Model.ListItem;

public class MyMusicHandler  {
    public List<ListItem> listItems;
    private MediaMetadataRetriever parser;

    public MyMusicHandler() {

        listItems = new ArrayList<>();
        ScanDirForMusic(Environment.getExternalStorageDirectory(), listItems, 0);

    }

    private void ScanDirForMusic(File dir, List<ListItem> files, int count) {

        for(File file : dir.listFiles()) {
            if(file.isDirectory())
                ScanDirForMusic(file, files, count);
            else {
                String name = file.getName();

                if(name.endsWith("mp3") || name.endsWith("MP3")) {
                    Log.d("reading: ", name);
                    parser = new MediaMetadataRetriever();
                    parser.setDataSource(file.getPath());
                    files.add(new ListItem(parser.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                            parser.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), file.getPath(),
                            BitmapFactory.decodeByteArray(parser.getEmbeddedPicture(), 0, parser.getEmbeddedPicture().length), count));
                    count++;
                }
            }

        }
    }

}
