import java.text.DecimalFormat;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int numChar = 10;
		String output = "$"+numChar*.100;
		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println(output);
		System.out.println("\\r\\n\\r\\n");
	}

}
