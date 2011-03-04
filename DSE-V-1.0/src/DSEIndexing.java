import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import dsePackage.IndexGUI;

public class DSEIndexing {
	static Logger myLogger=Logger.getLogger(DSEIndexing.class);
	public static void main(String[] args) {
		BasicConfigurator.configure();
		IndexGUI gui=new IndexGUI();
		gui.setVisible(true);
	}
}
