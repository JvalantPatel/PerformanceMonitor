package application;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		
		InetAddress iAddress = InetAddress.getLocalHost();
		String currentIp = iAddress.getHostAddress();
		System.out.println("Current IP address : " +currentIp); //gives only host address
	}

}
