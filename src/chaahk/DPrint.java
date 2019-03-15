package chaahk;

public class DPrint {

	public void print() {
		System.out.println();
	}

	public void print(String str) {
		System.out.println(str);
	}

	public void print(String str, double val) {
		System.out.print(str);
		System.out.print(" is ");
		System.out.println(val);
	}

	public void print(String str, double val, boolean condition) {
		if (condition) {
			System.out.print(str);
			System.out.print(" is ");
			System.out.println(val);
		}
	}
}
