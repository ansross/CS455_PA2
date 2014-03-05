package cs455.scaling.client;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class ClientInfo {
	private String hostName;
	private int portNum;
	
	private boolean isWriting;
	private boolean isReading;
	
	private Lock writeListLock = new ReentrantLock();
	private ArrayList<byte[]> pendingWriteList;

	public ClientInfo(SocketChannel channel) {
		isWriting=false;
		isReading=false;
		pendingWriteList = new ArrayList<byte[]>();
		
		//INITIALIZE HOST AND PORT
		
		
		// TODO Auto-generated constructor stub
	}

	public void addToPendingWriteList(String hash) {
		try{
			writeListLock.lock();
			pendingWriteList.add(hash.getBytes());
		}finally{
			writeListLock.unlock();
		}
		// TODO Auto-generated method stub
		
	}
	
	public boolean isWriting(){
		return isWriting;
	}
	
	public boolean isReading(){
		return isReading;
	}
	
	public void setReading(boolean reading){
		isReading = reading;
	}
	
	public void setWriting(boolean writing){
		isWriting = writing;
	}
	
	public String toString(){
		return hostName + portNum;
	}

	public ArrayList<byte[]> getPendingWriteList() {
		// TODO Auto-generated method stub
		return pendingWriteList;
	}

}
