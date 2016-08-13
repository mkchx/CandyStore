package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Items implements Parcelable {

    String id;
    String prefix;
    String suffix;
    int width;
    int height;

    public String getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.prefix);
        dest.writeString(this.suffix);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public Items() {
    }

    protected Items(Parcel in) {
        this.id = in.readString();
        this.prefix = in.readString();
        this.suffix = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<Items> CREATOR = new Parcelable.Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel source) {
            return new Items(source);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };
}
