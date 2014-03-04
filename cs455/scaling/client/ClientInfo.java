package cs455.scaling.client;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientInfo {
	private String hostName;
	private int portNum;
	private Lock writeListLock = new ReentrantLock();
	private ArrayList<Long> pendingWriteList;

	public ClientInfo(SocketChannel channel) {
		
		
		// TODO Auto-generated constructor stub
	}

	public void addToPendingWriteList(long hash) {
		try{
			writeListLock.lock();
			pendingWriteList.add(hash);
		}finally{
			writeListLock.unlock();
		}
		// TODO Auto-generated method stub
		
	}

}
