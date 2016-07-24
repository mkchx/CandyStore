package mkchx.pomelo.sampleapp.models.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Stats implements Parcelable {

    int checkinsCount;
    int usersCount;
    int tipCount;

    public int getCheckinsCount() {
        return checkinsCount;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public int getTipCount() {
        return tipCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.checkinsCount);
        dest.writeInt(this.usersCount);
        dest.writeInt(this.tipCount);
    }

    public Stats() {
    }

    protected Stats(Parcel in) {
        this.checkinsCount = in.readInt();
        this.usersCount = in.readInt();
        this.tipCount = in.readInt();
    }

    public static final Parcelable.Creator<Stats> CREATOR = new Parcelable.Creator<Stats>() {
        @Override
        public Stats createFromParcel(Parcel source) {
            return new Stats(source);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };
}
