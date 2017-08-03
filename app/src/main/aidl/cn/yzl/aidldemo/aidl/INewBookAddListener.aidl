// INewBookAddListener.aidl
package cn.yzl.aidldemo.aidl;
import cn.yzl.aidldemo.aidl.Book;

interface INewBookAddListener {
    void onNewBookAdd(in Book b);

//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

}
