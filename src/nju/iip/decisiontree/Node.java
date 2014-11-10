package nju.iip.decisiontree;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @description 节点类
 * @time 2014-11-05
 * @author wangqiang
 *
 */
public class Node {
	
	private int attribute;//分裂属性
	
	private ArrayList<ArrayList<Double>> docList;//元组集合
	
	private Double classify;//节点所属类别
	
	private Node leftChild;//左子树
	
	private Node rightChild;//右子树
	
	private HashMap<Double,Node>child_nodes;//子节点集合<属性值，对应分支节点>
	
	
	public void set_child_nodes(HashMap<Double,Node>child_nodes){
		this.child_nodes=child_nodes;
	}
	
	public HashMap<Double,Node>get_child_nodes(){
		return this.child_nodes;
	}
	
	public Node(){
		this.leftChild=null;
		this.rightChild=null;
		this.docList=null;
	}
	
	public Node(ArrayList<ArrayList<Double>> a){
		this.docList=a;
		this.child_nodes=new HashMap<Double,Node>();
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
	
	public void setClassify(Double a){
		this.classify=a;
	}
	
	public Double getClassify(){
		return this.classify;
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
