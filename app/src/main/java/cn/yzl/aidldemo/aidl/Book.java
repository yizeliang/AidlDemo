package cn.yzl.aidldemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YZL on 2017/8/3.
 */
public class Book implements Parcelable {
    public String name = "book";
    public int price = 100;

    public Book() {
    }

    public Book(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.price);
    }


    protected Book(Parcel in) {
        this.name = in.readString();
        this.price = in.readInt();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
