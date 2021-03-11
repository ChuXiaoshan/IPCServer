package com.cxsplay.ipcserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by CxS on 2021/3/11 11:28
 */
public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() {
            return mBookList;
        }

        @Override
        public void addBook(Book book) {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) {
            mListenerList.register(listener);
            LogUtils.d("---registerListener current size --->" + mListenerList.getRegisteredCallbackCount());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) {
            boolean isSuccess = mListenerList.unregister(listener);
            if (isSuccess) LogUtils.d("---unregister success--->");
            LogUtils.d("---unregisterListener current size --->" + mListenerList.getRegisteredCallbackCount());
        }
    };

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
            if (l != null) {
                l.onNewBookArrived(book);
            }
            mListenerList.finishBroadcast();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("---onCreate--->");
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("---onBind--->");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }
}
