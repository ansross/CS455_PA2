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
//read message from client
//produce hash from it
//add that hash to be eventually sent back to client
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
		ClientInfo client = (ClientInfo) key.attachment();
		System.out.println("Received a message from "+client.toString()+
				"\n Creating hash \n");
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
		String hash=null;
		try {
			hash = Utilities.SHA1FromBytes(bufferBytes);
			if(hash.length() != 40){
				//System.out.println("generated hash not 40 chars, all is lost");
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		client.addToPendingWriteList(hash.getBytes());
		key.interestOps(SelectionKey.OP_WRITE);
		synchronized(client){
			client.setReading(false);
		}
	}

}
