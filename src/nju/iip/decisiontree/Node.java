package nju.iip.decisiontree;

import java.util.ArrayList;

public class Node {
	
	private int attribute;//分裂属性
	
	private ArrayList<ArrayList<Double>> docList;//元组集合
	
	private Double split_point;//划分标准
	
	private Node leftChild;//左子树
	
	private Node rightChild;//右子树
	
	
	public Node(){
		this.leftChild=null;
		this.rightChild=null;
		this.docList=null;
	}
	
	public void setAttribute(int a){
		this.attribute=a;
	}
	
	public int getAttribute(){
		return this.attribute;
	}
	
	public void setDocList(ArrayList<ArrayList<Double>> a){
		this.docList=a;
	}
	
	public ArrayList<ArrayList<Double>> getDocList(){
		return this.docList;
	}
	
	public void setSplit_point(Double a){
		this.split_point=a;
	}
	
	public Double getSplit_point(){
		return this.split_point;
	}
	
	public void setLeftChild(Node a){
		this.leftChild=a;
	}
	
	public Node getLeftChild(){
		return this.leftChild;
	}
	
	public void setRightChild(Node a){
		this.rightChild=a;
	}
	
	public Node getRightChild(){
		return this.rightChild;
	}

}
