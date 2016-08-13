package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Venues implements Parcelable {

    String id;
    String name;
    Location location;
    Contact contact;
    List<Categories> categories;
    Stats stats;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Contact getContact() {
        return contact;
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.location, flags);
        dest.writeParcelable(this.contact, flags);
        dest.writeTypedList(this.categories);
        dest.writeParcelable(this.stats, flags);
    }

    public Venues() {
    }

    protected Venues(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.contact = in.readParcelable(Contact.class.getClassLoader());
        this.categories = in.createTypedArrayList(Categories.CREATOR);
        this.stats = in.readParcelable(Stats.class.getClassLoader());
    }

    public static final Parcelable.Creator<Venues> CREATOR = new Parcelable.Creator<Venues>() {
        @Override
        public Venues createFromParcel(Parcel source) {
            return new Venues(source);
        }

        @Override
        public Venues[] newArray(int size) {
            return new Venues[size];
        }
    };
}