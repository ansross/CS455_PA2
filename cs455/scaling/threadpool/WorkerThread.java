package cs455.scaling.threadpool;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cs455.scaling.task.Task;
import cs455.scaling.util.Protocol;

public class WorkerThread implements Runnable {

	private boolean running;
	private boolean hasTask;
	private Task currentTask;
	private Lock stateLock = new ReentrantLock();

	public WorkerThread(){
		running=true;
		hasTask=false;
		currentTask=null;
	}

	public void giveTask(Task newTask) {
		if(hasTask){
			System.out.print("ERROR: giving task to busy thread");
		}
		else{
			try{
				this.stateLock.lock();
				hasTask=true;
				currentTask=newTask;
			}finally{
				this.stateLock.unlock();
			}
		}

		// TODO Auto-generated method stub

	}

	//sets status so that thread is ready for next task
	private void makeFree(){
		try{
			this.stateLock.lock();
			hasTask=false;
			currentTask=null;
			System.out.println("I am free: "+this.isFree());
		}finally{
			this.stateLock.unlock();
		}

	}

	public boolean isFree(){
		try{
			stateLock.lock();
			return !hasTask;
		}finally{
			stateLock.unlock();
		}
	}



	@Override
	public void run() {
		if(Protocol.DEBUG){
			System.out.println("I am running");
		}
		while(this.running){
			if(!this.isFree()){
				currentTask.execute();
				makeFree();
				if(Protocol.DEBUG){
				System.out.println("I completed a Task");
				}
			}
				

		}

		// TODO Auto-generated method stub

	}

	private void completeTask(){
		//TODO
	}

}
