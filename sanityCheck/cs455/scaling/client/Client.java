package cs455.scaling.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import cs455.scaling.util.Protocol;

//20 char response
public class Client {
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


		DataInputStream din;
		try {
			din = new DataInputStream(sock.getInputStream());
			//chars are 2 bytes, 20 chars per hash
			int dataLength = 2*20;

			//waits for a response to be reading using standard I/O
			while(sock!=null){
				//System.out.println("checking for response");
				byte [] responseHash = new byte[dataLength];
				din.readFully(responseHash, 0, dataLength);
				checkHashes(new String(responseHash));

			}
		}

		catch (IOException ioe){
			ioe.printStackTrace();

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
				System.out.println("Received hash match!  "+response+"\n Removing hash\n");
				sentHashCodes.remove(respIndex);
			}

			else{
				System.out.println("Client recieved unexpected hash "+response+"\n");
			}
		}

	}

	private void establishConnection() throws IOException{
		sock = new Socket(this.serverHostName, this.serverPort);
	}

	public void addHash(String hash){
		synchronized(sentHashCodes){
			sentHashCodes.add(hash);
		}
	}
}
