package cs455.scaling.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cs455.scaling.util.Protocol;
import cs455.scaling.util.Utilities;

public class TCPSenderThread implements Runnable {
	private Socket socket;
	private DataOutputStream dout;
	private volatile boolean complete;
	private final double msgRate;
	private final Client client;
	

	public TCPSenderThread(Socket socketArg, double msgRateArg, Client client) throws IOException{
		this.client = client;
		this.msgRate = msgRateArg;
		this.socket = socketArg;
		complete = false;
		dout = new DataOutputStream(socket.getOutputStream());
	}

	public void sendMessage(byte[] dataToSend) throws IOException{
		try{
			dout.write(dataToSend);//, 0, dataLength);
			//System.out.println("send point 1.3");
			dout.flush();
			System.out.println("Sent message");
		}catch(IOException ioe){
			System.out.println("Sender IOE");
			ioe.printStackTrace();
		}
	}

	@Override
	public void run() {
		//TODO CHANGE BACK!!!
		//while(!complete)
		for(int i=0; i<10; i++){
			byte[] message = createAndHashMessage();
			try {
				sendMessage(message);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try{
				Thread.sleep((long) (1000.0/msgRate));
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			
		}
		
		// TODO Auto-generated method stub
		
	}

	private byte[] createAndHashMessage(){
		//generate a message
		byte [] message = new byte[Protocol.MESSAGE_SIZE];
		//CHANGE BACK!!!
		(new Random()).nextBytes(message);
		//calculate and store hash
		String hash=null;
		try {
			hash = Utilities.SHA1FromBytes(message);
			client.addHash(hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Created hash: "+hash);
		
		/*
		if(hash.bitLength()!=40){
			System.out.println("Hash isn't length 40");
			System.out.println("Hashes are malfunctioning, client will die");
			client.setComplete(true);
		}*/
		
		return message;
	}
	
}
