package com.ego.singleton;

public class User {

	private static User user = new User();
	
	private static String username = null;
	
	private static String address = null;
	
	private static String displayName = null;

    private static String email = null;

    private static boolean isSubscribed;
	
	public static User getInstance(){
		return user;
	}
	
	public static void setUserDetails(String e, String u, String a, String dn){
        email = e;
		username = u;
		address = a;
		displayName = dn;
	}
	
	public static String getUsername(){
		return username;
	}
	
	public static String getAddress(){
		return address;
	}
	
	public static String getDisplayName(){
		return displayName;
	}

    public static String getEmail() { return email; }
	
	public static void setDisplayName(String dn){
		displayName=dn;
	}

    public static boolean isSubscribed(){
        return isSubscribed;
    }

    public static void setIsSubscribed(boolean is){
        isSubscribed = is;
    }
	
	
}
