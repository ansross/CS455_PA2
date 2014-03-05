package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cs455.scaling.client.ClientInfo;
import cs455.scaling.server.Server;
import cs455.scaling.util.Protocol;

public class ReadTask implements Task {
	private Server server;
	private SelectionKey key;
	
	public ReadTask(SelectionKey keyArg, Server servArg){
		ClientInfo client = (ClientInfo) keyArg.attachment();
		synchronized(client){
			client.setReading(true);
			this.key=keyArg;
			this.server=servArg;
		}
		
	}

	@Override
	public void execute() {
		if(Protocol.DEBUG){
			System.out.println("Received a read task");
		}
		SocketChannel channel = (SocketChannel) key.channel();
		//8KB = 8192 bytes
		ByteBuffer buffer = ByteBuffer.allocate(Protocol.MESSAGE_SIZE);
		int read = 0;
		try{
			System.out.println("7");
			while(buffer.hasRemaining() && read!=-1){
				//System.out.println("8");
				read = channel.read(buffer);
				//System.out.println("9");
			}
		}catch (IOException e){
			//abnormal termination
			//TODO !!! 
			server.disconnect(key);
			return;
		}
		System.out.println("1");
		//if(read==-1){
			System.out.println("2");
			buffer.flip();
			System.out.println("3");
			byte[] bufferBytes = new byte[Protocol.MESSAGE_SIZE];
			buffer.get(bufferBytes);
			System.out.println("4");
			//TODO
			long hash = 0;
			System.out.println("IMPLEMENT HASH");
			//ClientInfo client = (ClientInfo) key.attachment();
			//client.addToPendingWriteList(hash);
			key.interestOps(SelectionKey.OP_WRITE);
		//}
			ClientInfo client = (ClientInfo) key.attachment();
			synchronized(client){
				client.setReading(false);
			}
		// TODO Auto-generated method stub
		
	}

}
