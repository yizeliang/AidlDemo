// IBookManager.aidl
package cn.yzl.aidldemo.aidl;

import cn.yzl.aidldemo.aidl.Book;
import cn.yzl.aidldemo.aidl.INewBookAddListener;

// Declare any non-default types here with import statements
interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    List<Book> getBooks();

    void saveBook(in Book book);
    void registerAddBookListener(in INewBookAddListener listener);
    void unRegisterAddBookListener(in INewBookAddListener listener);
}
