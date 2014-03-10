package cs455.scaling.task;

public class SampleTask implements Task {

	@Override
	public void execute() {
		//System.out.println("I've been executed!");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
