package nju.iip.decisiontree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Tools {
	
	
	/**
	 * @decription 从文件中读入数据
	 * @param filePath
	 * @return 
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>> readFile(String filePath) throws IOException{
		ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		 String line = br.readLine();
		 while(line != null){  
	        	String[] str=line.split(",");
	        	ArrayList<Double>vector=new ArrayList<Double>();
	        	for(int i=0;i<str.length;i++){
	        		vector.add(Double.parseDouble(str[i]));
	        	}
	        	allMatrix.add(vector);
	            line = br.readLine();    
	        }
	        br.close();
		return allMatrix;
	}

}
