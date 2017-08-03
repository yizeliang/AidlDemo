package cn.yzl.aidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.yzl.aidldemo.aidl.Book;
import cn.yzl.aidldemo.aidl.IBookManager;
import cn.yzl.aidldemo.aidl.INewBookAddListener;

public class MyService extends Service {

    private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();

    /**
     * RemoteCallbackList是系统提供的专门用于删除跨进程listener的接口.
     * 用RemoteCallbackList，而不用ArrayList的原因是, 客户端的对象注册进来后，
     * 服务端会通过它反序列化出一个新的对象保存一起，所以说已经不是同一个对象了.
     * 在客户端调用解除注册方法时， 在list中根本就找不到它的对象， 也就无法从list中删除客户端的对象.
     * 而RemoteCallbackList的内部保存的是客户端对象底层的binder对象,
     * 这个binder对象在客户端对象和反序列化的新对象中是同一个对象,
     * RemoteCallbackList的实现原理就是利用的这个特性.
     */
    private RemoteCallbackList<INewBookAddListener> myListener = new RemoteCallbackList<>();
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBooks() throws RemoteException {
            return books;
        }

        @Override
        public void saveBook(Book book) throws RemoteException {
            books.add(book);
            myListener.beginBroadcast();//配对使用
            for (int i = 0; i < myListener.getRegisteredCallbackCount(); i++) {
                myListener.getBroadcastItem(i).onNewBookAdd(book);
            }
            myListener.finishBroadcast();
        }

        @Override
        public void registerAddBookListener(INewBookAddListener listener) throws RemoteException {
            myListener.register(listener);
        }

        @Override
        public void unRegisterAddBookListener(INewBookAddListener listener) throws RemoteException {
            myListener.unregister(listener);
        }

    };

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        books.add(new Book("jpm", 200));
        books.add(new Book("xxx", 100));
    }
}
