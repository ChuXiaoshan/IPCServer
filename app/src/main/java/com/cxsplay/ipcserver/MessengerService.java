package com.cxsplay.ipcserver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by CxS on 2021/3/10 16:24
 */
public class MessengerService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("---onCreate--->");
    }

    private static class MessengerHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Constants.MSG_FROM_CLIENT:
                    //接收到来自客户端的消息
                    String msgFromClient = msg.getData().getString("msg");
                    LogUtils.d("---msgFromClient--->" + msgFromClient);
                    //回复客户端
                    Messenger client = msg.replyTo;
                    Message replyMessage = Message.obtain(null, Constants.MSG_FROM_CLIENT);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "嗯，你的消息我已经收到了，稍后回复你。");
                    replyMessage.setData(bundle);
                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("---onBind--->");
        return mMessenger.getBinder();
    }
}