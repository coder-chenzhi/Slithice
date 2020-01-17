package jqian.slicer.util;


public class ErrorPrinter {
	public static void printError(String msg){
		System.out.println(msg);
	}
	
	public static void printError(Exception e){
		System.out.println(e.getMessage());
	}
}
