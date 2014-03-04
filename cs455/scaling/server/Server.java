package cs455.scaling.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cs455.scaling.client.ClientInfo;
import cs455.scaling.task.*;
import cs455.scaling.threadpool.ThreadPoolManager;
import cs455.scaling.util.Protocol;



public class Server implements ServerNode {
	private final int port = 15006;
	private final int numThreads = 5;

	private ThreadPoolManager threadPoolMgr;
	private Lock selectorLock = new ReentrantLock();
	private Selector selector;
	private ServerSocketChannel servSockCh;

	public Server() throws IOException{
		//create selector
		selector= Selector.open();
		threadPoolMgr = new ThreadPoolManager(numThreads);
	}
	
	public void initiateServer(){
		threadPoolMgr.initiateThreadPoolManager();
	}

	public static void main(String [] args){
		try {
			Server server = new Server();
			server.initiateServer(); 
			
			server.startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void startServer() throws IOException{
		if(Protocol.DEBUG){
			System.out.println("Started Server");
		}
		System.out.println("1");
		servSockCh = ServerSocketChannel.open();
		servSockCh.configureBlocking(false);
		System.out.println("2");
		servSockCh.socket().bind(new InetSocketAddress(port));
		servSockCh.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("3");
		while(true){
			java.util.Iterator<SelectionKey> keyIter;
			try{
				selectorLock.lock();
				this.selector.select();
				keyIter = this.selector.selectedKeys().iterator();
			} finally{
				selectorLock.unlock();
			}
			while(keyIter.hasNext()){;
				SelectionKey key = keyIter.next();
				//determine type of task that is ready
				Task newTask=null;
				if(key.isAcceptable()){
					newTask = new ConnectTask(key, this);
				}
				//TODO need?
				//nope?
			//	else if(key.isConnectable()){
				//	this.connect(key);
				//}
				else if(key.isWritable()){
					newTask = new SendTask(key);
				}
				else if(key.isReadable()){
					newTask = new ReadTask(key, this);
				}
				if(newTask != null){
					//give that task to thread pool to handle
					this.threadPoolMgr.addTask(newTask);
				} else{
					System.out.println("Error in server: task is null!");
				}
				keyIter.remove();
				threadPoolMgr.assignNextTask();
			}
			threadPoolMgr.assignNextTask();
		}

	}
	
	

	private void read(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	private void write(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	private void accept(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}

	private void monitorChannels(){
		while(true){
			try {
				int readyChannels = selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void registerChannel(SocketChannel channel, ClientInfo client) throws ClosedChannelException {
		try{
			selectorLock.lock();
			channel.register(selector, SelectionKey.OP_READ, client);
		} finally{
			selectorLock.unlock();
		}
		// TODO Auto-generated method stub
		
	}

	public void disconnect(SelectionKey key) {
		// TODO Auto-generated method stub
		
	}
}
