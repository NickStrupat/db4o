/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;


public class ReadOnly implements Runnable{
	
	private static final String FILE = "readonly.db4o";
	private static final int COUNT = 100;
	private static final String MY_STRING = "ReadOnly test instance ";
	
	public String myString;
	
	public static void main(String [] args){
		Db4o.configure().readOnly(true);
		new ReadOnly().spendSomeTime();
		Db4o.configure().readOnly(false);	
	}
	
	public void run(){
		setUp();
		test();
		Db4o.configure().readOnly(false);	
	}
	
	private void setUp(){
		new File(FILE).delete();
		ObjectContainer con = Db4o.openFile(FILE);
		for (int i = 0; i < COUNT; i++) {
			ReadOnly ro = new ReadOnly();
			ro.myString = MY_STRING + i;
			con.store(ro);
		}
		con.close();
	}
	
	private void test(){
		Db4o.configure().readOnly(true);
		checkCount();
		ObjectContainer con = Db4o.openFile(FILE);
		con.store(new ReadOnly());
		con.close();
		checkCount();
		try{
			/*
			Process pcs = Runtime.getRuntime().exec(new String[] {"javaw", "com.db4o.test.ReadOnly"}, null,  new File("D:\\db4o\\"));
			InputStream in = pcs.getInputStream();
			while(in.available() > 0){
				System.out.print(in.read());
			}
			*/
		}catch(Exception e){
		}
	}
	
	private void spendSomeTime(){
		Db4o.configure().readOnly(true);
		ObjectContainer con = Db4o.openFile(FILE);
		ObjectSet set = con.queryByExample(new ReadOnly());
		while(set.hasNext()){
			ReadOnly ro = (ReadOnly)set.next();
			if(ro.myString.equals(MY_STRING + "1")){
				System.out.println("O.K. " + ro.myString);
			}
			if(ro.myString.equals(MY_STRING + (COUNT - 1))){
				System.out.println("O.K. " + ro.myString);
			}
			synchronized(this){
				try{
					this.wait(50);
				}catch(Exception e){
				}
			}
		}
		con.close();
	}
	
	private void checkCount(){
		Db4o.configure().readOnly(true);
		ObjectContainer con = Db4o.openFile(FILE);
		int size = con.queryByExample(new ReadOnly()).size();
		if (size != COUNT){
			throw new RuntimeException("ReadOnly.test: unexpected number of objects:" + size);
		}
		con.close();
	}

}
