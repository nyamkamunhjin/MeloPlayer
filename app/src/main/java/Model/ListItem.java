package Model;


import android.graphics.Bitmap;

public class ListItem {
    private String songName;
    private String artist;
    private String songPath;
    private Bitmap albumCover;
    private int pos;

    public ListItem(String songName, String artist, String songPath, Bitmap albumCover, int pos) {
        this.songName = songName;
        this.artist = artist;
        this.songPath = songPath;
        this.albumCover = albumCover;
        this.pos = pos;
    }

    public Bitmap getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(Bitmap albumCover) {
        this.albumCover = albumCover;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

}
