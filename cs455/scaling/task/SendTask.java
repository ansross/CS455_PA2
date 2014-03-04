package cs455.scaling.task;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

//is writable
public class SendTask implements Task{
	SelectionKey key;
	
	public SendTask(SelectionKey keyArg){
		this.key=keyArg;
	}

	@Override
	public void execute() {
		SocketChannel channel = (SocketChannel) key.channel();
		
		// TODO Auto-generated method stub
		
	}
	
	
}
