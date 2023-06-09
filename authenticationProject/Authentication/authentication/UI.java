package authentication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class UI {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);
		System.out.print("What would you like to do: Create Account (C) or Log In (L)? Anything"
				+ " else to quit: ");
		String userRequest = scan.nextLine();
		while(userRequest.toLowerCase().equals("c") || userRequest.toLowerCase().equals("l")) {
			if (userRequest.toLowerCase().equals("c")) {
				createAccount(scan);
			}
			else {
				System.out.print("Enter Your Username: ");
				String username = scan.nextLine();
				System.out.print("Enter Your Password: ");
				String password = scan.nextLine();
				boolean successfulLogin = login(username, password);
				if (successfulLogin)
					System.out.println("Successfully Logged In");
				else
					System.out.println("Incorrect Login");
				
			}
			System.out.print("What would you like to do: Create Account (C) or Log In (L)? Anything"
					+ " else to quit: ");
			userRequest = scan.nextLine();
		}
		
		scan.close();

	}
	
	public static void createAccount(Scanner scan) {
		System.out.print("Enter Your Username: ");
		String username = scan.nextLine();
		System.out.print("Enter Your Password: ");
		String password = scan.nextLine();
		String salt = getSalt();
		String hashedPassword = getHashedPassword(password, salt);
		try {
			  String accountsFilePath = "authenticationFiles/Accounts.txt";
			  File accountsFile = new File(accountsFilePath);
			  String accounts = readMessage(accountsFile);
			  String newAccountInfo = username + ":" + hashedPassword + ":" + salt;
			  accounts += newAccountInfo + "_";
			  PrintWriter accountsOut = new PrintWriter(accountsFile);
		      String[] accountsArray = accounts.split("_");
		      for(String account: accountsArray)
		    	  accountsOut.println(account);
		      accountsOut.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	public static String readMessage(File file) throws FileNotFoundException {
		Scanner fromMessage = new Scanner(file);
        
        String message = "";
        while(fromMessage.hasNext())
            message += fromMessage.next() + "_";
        
        fromMessage.close();
        
        return message;
	}
	
	public static String getSalt() {
		String salt = "";
		for (int i = 0; i < 10; i++) {
			Character randomValue = (char) ((int) (Math.random() * 25) + 65);
			salt += randomValue;
		}
		
		return salt;
		
	}
	
	public static String getHashedPassword(String password, String salt) {
		String valueToHash = password += salt;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(valueToHash.getBytes());
	        BigInteger no = new BigInteger(1, messageDigest);
	        String hashtext = no.toString(16);
	        while (hashtext.length() < 32) {
	            hashtext = "0" + hashtext;
	        }
	        return hashtext;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
        
	}
	
	public static boolean login(String username, String password) throws FileNotFoundException {
		String accountsFilePath = "authenticationFiles/Accounts.txt";
		File accountsFile = new File(accountsFilePath);
		String accounts = readMessage(accountsFile);
		String[] accountsArray = accounts.split("_");
		for (String account : accountsArray) {
			String[] accountArray = account.split(":");
			if (username.equals(accountArray[0])) {
				String salt = accountArray[2];
				String hashedPassword = getHashedPassword(password, salt);
				return hashedPassword.equals(accountArray[1]);
			}
		}
		return false;
	}

}
