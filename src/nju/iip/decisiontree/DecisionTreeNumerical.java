package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
		for(int i=0;i<attribte_list_size;i++){
			attribte_list.add(i);
		}
		return attribte_list;
	}
	
	/**
	 * @decription 初始化根节点
	 * @return rootNode
	 * @throws IOException 
	 */
	public static Node getRootNode(ArrayList<ArrayList<Double>>matrix){
		Node rootNode=new Node(matrix);
		return rootNode;
	}
	
	
	/**
	 * @description 计算在某个属性下最佳划分的gini与split_point
	 * @param n(第几个属性)
	 * @param lefeDocList
	 * @param rightDocList
	 * @return ArrayList<Double>{gini,split_point}
	 */
	public static ArrayList<Double> get_attribute_gini(int n,ArrayList<ArrayList<Double>>D,ArrayList<ArrayList<Double>>lefeDocList,ArrayList<ArrayList<Double>>rightDocList){
		double split_point=0.0;
		double gini=Double.POSITIVE_INFINITY;
		ArrayList<Double> divide_result = new ArrayList<Double>();
		ArrayList<Double> value_list=new ArrayList<Double>(); 
		for(ArrayList<Double> vector:D){
			if(!value_list.contains(vector.get(n)))
			{
				value_list.add(vector.get(n));//统计所有出现过的属性值（去掉重复的）
			}
		}
		Collections.sort(value_list);
		for(Double value:value_list){
			ArrayList<ArrayList<Double>>D1=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>D2=new ArrayList<ArrayList<Double>>();
			for(ArrayList<Double> vector:D){
				if(vector.get(n)<=value){
					D1.add(vector);
				}
				else{
					D2.add(vector);
				}
				
			}
			double r1=1.0*D1.size()/D.size();
			double r2=1.0*D2.size()/D.size();
			double temp_gini=r1*getGini(D1)+r2*getGini(D2);
			if(temp_gini<gini){
				lefeDocList.clear();
				rightDocList.clear();
				gini=temp_gini;
				split_point=value;
				lefeDocList.addAll(D1);
				rightDocList.addAll(D2);
			}
		}
		divide_result.add(gini);
		divide_result.add(split_point);
		return divide_result;
	}
	
	/**
	 * @description 计算最好的分裂属性
	 * @param N
	 * @param attribte_list
	 * @return 最好的分裂属性
	 */
	public static int Attribute_selection_method(Node N,Node leftChild,Node rightChild){
		int attribute = 0;
		double split_point = 0.0;
		double gini=Double.POSITIVE_INFINITY;
		ArrayList<ArrayList<Double>>D=N.getDocList();//点N所包含的元组
		int attribte_list_size=attribte_list.size();
		for(int i=0;i<attribte_list_size;i++){
			ArrayList<ArrayList<Double>>lefeDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>rightDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<Double> divide_result=get_attribute_gini(i,D,lefeDocList,rightDocList);//返回在某个属性划分下gini与split_point
			double temp_gini=divide_result.get(0);
			if(temp_gini<gini){
				split_point=divide_result.get(1);
				attribute=i;
				gini=temp_gini;
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
	public static HashMap<Double,Integer>statistics(ArrayList<ArrayList<Double>>D){
	    HashMap<Double,Integer>statisticsMap=new HashMap<Double,Integer>();
		try{
			for(int i=0;i<D.size();i++){
				Double classify=D.get(i).get(attribte_list_size);//提取出元组类别
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
	public static Double getGini(ArrayList<ArrayList<Double>>D){
		Double gini=0.0;
		Double temp=0.0;
		HashMap<Double,Integer>statisticsMap=statistics(D);
		Double D_Size=1.0*D.size();
		Set<Double>classifys=statisticsMap.keySet();
		for(Double classify:classifys){
			Double p=(statisticsMap.get(classify)/D_Size);
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
		HashMap<Double,Integer>map=statistics(N.getDocList());
		return Tools.sortMap(map);
	}
	
	/**
	 * @description 创建二叉树
	 * @param N
	 */
	public static void getDecisionTree(Node N){
		if(N!=null){
			if(getGini(N.getDocList())==0||N.getDocList().size()<=6){
				N.setLeftChild(null);
				N.setRightChild(null);
			    N.setClassify(nodeClassify(N));
			}
			else{
				Node left=new Node();
				Node right=new Node();
				Attribute_selection_method(N,left,right);
				if(left.getDocList().size()==0||right.getDocList().size()==0){
					N.setLeftChild(null);
					N.setRightChild(null);
					N.setClassify(nodeClassify(N));
				}
				else{
					N.setLeftChild(left);
					N.setRightChild(right);
					getDecisionTree(left);
					getDecisionTree(right);
					
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
			if(vector.get(N.getAttribute())<=N.get_split_point()){
				return getResult(vector,N.getLeftChild());
			}
			
			else{
				return getResult(vector,N.getRightChild());
			}
		}
	}
	
	public static void process(){
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			int count=0;
			ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
			int num=Tools.divide2(i, allMatrix, testSample, trainSample);
			Node N=getRootNode(trainSample);
			getDecisionTree(N);
			//System.out.println("树的高度为:"+Tools.getTreeHeight(N));
			for(int j=0;j<testSample.size();j++){
				Double c1=testSample.get(j).get(attribte_list_size);
				Double c2=getResult(testSample.get(j),N);
				if(c1.equals(c2)){
					count++;
				}
			}
			System.out.println("第"+(i+1)+"折命中率为:"+1.0*count/num);
			resultList.add(1.0*count/num);
		}
		System.out.println("十折均值为:"+Tools.getMean(resultList));
	}
	
	
	public static void main(String[] args){
		getAllMatrix();
		getAttribte_list();
		process();
	}

}
