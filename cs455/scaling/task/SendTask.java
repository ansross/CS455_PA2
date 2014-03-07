package cs455.scaling.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import cs455.scaling.client.ClientInfo;
import cs455.scaling.util.Protocol;

//is writable
public class SendTask implements Task{
	SelectionKey key;

	public SendTask(SelectionKey keyArg){
		ClientInfo client = (ClientInfo) keyArg.attachment();
		synchronized(client){
			client.setWriting(true);
			this.key=keyArg;
		}
	}

	@Override
	public void execute() {
		if(Protocol.DEBUG){
			System.out.println("I am writing");
		}

		ClientInfo client = (ClientInfo) key.attachment();
		//synchronized(client){

		SocketChannel channel = (SocketChannel) key.channel();
		ArrayList<byte []> writeList = client.getPendingWriteList();
		System.out.println("write list length: "+writeList.size());
		synchronized(writeList){
			for(byte[] data : writeList){

				try {
					System.out.println("Sending hash "+new String(data) + 
							" to " + client.toString());
					ByteBuffer buffer = ByteBuffer.wrap(data);
					channel.write(buffer);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			writeList.clear();
		}
		key.interestOps(SelectionKey.OP_READ);
		synchronized(client){
			client.setWriting(false);
		}

		//}

		// TODO Auto-generated method stub

	}


}
