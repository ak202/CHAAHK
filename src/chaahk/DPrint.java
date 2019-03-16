package chaahk;

public class DPrint {

	public static void print() {
		System.out.println();
	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static void print(String str, double val) {
		System.out.print(str);
		System.out.print(" is ");
		System.out.println(val);
	}

	public static void print(String str, double val, boolean condition) {
		if (condition) {
			System.out.print(str);
			System.out.print(" is ");
			System.out.println(val);
		}
	}
}
