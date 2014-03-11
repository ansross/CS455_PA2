package cs455.scaling.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import cs455.scaling.client.ClientInfo;
import cs455.scaling.server.Server;
import cs455.scaling.util.Protocol;
//not used, connection handled in server
public class ConnectTask implements Task {
	private Server server;
	private SelectionKey key;
	
	public ConnectTask(SelectionKey keyArg, Server servArg){
		this.key = keyArg;
		this.server=servArg;
	}
	
	@Override
	public void execute() {
		if(Protocol.DEBUG){
			System.out.println("Connect task executing");
		}
		ServerSocketChannel servSockCh = (ServerSocketChannel) key.channel();
		try {
			SocketChannel channel = servSockCh.accept();
			//Socket socket = channel.socket();
			if(Protocol.DEBUG){
				System.out.println("Accepting incoming connection "+Thread.currentThread().getName());
				System.out.println("null channel: "+(channel==null));
			}
			//since non-blocking, wait until established
			while(channel==null){
				if(Protocol.DEBUG){
					
				}
			}
			ClientInfo client = new ClientInfo(channel);
			channel.configureBlocking(false);
			//register new channel with server's selector
			
			server.registerChannel(channel, client);
			if(Protocol.DEBUG){
				System.out.println("registered connection");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
