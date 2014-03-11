Annie Ross
CS455--Spring 2014
Assignment 2

to compile all files:
	% make all

to run server:
	% java cs455.scaling.server.Server <port-number> <thread-pool-size>

to run client:
	% java cs455.scaling.client.Client <server-host-name> <server-port-number> <message-rate>

File Breakdown:
  .client:
     Client: main client class for sending, storing and checking hashes, no
	clean way to quit
     ClientInfo: stores information abouta client used by the server to check
	what hashes need to be sent and if it is currently being serviced
     TCPSenderThread: used by the Client to send messages at specified rate
  .server:
     Server: main server class, contains a thread pool manager for delegation
	of tasks
  .threadpool:
     ThreadPoolManager: creates and tracks threads. Keeps queue and delegates
	tasks to free threads
     WorkerThread: completes tasks
  .task:
     Task: interface for all tasks that a worker thread may complete
     ReadTask: created when a client has sent a message that needs to be read
	from channel
     WriteTask: created when a client is ready to recieve hashes that the
	server has created 
