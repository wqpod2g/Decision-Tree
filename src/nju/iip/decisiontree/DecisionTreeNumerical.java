package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * @description 连续属性值决策树
 * @author wangqiang
 * @since 2014-11-10
 */
public class DecisionTreeNumerical {
	/**
	 * 测试数据路径
	 */
	private static String filePath="Benchmark Dataset/segment.data";
	
	private static ArrayList<Integer>attribte_list=new ArrayList<Integer>();//属性集合
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	private static int attribte_list_size=0;//属性个数
	

	/**
	 * @获取整个样本的特征矩阵集合
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>>getAllMatrix(){
		try {
			allMatrix=Tools.readFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		attribte_list_size=allMatrix.get(0).size()-1;//计算属性个数
		return allMatrix;
	}
	
	
	/**
	 * @description 创建属性列表
	 * @return
	 */
	public static ArrayList<Integer>getAttribte_list(){
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
	 * @description 计算在某个属性下最佳划分的gini与split_point
	 * @param i
	 * @param lefeDocList
	 * @param rightDocList
	 * @return ArrayList<Double>{gini,split_point}
	 */
	public static ArrayList<Double>get_attribute_gini(int i,ArrayList<ArrayList<Double>>D,ArrayList<ArrayList<Double>>lefeDocList,ArrayList<ArrayList<Double>>rightDocList){
		ArrayList<Double> divide_result=new ArrayList<Double>();
		 
		return divide_result;
	}
	
	/**
	 * @description 计算最好的分裂属性
	 * @param N
	 * @param attribte_list
	 * @return 最好的分裂属性
	 */
	public static int Attribute_selection_method(Node N,Node leftChild,Node rightChild){
		int attribute=0;
		double split_point=0.0;
		double gini=Double.POSITIVE_INFINITY;
		ArrayList<ArrayList<Double>>D=N.getDocList();//点N所包含的元组
		int attribte_list_size=attribte_list.size();
		for(int i=0;i<attribte_list_size;i++){
			ArrayList<ArrayList<Double>>lefeDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>rightDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<Double> divide_result=get_attribute_gini(i,D,lefeDocList,rightDocList);//返回在某个属性划分下gini与split_point
			double temp=divide_result.get(0);
			if(temp<gini){
				split_point=divide_result.get(1);
				attribute=i;
				gini=temp;
				leftChild.setDocList(lefeDocList);
				rightChild.setDocList(rightDocList);
			}
		}
		N.setAttribute(attribute);
		N.set_split_point(split_point);
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
	 * @return 一个点的Gini值
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
				Node left=new Node();
				Node right=new Node();
				int attribute=Attribute_selection_method(N,left,right);
				if(left.getDocList().size()==0||right.getDocList().size()==0){
					N.setLeftChild(null);
					N.setRightChild(null);
					N.setClassify(nodeClassify(N));
				}
				else{
					N.setAttribute(attribute);
					N.setLeftChild(left);
					N.setRightChild(right);
					getDecisionTree(left);
					getDecisionTree(right);
				}
			}
		}
	}
	
	public static void main(String[] args){
		getAllMatrix();
		for(int i=0;i<allMatrix.size();i++){
			allMatrix.get(i).remove(2);
		}
		for(int i=0;i<allMatrix.size();i++){
			System.out.println(allMatrix.get(i).get(2));
		}
	}

}
