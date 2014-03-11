package cs455.scaling.client;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


//class that hold this informatoin about a client
public class ClientInfo {
	private String hostName;
	private String portNum;
	//should only be reading or writing from a client
	//with one thread ata time. Don't read if already being
	//read from 
	private boolean isWriting;
	private boolean isReading;
	
	private Lock writeListLock = new ReentrantLock();
	//hashes waiting to be sent back
	private ArrayList<byte[]> pendingWriteList;

	public ClientInfo(SocketChannel channel) {
		isWriting=false;
		isReading=false;
		pendingWriteList = new ArrayList<byte[]>();
		hostName = channel.socket().getInetAddress().getHostName();
		portNum = channel.socket().getInetAddress().getHostAddress();
				
		// TODO Auto-generated constructor stub
	}

	public void addToPendingWriteList(byte[] bs) {
		try{
			writeListLock.lock();
			pendingWriteList.add(bs);
		}finally{
			writeListLock.unlock();
		}
		
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
		return hostName +":"+ portNum;
	}

	public ArrayList<byte[]> getPendingWriteList() {
		return pendingWriteList;
	}

}
