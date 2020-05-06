import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
 
	static ChatClient c;
	static String HOST = "127.0.0.1";
	static final int PORT = 9876;
	String clientName;
	public static void main(String[] args) throws Exception {
		c = new ChatClient();
	}
	
	
	ChatClient() {
 
		/*
		 * Get name of the user for this chat session.
		 */
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter your name: ");
		if (scanner.hasNext()) {
			clientName = scanner.nextLine();
			System.out.println("Welcome " + clientName);
		}
		
		System.out.println("Please enter server IP: ");
		if (scanner.hasNext()) {
			HOST = scanner.nextLine();
			System.out.println("attempting to connect to " + HOST);
		}
		
		/*
		 * Configure the client.
		 */
 
		// Since this is client, it doesn't need boss group. Create single group.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group) // Set EventLoopGroup to handle all eventsf for client.
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
 
							// This is our custom client handler which will have logic for chat.
							p.addLast(new ChatClientHandler());
 
						}
					});
 
			// Start the client.
			ChannelFuture f = b.connect(HOST, PORT).sync();
 
			/*
			 * Iterate & take chat message inputs from user & then send to server.
			 */
	         
			while (scanner.hasNext()) {
				String input = scanner.nextLine();
				Channel channel = f.sync().channel();
				channel.writeAndFlush("[" + clientName + "]: " + input);
				channel.flush();
			}
 
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
	}
}