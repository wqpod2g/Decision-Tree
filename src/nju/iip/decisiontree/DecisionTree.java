package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


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
	
	private static ArrayList<Integer>attribte_list=new ArrayList<Integer>();//属性集合
	
	private static int attribte_list_size=0;//属性个数
	
	
	
	
	/**
	 * @获取整个样本的特征矩阵集合
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>>getAllMatrix() throws IOException{
		allMatrix=Tools.readFile(filePath);
		attribte_list_size=allMatrix.get(0).size()-1;//计算属性个数
		return allMatrix;
	}
	
	/**
	 * @description 创建属性列表
	 * @return
	 */
	public static ArrayList<Integer>getAttribte_list (){
		for(int i=0;i<allMatrix.get(0).size()-1;i++){
			attribte_list.add(i);
		}
		return attribte_list;
	}
	
	/**
	 * @decription 初始化根节点
	 * @return rootNode
	 * @throws IOException 
	 */
	public static Node getRootNode(ArrayList<ArrayList<Double>>matrix) throws IOException{
		Node rootNode=new Node(matrix);
		return rootNode;
	}
	
	/**
	 * @description 计算最好的分裂属性
	 * @param N
	 * @param attribte_list
	 * @return 最好的分裂属性
	 */
	public static int Attribute_selection_method(Node N){
		int attribute=0;
		Double gini=10.0;
		Node leftChild=new Node();
		Node rightChild=new Node();
		ArrayList<ArrayList<Double>>D=N.getDocList();//点N所包含的元组
		for(int i=0;i<attribte_list.size();i++){
			ArrayList<ArrayList<Double>>D1=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>D2=new ArrayList<ArrayList<Double>>();
			for(int j=0;j<D.size();j++){
				ArrayList<Double>vector=D.get(j);
				if(vector.get(i)==0){
					D1.add(vector);
				}
				else{
					D2.add(vector);
				}
			}
			Node N1=new Node(D1);
			Node N2=new Node(D2);
			Double tempGini=(D1.size()/N.getDocList().size())*getGini(N1)+(D2.size()/N.getDocList().size())*getGini(N2);
			if(tempGini<gini){
				gini=tempGini;
				attribute=i;
				leftChild.setDocList(D1);
				rightChild.setDocList(D2);
			}
		}
		N.setLeftChild(leftChild);
		N.setRightChild(rightChild);
		return attribute;
	}
	
	/**
	 * @description 节点N中所有元组的类别统计
	 * @param N
	 * @return HashMap<类别，元组数>
	 */
	public static HashMap<Double,Integer>statistics(Node N){
	    HashMap<Double,Integer>statisticsMap=new HashMap<Double,Integer>();
		ArrayList<ArrayList<Double>>matrix=N.getDocList();
		try{
			for(int i=0;i<matrix.size();i++){
				Double classify=matrix.get(i).get(attribte_list_size);//提取出元组类别
				if(statisticsMap.containsKey(classify)){
					statisticsMap.put(classify, statisticsMap.get(classify)+1);
				}
				else{
					statisticsMap.put(classify, 1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return statisticsMap;
	}
	
	/**
	 * @description 计算某个点的Gini指标（不纯度）
	 * @param N
	 * @return
	 */
	public static Double getGini(Node N){
		Double gini=0.0;
		Double temp=0.0;
		HashMap<Double,Integer>statisticsMap=statistics(N);
		Double D=1.0*N.getDocList().size();
		Set<Double>classifys=statisticsMap.keySet();
		
		for(Double classify:classifys){
			Double p=(statisticsMap.get(classify)/D);
			temp=temp+p*p;
		}
		gini=1-temp;
		return gini;
	}
	
	
	
	/**
	 * @description 计算某个叶子结点中帖子最多的类
	 * @param N
	 * @return
	 */
	public static Double nodeClassify(Node N){
		HashMap<Double,Integer>map=statistics(N);
		return Tools.sortMap(map);
	}

	/**
	 * @description 创建二叉树
	 * @param N
	 */
	public static void getDecisionTree(Node N){
		if(N!=null){
			if(getGini(N)<=0.5||attribte_list.size()==0){
				N.setLeftChild(null);
				N.setRightChild(null);
			    N.setClassify(nodeClassify(N));
			}
			else{
				int attribute=Attribute_selection_method(N);
				if(N.getLeftChild().getDocList().size()==0||N.getRightChild().getDocList().size()==0){
					N.setLeftChild(null);
					N.setRightChild(null);
					N.setClassify(nodeClassify(N));
				}
				else{
					//attribte_list.remove(attribute);//删除划分属性
					N.setAttribute(attribute);
					getDecisionTree(N.getLeftChild());
					getDecisionTree(N.getRightChild());
					
				}
			}
		}
	}
	
	
	
	/**
	 * @description 计算某篇帖子所属类别
	 * @param vector
	 * @return
	 */
	public static Double getResult(ArrayList<Double>vector,Node N){
		
		if(N.getLeftChild()==null&&N.getRightChild()==null){
			return N.getClassify();
		}
		
		else{
			if(vector.get(N.getAttribute())==0){
				return getResult(vector,N.getLeftChild());
			}
			
			else{
				return getResult(vector,N.getRightChild());
			}
		}
	}
	
	public static void process() throws IOException{
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int i=0;i<10;i++){
//			attribte_list.clear();
//			getAttribte_list();
			int count=0;
			ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
			Tools.divide(i, allMatrix, testSample, trainSample);
			Node N=getRootNode(trainSample);
			getDecisionTree(N);
			for(int j=0;j<testSample.size();j++){
				Double c1=testSample.get(j).get(attribte_list_size);
				Double c2=getResult(testSample.get(j),N);
				if(c1.equals(c2)){
					count++;
				}
			}
			System.out.println("第"+(i+1)+"折命中率为:"+count/100.0);
			resultList.add(count/100.0);
		}
		System.out.println("十折均值为:"+Tools.getMean(resultList));
	}
	
	
	public static void main(String[] args) throws IOException{
		getAllMatrix();
		attribte_list=getAttribte_list();
		process();
	
	}

}
