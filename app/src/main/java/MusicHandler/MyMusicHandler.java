package MusicHandler;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.munhj.meloplayer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.ListItem;

public class MyMusicHandler extends AsyncTask {
    private List<ListItem> listItems;
    private MediaMetadataRetriever parser;
    private Context context;

    public MyMusicHandler(Context context) {
        this.context = context;
        listItems = new ArrayList<>();
//        ScanDirForMusic(Environment.getExternalStorageDirectory(), listItems, 0);
        ScanWithMediaProvider(listItems, 0);
//        this.execute();
    }

    public void ScanDirForMusic(File dir, List<ListItem> files, int count) {

        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                ScanDirForMusic(file, files, count);
            else {
                String name = file.getName();

                if (name.toLowerCase().endsWith("mp3") || name.toLowerCase().endsWith("m4a")) {
                    Log.d("reading: ", name);
                    parser = new MediaMetadataRetriever();
                    parser.setDataSource(file.getPath());

                    // check if music file has an album cover (image) or not to prevent getting a null error
                    Bitmap coverImage;
                    if (parser.getEmbeddedPicture() != null) {
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

    public void ScanWithMediaProvider(List<ListItem> listItems, int count) {
//        ArrayList audio = new ArrayList();

        String[] projection = new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
//            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

            parser = new MediaMetadataRetriever();
            parser.setDataSource(data);

            Bitmap coverImage;
            if (parser.getEmbeddedPicture() != null) {
                coverImage = BitmapFactory.decodeByteArray(parser.getEmbeddedPicture(),
                        0, parser.getEmbeddedPicture().length);
            } else {
                coverImage = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.music_icon);
            }

            listItems.add(
                    new ListItem(
                            title,
                            artist,
                            data,
                            coverImage,
                            count
                    )

            );
            count++;
//            System.out.println("name: " + name);
//            System.out.println("album: " + album);
//            System.out.println("data" + data);
//            System.out.println("title: " + title);

        }
    }

    public List<ListItem> getListItems() {
        return listItems;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        ScanDirForMusic(Environment.getExternalStorageDirectory(), listItems, 0);
        return null;
    }
}

