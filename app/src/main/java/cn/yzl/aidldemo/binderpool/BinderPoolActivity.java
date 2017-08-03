package cn.yzl.aidldemo.binderpool;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.yzl.aidldemo.R;

public class BinderPoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        new Thread() {
            @Override
            public void run() {
                BinderPool instance = BinderPool.getInstance(BinderPoolActivity.this);

                instance.connectionService();
                IBinder iBinder = instance.queryBinder(BinderPool.POOL_ONE);
                IBinder iBinder2 = instance.queryBinder(BinderPool.POOL_TWO);
                try {
                    IPoolOne.Stub.asInterface(iBinder).logName();
                    IPoolTwo.Stub.asInterface(iBinder2).logName();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
