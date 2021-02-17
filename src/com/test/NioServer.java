package com.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterable = selectionKeys.iterator();
            while (iterable.hasNext()) {
                SelectionKey selectionKey = iterable.next();
                if (selectionKey.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    if (channel.read(byteBuffer) > 0) {
                        byteBuffer.flip();
                        String str = new String(byteBuffer.array(), 0 , byteBuffer.remaining());
                        System.out.println(str);
                        // 获取所有通道
                        for (SelectionKey selectedKey : selector.selectedKeys()) {
                            if (selectedKey.channel() != channel) {
                                ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
                                byteBuffer1.put(str.getBytes(StandardCharsets.UTF_8));
                                byteBuffer1.flip();
                                channel.write(byteBuffer1);
                            }
                        }

                    }
                }
                iterable.remove();
            }
        }

    }
}
