package com.javachallenge;

import com.Ostermiller.util.MD5;

public class CheckSumThread extends Thread{
	long idx;
	String uid;
	
	public CheckSumThread(long idx, String uid){
		super(String.format("CheckSumThread%d", idx));
		this.idx = idx;
		this.uid = uid;
	}
	
	@Override
	public void run(){
		try{
			String md5hash = MD5.getHashString(uid);
			
			System.out.println(String.format("Thread : %s, idx : %d MD5 : %s", this.getName(), idx, md5hash));	
		}catch (Exception e){
			System.out.println("Error in thread");
			e.printStackTrace();
		}
		
	}
}