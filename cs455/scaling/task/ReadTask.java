package cs455.scaling.task;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cs455.scaling.client.ClientInfo;
import cs455.scaling.server.Server;
import cs455.scaling.util.Protocol;
import cs455.scaling.util.Utilities;

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
		if(read==-1){
			//TODO
			//client terminated connection
			//server.disconnet(key);
			return;
		}

		buffer.flip();
		byte[] bufferBytes = new byte[Protocol.MESSAGE_SIZE];
		buffer.get(bufferBytes);
		//TODO
		String hash="";
		try {
			hash = Utilities.SHA1FromBytes(bufferBytes);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		ClientInfo client = (ClientInfo) key.attachment();
		client.addToPendingWriteList(hash);
		key.interestOps(SelectionKey.OP_WRITE);
		//}
		//ClientInfo client = (ClientInfo) key.attachment();
		synchronized(client){
			client.setReading(false);
		}
		// TODO Auto-generated method stub

	}

}
