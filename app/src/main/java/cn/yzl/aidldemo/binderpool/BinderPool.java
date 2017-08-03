package cn.yzl.aidldemo.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IntDef;

import java.util.concurrent.CountDownLatch;

/**
 * binder池,实现同一个service管理多个ibinder<br/>
 * 其实原理也很简单,就是创建一个 binder池aidl,提供一个根据code查询binder对象的方法<br/>
 * 然后在service中实现这个binderpool<br/>
 * 客户端封装一个单例对象,就是本对象,对外提供统一的管理<br/>
 * @see BinderPool#queryBinder(int) <br/>
 * 这样的好处很明显,就是不用每个binder都创建一个service了,并且可以再本类中实现 对service的一些处理<br/>
 * Created by YZL on 2017/8/3.
 */
public class BinderPool {
    public static BinderPool instance;

    private Context mContext;

    public static final int POOL_ONE = 1;

    public static final int POOL_TWO = 2;
    private CountDownLatch countDownLatch;


    @IntDef({POOL_ONE, POOL_TWO})
    public @interface PoolType {

    }

    IBinderPool iBinderPool;
    private IBinder mIBinder;

    private BinderPool(Context context) {
        mContext = context;
    }

    public static BinderPool getInstance(Context context) {
        if (instance == null) {
            synchronized (BinderPool.class) {
                if (instance == null) {
                    instance = new BinderPool(context);
                }
            }
        }
        return instance;
    }

    public IBinder queryBinder(@PoolType int type) {
        try {
            return iBinderPool.queryBinder(type);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void connectionService() {
        countDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIBinder = iBinder;
            iBinderPool = IBinderPool.Stub.asInterface(iBinder);
            try {
                iBinder.linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            /**
             * @see deathRecipient#binderDied()
             */
        }
    };


    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mIBinder.unlinkToDeath(deathRecipient, 0);
            iBinderPool = null;
            mIBinder = null;
            connectionService();
        }
    };

    public void onDestroy() {
        mIBinder.unlinkToDeath(deathRecipient, 0);
        mContext.unbindService(serviceConnection);
    }
}
