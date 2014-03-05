package cs455.scaling.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import cs455.scaling.util.Protocol;
import cs455.scaling.util.Utilities;

public class Client {
	private LinkedList<String> sentHashCodes;
	private double msgRate;
	private int serverPort;
	private String serverHostName;
	private SocketChannel sock;
	
	public Client(String hostArg, int portArg, int rateArg){
		this.msgRate = rateArg;
		this.serverHostName = hostArg;
		this.serverPort = portArg;
		sentHashCodes = new LinkedList<String>();
	}
	
	//@args: server-host, server-port, message-rate
	public static void main(String [] args){
		if(args.length!=3){
			System.out.println("Arguments:\n <server-host> <server-port> <message-rate>");
			
		}
		else{
			
			Client client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			client.startClient();
			
		}
		
	}
	
	public void startClient(){
		try {
			establishConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(Protocol.DEBUG){
			System.out.println("Connected");
		}
		//while(!sock.isConnected()){;}
		Random rand = new Random();
		
		//while(true)
		for(int i=0; i<5; ++i)
		{
			if(Protocol.DEBUG){
				System.out.println("Pending hashes: "+sentHashCodes.size());
			}
			ByteBuffer buf = ByteBuffer.allocate(Protocol.MESSAGE_SIZE);
			checkForResponse(buf);
			//check for response
			
			
			System.out.println("6");
			
			//write new message
			writeMessage(buf);
			
			
			//sleep to establish message rate
			try {
				Thread.sleep((long) (1000.0/msgRate));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Protocol.DEBUG){
				System.out.println("Woke up");
			}
			
		}
		//DELETE ME!!
		while(true){}
		
		
	}
	
	private void writeMessage(ByteBuffer buf){
		byte[] message = createAndHashMessage();
		//write message
		writeMessageToBuffer(buf, message);
	}
	
	public void writeMessageToBuffer(ByteBuffer buf, byte[] message){
		buf.put(message);
		buf.flip(); //read to read into channel
		//write message
		while(buf.hasRemaining()){
			try {
				sock.write(buf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(Protocol.DEBUG){
			System.out.println("Sent Message");
		}
	}
	private byte[] createAndHashMessage(){
		//generate a message
		byte [] message = new byte[Protocol.MESSAGE_SIZE];
		(new Random()).nextBytes(message);
		//calculate and store hash
		String hash="hash";
		try {
			hash = Utilities.SHA1FromBytes(message);
			sentHashCodes.add(hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	private void checkForResponse(ByteBuffer buf) {
		int bytesRead=0;
		try {
			System.out.println("2");
			//TODO ??!?!?!?!?! WHERE DO THE FLIPS GO?
			buf.flip();
			bytesRead = sock.read(buf);
			System.out.println("3");
			buf.flip();
			System.out.println("4");
			if(bytesRead == -1){
				System.out.println("Connection Closed in Client");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//if have message, check awaited hashes
		if(bytesRead >0){
			checkHashes(buf);
		}
		
		buf.clear();
		// TODO Auto-generated method stub
		
	}
	
	private void checkHashes(ByteBuffer buf){
		System.out.println("5");
		byte[] resBytes = new byte[Protocol.MESSAGE_SIZE];
			buf.get(resBytes);
		String response = new String(resBytes);
		int respIndex = sentHashCodes.indexOf(response);
		//if were waiting for such a hash code, remove it from the list
		if(respIndex != -1){
			sentHashCodes.remove(respIndex);
		}
		else{
			System.out.println("Client recieved unexpected hash");
		}
		
	}

	private void establishConnection() throws IOException{
		sock = SocketChannel.open();
		//sock.configureBlocking(false);
		sock.connect(new InetSocketAddress(this.serverHostName, this.serverPort));
	}
}
