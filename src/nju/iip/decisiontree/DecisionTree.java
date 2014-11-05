package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @decription 决策树算法实现
 * @time 2014-11-5
 * @author wangqiang
 *
 */
public class DecisionTree {
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	/**
	 * 测试数据路径
	 */
	private static String filePath="Benchmark Dataset/lily.data";
	
	private static Node rootNode=new Node();//根节点
	
	
	private static ArrayList<Integer>attribte_list=new ArrayList<Integer>();
	
	/**
	 * @获取整个样本的特征矩阵集合
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>>getAllMatrix() throws IOException{
		allMatrix=Tools.readFile(filePath);
		return allMatrix;
	}
	
	
	/**
	 * @decription 初始化根节点
	 * @return rootNode
	 * @throws IOException 
	 */
	public Node getRootNode() throws IOException{
		rootNode.setDocList(getAllMatrix());
		return rootNode;
	}
	
	/**
	 * @description 计算最好的分裂属性
	 * @param N
	 * @param attribte_list
	 * @return
	 */
	public static int Attribute_selection_method(Node N,ArrayList<Integer> attribte_list){
		int attribute=0;
		return attribute;
	}
	
	/**
	 * @description 计算某个点的信息熵
	 * @param N
	 * @return
	 */
	public static Double getEntropy(Node N){
		Double entropy=0.0;
		return entropy;
	}
	
	
	public static void partion(Node N){
		
	}
	
	
	/**
	 * @description 创建二叉树
	 * @param N
	 */
	public static void getDecisionTree(Node N){
		if(N!=null){
			int attribute=Attribute_selection_method(N,attribte_list);
			getDecisionTree(N.getLeftChild());
			getDecisionTree(N.getRightChild());
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		getAllMatrix();
		//System.out.println(allMatrix.get(2).size());
	}

}
