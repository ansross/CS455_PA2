package cs455.scaling.client;

import java.io.DataInputStream;
import java.io.IOException;
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
	private LinkedList<String> sentHashCodes;
	private double msgRate;
	private int serverPort;
	private String serverHostName;
	private Socket sock;
	private Thread senderThread;

	public Client(String hostArg, int portArg, int rateArg) throws IOException{
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
		//while(!sock.isConnected()){;}
		//waits for a read to be ready
		int i=0;

		DataInputStream din;
		try {
			din = new DataInputStream(sock.getInputStream());

			boolean complete=false;
			//chars are 2 bytes, 20 chars per hash
			int dataLength = 2*20;
			while(!complete){
				if(sock!=null){
					byte [] responseHash = new byte[dataLength];
					din.readFully(responseHash, 0, dataLength);
					checkHashes(new String(responseHash));

				}
			}

		}	catch (IOException ioe){
			ioe.printStackTrace();
		}
		//for(int i=0; i<5; ++i)


		//DELETE ME!!
		//while(true){}


	}


	/*

	private void checkForResponse(ByteBuffer buf) {
		//ByteBuffer buf = ByteBuffer.allocate(Protocol.MESSAGE_SIZE);

		System.out.println("checking for response");
		int bytesRead=0;
		try {
			System.out.println("1");

			//TODO ??!?!?!?!?! WHERE DO THE FLIPS GO?
			buf.flip();
			if(Protocol.DEBUG){
				System.out.println("buf.hasRemaining(): "+(buf.hasRemaining())+"/n read!=-1" + (bytesRead != -1));
			}
			while(buf.hasRemaining() && bytesRead !=-1){
				System.out.println("2");
				bytesRead = sock.read(buf);
			}

			System.out.println("3");
			if(bytesRead == -1){
				System.out.println("Connection Closed in Client");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(bytesRead == -1){
			// connection terminated
		}


		//if have message, check awaited hashes
		if(bytesRead >0){
			System.out.println("4");
			checkHashes(buf);
		}

		buf.clear();
		// TODO Auto-generated method stub

	}
	 */
	private void checkHashes(String response){
		if(Protocol.DEBUG){
			System.out.println("Client received hash: " + response);
		}
		synchronized(sentHashCodes){
			int respIndex = sentHashCodes.indexOf(response);
			//if were waiting for such a hash code, remove it from the list
			if(respIndex != -1){
				System.out.println("Received hash match! "+response);
				sentHashCodes.remove(respIndex);
			}

			else{
				System.out.println("Client recieved unexpected hash");
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
