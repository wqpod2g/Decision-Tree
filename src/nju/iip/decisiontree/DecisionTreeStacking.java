package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class DecisionTreeStacking {
	/**
	 * 测试数据路径
	 */
	private static String filePath="Benchmark Dataset/lily.data";
	
	
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
		int attribte_list_size=D.get(0).size()-1;
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
	    if(D.size()==0){
	    	return statisticsMap;
	    }
	    int attribte_list_size=D.get(0).size()-1;
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
	public static void getDecisionTree(Node N,int n){
		if(N!=null){
			if(getGini(N.getDocList())==0||N.getDocList().size()<=n){
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
					getDecisionTree(left,n);
					getDecisionTree(right,n);
					
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
	
    
	
	
    public static Node getMetaTree(ArrayList<ArrayList<Double>>trainSample){
		Node meta_node=new Node();
		ArrayList<ArrayList<Double>>D=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<10;i++){
			ArrayList<ArrayList<Double>>test=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>train=new ArrayList<ArrayList<Double>>();
			ArrayList<Node>node_list=new ArrayList<Node>();//5棵树根节点list
			Tools.divide2(i, trainSample, test, train);
			for(int j=0;j<5;j++){
				Node node=new Node(train);
				getDecisionTree(node,(j+1)*5);
				node_list.add(node);//循环生成5棵不同的树
			}
			ArrayList<ArrayList<Double>>Di=new ArrayList<ArrayList<Double>>();
			for(Node node:node_list){//对每棵树产生test对应预测的lable_lisy
				ArrayList<Double> lable_list=new  ArrayList<Double>();//5列lable预测值
				for(ArrayList<Double> vector:test){
					lable_list.add(getResult(vector,node));
				}
				Di.add(lable_list);
			}
			Di=Tools.getChangeMatrix(Di);
			for(int j=0;j<Di.size();j++){
				Di.get(j).add(test.get(j).get(attribte_list_size));//将实际lable值加入
			}
			D.addAll(Di);
		}
		meta_node.setDocList(D);
		getDecisionTree(meta_node,6);
		return meta_node;
	}
	
	public static ArrayList<ArrayList<Double>>get_test_meta(ArrayList<ArrayList<Double>>testSample,ArrayList<ArrayList<Double>>trainSample){
		ArrayList<ArrayList<Double>>test_meta=new ArrayList<ArrayList<Double>>();
		ArrayList<Node>node_list=new ArrayList<Node>();//5棵树根节点list
		for(int i=0;i<5;i++){
			Node node=new Node(trainSample);
			getDecisionTree(node,(i+1)*5);
			node_list.add(node);//循环生成5棵不同的树
		}
		
		ArrayList<ArrayList<Double>>Di=new ArrayList<ArrayList<Double>>();
		for(Node node:node_list){//对每棵树产生test对应预测的lable_lisy
			ArrayList<Double> lable_list=new  ArrayList<Double>();//5列lable预测值
			for(ArrayList<Double> vector:testSample){
				lable_list.add(getResult(vector,node));
			}
			Di.add(lable_list);
		}
		test_meta=Tools.getChangeMatrix(Di);
		for(int j=0;j<test_meta.size();j++){
			test_meta.get(j).add(testSample.get(j).get(attribte_list_size));//将实际lable值加入
		}
		return test_meta;
	}
	
	
	public static void process(){
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			int count=0;
			ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
			Tools.divide(i, allMatrix, testSample, trainSample);
			Node meta_node=getMetaTree(trainSample);
			ArrayList<ArrayList<Double>>test_meta=get_test_meta(testSample,trainSample);
			for(int j=0;j<test_meta.size();j++){
				Double c1=test_meta.get(j).get(5);
				Double c2=getResult(test_meta.get(j),meta_node);
				if(c1.equals(c2)){
					count++;
				}
			}
			System.out.println("第"+(i+1)+"折命中率为:"+1.0*count/100);
			resultList.add(1.0*count/100);
		}
		System.out.println("十折均值为:"+Tools.getMean(resultList));
	}
	
	
	public static void main(String[] args){
		System.out.println("测试数据集为:"+filePath);
		getAllMatrix();
		long startTime=System.currentTimeMillis();   //获取开始时间
		process();
		long endTime=System.currentTimeMillis(); //获取结束时间   
		System.out.println("运行时间： "+(endTime-startTime)/60000.0+"mins");
	}


}
