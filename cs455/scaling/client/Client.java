package cs455.scaling.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.*;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import cs455.scaling.util.Protocol;
import cs455.scaling.util.Utilities;

//20 char response
public class Client {
	//private LinkedList<String> sentHashCodes;
	private LinkedList<String> sentHashCodes;
	private double msgRate;
	private int serverPort;
	private String serverHostName;
	private Socket sock;
	private Thread senderThread;
	private volatile boolean complete;

	public Client(String hostArg, int portArg, int rateArg) throws IOException{
		complete=false;
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

			Client client;
			try {
				client = new Client(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
				client.startClient();
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


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
		//a thread takes care of sending
		try {
			senderThread = new Thread(new TCPSenderThread(sock, msgRate, this));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		senderThread.start();
		while(!sock.isConnected()){
			//waits for a read to be ready
			int i=0;

			DataInputStream din;
			try {
				din = new DataInputStream(sock.getInputStream());
				//chars are 2 bytes, 20 chars per hash
				int dataLength = 2*20;

				if(sock!=null){
					byte [] responseHash = new byte[dataLength];
					din.readFully(responseHash, 0, dataLength);
					checkHashes(new String(responseHash));

				}
			}

			catch (IOException ioe){
				ioe.printStackTrace();

			}
			//for(int i=0; i<5; ++i)


			//DELETE ME!!
			//while(true){}
		}
		System.out.println("Socket is disconnected");

	}

	public void setComplete(boolean comp){
		complete=comp;
	}


	private void checkHashes(String response){
		if(Protocol.DEBUG){
			System.out.println("Client received hash: " + response);
		}
		synchronized(sentHashCodes){
			int respIndex = sentHashCodes.indexOf(response);
			//if were waiting for such a hash code, remove it from the list
			if(respIndex != -1){
				System.out.println("Received hash match!  "+response+'\n');
				sentHashCodes.remove(respIndex);
			}

			else{
				System.out.println("Client recieved unexpected hash "+response+"\n");
			}
		}

	}

	private void establishConnection() throws IOException{
		sock = new Socket(this.serverHostName, this.serverPort);
		//sock.configureBlocking(false);
		//sock.configureBlocking(false);
		//sock.connect(new InetSocketAddress(this.serverHostName, this.serverPort));
	}

	public void addHash(String hash){
		synchronized(sentHashCodes){
			sentHashCodes.add(hash);
		}
	}
}
