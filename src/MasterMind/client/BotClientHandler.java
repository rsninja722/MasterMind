package MasterMind.client;

import MasterMind.MasterMind;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
 
public class BotClientHandler extends SimpleChannelInboundHandler<String> {
 
	/*
	 * store messages received from server.
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("[BOT]" + msg);
        // if messages get squished together, separate them
        if(msg.lastIndexOf("[") > 1) {
            MasterMind.botMessagesIn.add(msg.substring(0, msg.lastIndexOf("[")));
            MasterMind.botMessagesIn.add(msg.substring(msg.lastIndexOf("[")));
        } else {
            MasterMind.botMessagesIn.add(msg);
        }
	}
 
}