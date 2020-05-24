package MasterMind.client;

import MasterMind.ConnectionHandler;
import MasterMind.MasterMind;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
 
public class ChatClient {

	static final int PORT = 9876;
	
	public ChatClient(String ip) {
        System.out.println("[client] attempting to connect to " + ip);
		
		/*
		 * Configure the client.
		 */
 
		// Since this is client, it doesn't need boss group. Create single group.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // Set EventLoopGroup to handle all events for client.
					.channel(NioSocketChannel.class)// Use NIO to accept new connections.
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							/*
							 * Socket/channel communication happens in byte streams. String decoder &
							 * encoder helps conversion between bytes & String.
							 */
							p.addLast(new StringDecoder());
							p.addLast(new StringEncoder());
 
							// This is our custom client handler which will have logic for the game.
							p.addLast(new ChatClientHandler());
 
						}
					});
 
			// Start the client.
			ChannelFuture f = b.connect(ip, PORT).sync();
 
			/*
			 * Iterate & take chat message inputs from user & then send to server.
			 */
			while (MasterMind.gameRunning) {
                if(ConnectionHandler.clientConnected == false && MasterMind.clientReadyToJoin) {
                    if(MasterMind.clientReceivedMessage("[AP] Successfully Joined")) {
                        ConnectionHandler.clientConnected = true;
                    }
                }
				if(MasterMind.clientMessagesOut.size() > 0) {
                    Channel channel = f.sync().channel();
                    channel.writeAndFlush(MasterMind.clientMessagesOut.get(0));
                    channel.flush();
                    MasterMind.clientMessagesOut.remove(0);
                }
                Thread.sleep(1);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
	}
}