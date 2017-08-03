package cn.yzl.aidldemo.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BinderPoolService extends Service {


    Binder myPool = new IBinderPool.Stub() {
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            switch (binderCode){
                case BinderPool.POOL_ONE:
                    return new IPoolOne.Stub() {
                        @Override
                        public void logName() throws RemoteException {
                            Log.e("IBinderPool",this.getInterfaceDescriptor());
                        }
                    };
                case BinderPool.POOL_TWO:
                    return new IPoolTwo.Stub() {
                        @Override
                        public void logName() throws RemoteException {
                            Log.e("IBinderPool",this.getInterfaceDescriptor());
                        }
                    };
            }
            return null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return myPool;
    }
}
