import dsePackage.CustomizationUI;

public class DSECustomizing {
	public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomizationUI().setVisible(true);
            }
        });
    }
}
