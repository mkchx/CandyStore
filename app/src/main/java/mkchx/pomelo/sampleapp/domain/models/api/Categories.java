package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Categories implements Parcelable {

    String id;
    String name;
    String pluralName;
    String shortName;

    Icon icon;
    boolean primary;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public String getShortName() {
        return shortName;
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isPrimary() {
        return primary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.pluralName);
        dest.writeString(this.shortName);
        dest.writeParcelable(this.icon, flags);
        dest.writeByte(this.primary ? (byte) 1 : (byte) 0);
    }

    public Categories() {
    }

    protected Categories(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.pluralName = in.readString();
        this.shortName = in.readString();
        this.icon = in.readParcelable(Icon.class.getClassLoader());
        this.primary = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Categories> CREATOR = new Parcelable.Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel source) {
            return new Categories(source);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };
}
