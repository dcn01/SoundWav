package company.com.testfileretrival;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by John Drake on 11/15/2017.
 */

public class SongInfo implements Parcelable {
    private String title;
    private String artist;
    private String album;
    private int audio;
    private int albumArt;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeInt(this.audio);
        dest.writeInt(this.albumArt);
    }

    public SongInfo(){
    }

    protected SongInfo(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.audio = in.readInt();
        this.albumArt = in.readInt();
    }

    public static final Parcelable.Creator<SongInfo> CREATOR = new Parcelable.Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel source) {
            return new SongInfo(source);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

}
