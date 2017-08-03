package cn.yzl.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yzl.aidldemo.aidl.Book;
import cn.yzl.aidldemo.aidl.IBookManager;
import cn.yzl.aidldemo.aidl.INewBookAddListener;
import cn.yzl.aidldemo.service.MyService;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tv)
    TextView tv;
    private IBookManager myBookManager;

    /**
     * binder死亡监听
     */
    IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e("ibinder", "死亡");
        }
    };

    /**
     * 远端监听书籍添加
     * 其实这里相当于是远端,然后通过IBookManager 传递给 service,service相当于是本地
     * 在sevice添加监听,添加书籍的时候,调onNewBookAdd方法,其实又相当于是一个RPC过程,service是本地,这里是远端
     */
    private INewBookAddListener mListener = new INewBookAddListener.Stub() {
        @Override
        public void onNewBookAdd(Book b) throws RemoteException {
            Toast.makeText(MainActivity.this, b.toString(), Toast.LENGTH_LONG).show();
        }

    };

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBookManager = IBookManager.Stub.asInterface(iBinder);
            try {
                myBookManager.registerAddBookListener(mListener);
                //添加死亡监听
                iBinder.linkToDeath(deathRecipient, 0);
                //移出死亡监听
//                iBinder.unlinkToDeath(deathRecipient,0);
                //检查binder是否还活着
                iBinder.isBinderAlive();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (serviceIntent == null) {
            serviceIntent = new Intent(this, MyService.class);
        }
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }


    public void getBookList() {
        try {
            if (myBookManager != null) {
                List<Book> books = myBookManager.getBooks();
                String s = "";
                for (int i = 0; i < books.size(); i++) {
                    s += books.get(i).toString() + "\n";
                }
                tv.setText(s);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.bt_getlist, R.id.bt_addbook})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_getlist:
                getBookList();
                break;
            case R.id.bt_addbook:
                saveBook();
                break;
        }
    }

    void saveBook() {
        try {
            myBookManager.saveBook(new Book(System.currentTimeMillis() + "", 111));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            myBookManager.unRegisterAddBookListener(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(connection);
        super.onDestroy();
    }

}
