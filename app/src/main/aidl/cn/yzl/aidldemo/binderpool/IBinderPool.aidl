// IBinderPool.aidl
package cn.yzl.aidldemo.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
