import java.nio.file.Paths;
import java.nio.file.Path;

import dsePackage.WatchDir;

public class ReIndexDaemon {
	public static void main(String[] args) {
		String test="D:\\Eclipse Examples\\testData\\test";
		try{
			new WatchDir(Paths.get(test),true).processEvents();
		}
		catch(Exception e){System.out.println(e.getMessage());}
	}
}
