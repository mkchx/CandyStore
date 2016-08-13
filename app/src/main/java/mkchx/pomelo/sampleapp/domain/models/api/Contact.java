package mkchx.pomelo.sampleapp.domain.models.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    String phone;
    String formattedPhone;

    public String getPhone() {
        return phone;
    }

    public String getFormattedPhone() {
        return formattedPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.formattedPhone);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.phone = in.readString();
        this.formattedPhone = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}