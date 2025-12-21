package com.trading.platform;

import com.warrenstrange.googleauth.GoogleAuthenticator;

public class Test {

	public static void main(String[] args) {
		GoogleAuthenticator auth = new GoogleAuthenticator();
		System.out.println(auth.getTotpPassword("KPDTBHFGE6DXPDBLNGMUGKXO2J6ACAY7"));
	}
	
}
