public class DSEDaemon extends Thread{
	
	public DSEDaemon(String name){
		this.setName(name);
	}
	
	public static void main(String[] args) {
		DSEDaemon thread1 =new DSEDaemon("DSEIndexing");
		DSEDaemon thread2 =new DSEDaemon("DSEWatchDirDaemon");
		thread1.start();
		thread2.start();
	}
	
	public void run() {
		System.out.println(Thread.currentThread());
		if(this.getName().equalsIgnoreCase("DSEIndexing")){
			DSEIndexing.main(null);
		}
		else if(this.getName().equalsIgnoreCase("DSEWatchDirDaemon")){
			DSEWatchDirDaemon.main(null);
		}
	}
}

