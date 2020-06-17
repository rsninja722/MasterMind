package MasterMind.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import MasterMind.MasterMind;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    /*
     * store messages received from server.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // if messages get squished together, separate them
        String[] messages = msg.split(",");

        for (int i = 1; i < messages.length; i++) {
            System.out.println(messages[i]);
            MasterMind.clientMessagesIn.add(messages[i]);
        }
    }

}