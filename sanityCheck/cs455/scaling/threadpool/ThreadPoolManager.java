package cs455.scaling.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cs455.scaling.task.Task;
import cs455.scaling.util.Protocol;

public class ThreadPoolManager {
	private int numThreads;
	private LinkedList<Task> pendingTasks;
	private Lock poolLock = new ReentrantLock();
	private ArrayList<Thread> threadPool;
	private ArrayList<WorkerThread> workers;
	private int numTasks;

	//will look through thread pool, if one is free assign to thread
	//when a thread finishes it changes its status to free
	//must lock pool 

	public ThreadPoolManager(int numThreadsArg){
		numTasks =0;
		numThreads=numThreadsArg;
		threadPool = new ArrayList<Thread>();
		workers = new ArrayList<WorkerThread>();
		pendingTasks = new LinkedList<Task>();
		if(Protocol.DEBUG){
			System.out.println("adding "+numThreads+" threads");
		}
		//create set number of threads
		for(int i=0; i<numThreads; ++i){
			WorkerThread wt = new WorkerThread();
			//System.out.println("adding worker");
			workers.add(wt);
			//System.out.println("adding thread");
			threadPool.add(new Thread(wt));
		}
	}

	public void initiateThreadPoolManager(){
		//start all threads
		for(Thread t: threadPool){
			t.start();
		}
	}

	private Task getNextTask(){
		Task retTask=null;
		try{
			poolLock.lock();
			if(!pendingTasks.isEmpty()){
				retTask=pendingTasks.pop();
			}
		}finally{
			poolLock.unlock();
		}
		return retTask;
	}
	
	private int getNumPendingTasks(){
		try{
			poolLock.lock();
			return pendingTasks.size();
		}finally{
			poolLock.unlock();
		}
	}

	public void addTask(Task newTask){
		try{
			poolLock.lock();
			pendingTasks.addLast(newTask);
		}finally{
			poolLock.unlock();
		}
	}

	//takes a task off the queue and gives it to a free thread if there is one, 
	// if no free worker thread, does nothing
	public void assignNextTask(){//Task newTask, WorkerThread wkr){
		//lock in case a thread finishes while looking
		try{ 
			poolLock.lock();
			//see if any pending tasks
			if(!pendingTasks.isEmpty()){
				//see if available thread to take care if pending task

				//try to find free thread in pool
				//if free worker, assign the next pending task to that thread
				WorkerThread taskThread = findFreeThread();
				//if there is a free thread, give it the task
				if(taskThread!=null){
					Task nextTask = getNextTask();
					//System.out.println("pendingTasks size" + pendingTasks.size());
					taskThread.giveTask(nextTask);
					
				}
				else{
					//System.out.println("No free threads");
				}
			}else{
				//System.out.println("No pending tasks");
			}
		}finally{
			poolLock.unlock();
		}
	}

	//goes through tread pool, if there is 
	//a free thread, returns it, else returns null
	private WorkerThread findFreeThread() {
		WorkerThread freeThread = null;
		for(WorkerThread thread: workers){
			if(thread.isFree()){
				freeThread = thread;
				break;
			}
		}
		return freeThread;
	}

}
