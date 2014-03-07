package cs455.scaling.client;

import java.io.DataOutputStream;
import java.io.IOException;
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
			System.out.println("Sent message: "+new String(dataToSend));
		}catch(IOException ioe){
			System.out.println("Sender IOE");
			ioe.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(!complete){
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
		(new Random()).nextBytes(message);
		//calculate and store hash
		String hash="hash";
		try {
			hash = Utilities.SHA1FromBytes(message);
			client.addHash(hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(Protocol.DEBUG){
			System.out.println("Created hash: "+hash);
		}
		if(hash.length()!=40){
			System.out.println("Hash isn't length 40");
		}
		
		return message;
	}
	
}
