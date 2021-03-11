package com.cxsplay.ipcserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by CxS on 2021/3/11 11:28
 */
public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("---onCreate--->");
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("---onBind--->");
        return mBinder;
    }
}
