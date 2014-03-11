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



public class Server {
	private final int port;
	private final int numThreads;

	private ThreadPoolManager threadPoolMgr;
	private Lock selectorLock = new ReentrantLock();
	private Selector selector;
	private ServerSocketChannel servSockCh;

	public Server(int portNum, int numThreads) throws IOException{
		//create selector
		selector= Selector.open();
		this.port=portNum;
		this.numThreads=numThreads;
		threadPoolMgr = new ThreadPoolManager(this.numThreads);
	}

	public void initiateServer(){
		//initiate all threads after thread pool is constructed. 
		//could move this to the Server ctor
		threadPoolMgr.initiateThreadPoolManager();
	}

	public static void main(String [] args){
		if(args.length!=2){
			System.out.println("Usage: java css455.scaling.server.Server <portNum> <thread-pool-size>");
			return;
		}
		else{
			try {
				Server server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
				server.initiateServer(); 

				server.startServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void startServer() throws IOException{


		servSockCh = ServerSocketChannel.open();
		servSockCh.configureBlocking(false);
		servSockCh.socket().bind(new InetSocketAddress(port));
		servSockCh.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("Started Server");
		int numTasks = 0;
		int numRead = 0;
		while(true){
			java.util.Iterator<SelectionKey> keyIter;
			try{
				selectorLock.lock();
				this.selector.select();
				keyIter = this.selector.selectedKeys().iterator();
				if(Protocol.DEBUG){
					//System.out.println("num keys: "+this.selector.selectedKeys().size());
				}
			} finally{
				selectorLock.unlock();
			}

			while(keyIter.hasNext()){
				numTasks++;
				SelectionKey key = keyIter.next();

				//determine type of task that is ready
				//reading and writing tasks go to thread pool
				//connections established in the server
				//at most one write and one rad can exist for a single 
				//client
				Task newTask=null;
				if(key.isAcceptable()){
					//System.out.println("making connection task");
					//System.out.println(key.toString());
					accept(key);
					//newTask = new ConnectTask(key, this);
				}
				else if(key.isWritable()){
					//System.out.println("Client is ready to write");
					ClientInfo client = (ClientInfo) key.attachment();
					if(client == null){
						System.out.println("client not attached to key");
					}
					if(!client.isWriting()){
						//System.out.println("making writable");
						newTask = new SendTask(key);
					}
				}
				else if(key.isReadable()){
					//System.out.println("Clinet is realdy to read");
					ClientInfo client = (ClientInfo) key.attachment();
					if(client == null){
						System.out.println("client not attached to key");
					}
					if(!client.isReading()){
						//System.out.println("making read task");
						newTask = new ReadTask(key, this);
					}
				}
				if(newTask != null){
					//give that task to thread pool to handle
					this.threadPoolMgr.addTask(newTask);
				} else{
					//System.out.println("Error in server: task is null!");
				}
				keyIter.remove();

			}

			threadPoolMgr.assignNextTask();
		}

	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
		SocketChannel channel = servSocket.accept();
		ClientInfo client = new ClientInfo(channel);

		System.out.println("Accepting incoming connection");
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ, client);
		// TODO Auto-generated method stub

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
