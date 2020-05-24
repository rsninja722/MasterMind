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
        
        // if messages get squished together, separate them
        while(msg.lastIndexOf("[") > 1) {
            for(int i=2;i<msg.length();i++) {
                if(msg.charAt(i) == '[') {
                    System.out.println("[BOT]" + msg.substring(0, i));
                    MasterMind.botMessagesIn.add(msg.substring(0, i));
                    msg = msg.substring(i);
                }
            }
        }
        System.out.println("[BOT]" + msg);
        MasterMind.botMessagesIn.add(msg);
	}
 
}