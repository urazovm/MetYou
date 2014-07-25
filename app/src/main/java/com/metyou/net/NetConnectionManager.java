package com.metyou.net;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by mihai on 7/19/14.
 */
public class NetConnectionManager {
    private ServerSocket mServerSocket;
    private int mLocalPort;
    private Selector selector;
    private ServerSocketChannel mServerSocketChannel;

    public NetConnectionManager() {

    }

    public void initializeServerSocket() {
        try {
            mServerSocket = new ServerSocket(0);
            mLocalPort = mServerSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        try {
            selector = Selector.open();
            mServerSocketChannel = mServerSocket.getChannel();
            mServerSocketChannel.configureBlocking(false);
            mServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable())
                        accept(key);
//                    else if (key.isReadable())
//                        read(key);
//                    else if (key.isWritable())
//                        write(key);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ);
        Log.d("Connection Manager", "New connection accepted: " + socketChannel.socket().getRemoteSocketAddress());
    }
}
