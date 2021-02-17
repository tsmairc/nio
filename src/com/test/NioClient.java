package com.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("client already!!!å");
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(("TEST NIO:" + Thread.currentThread()).getBytes(StandardCharsets.UTF_8));
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        System.out.println("write success");

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterable = selectionKeys.iterator();
            while (iterable.hasNext()) {
                SelectionKey selectionKey = iterable.next();
                if (selectionKey.isReadable()) {
                    ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
                    SocketChannel socketChannel1 = (SocketChannel) selectionKey.channel();
                    socketChannel1.read(byteBuffer1);
                    byteBuffer1.flip();
                    System.out.println(new String(byteBuffer1.array(), 0, byteBuffer1.remaining()));
                }
                iterable.remove();
            }
        }
    }
}
