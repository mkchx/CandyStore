package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Photos implements Parcelable {

    int count;
    List<Items> items;

    public int getCount() {
        return count;
    }

    public List<Items> getItems() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeTypedList(this.items);
    }

    public Photos() {
    }

    protected Photos(Parcel in) {
        this.count = in.readInt();
        this.items = in.createTypedArrayList(Items.CREATOR);
    }

    public static final Parcelable.Creator<Photos> CREATOR = new Parcelable.Creator<Photos>() {
        @Override
        public Photos createFromParcel(Parcel source) {
            return new Photos(source);
        }

        @Override
        public Photos[] newArray(int size) {
            return new Photos[size];
        }
    };
}
