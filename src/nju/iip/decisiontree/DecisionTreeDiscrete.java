package nju.iip.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * @description 离散属性值决策树
 * @author wangqiang
 *
 */
public class DecisionTreeDiscrete {
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	/**
	 * 测试数据路径
	 */
	private static String filePath="Benchmark Dataset/lily.data";
	
	private static HashMap<Integer,ArrayList<Double>>attribute_list_map=new HashMap<Integer,ArrayList<Double>>();
	
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
	 * @description 统计每个离散属性所有出现过的值以及对应元组集合
	 * @param n
	 * @return ArrayList<Double>属性值的集合
	 */
	public static ArrayList<Double>statistics_Attribute_Value(int n){
		ArrayList<Double>attribute_value=new ArrayList<Double>();
		for(int i=0;i<allMatrix.size();i++){
			double value=allMatrix.get(i).get(n);
			if(!attribute_value.contains(value)){
				attribute_value.add(value);
			}
			
		}
		return attribute_value;
	}
	
	/**
	 * @description 创建属性列表
	 * @return
	 */
	public static HashMap<Integer,ArrayList<Double>>getAttribte_Map(){
		for(int i=0;i<allMatrix.get(0).size()-1;i++){
			attribute_list_map.put(i, statistics_Attribute_Value(i));
		}
		return attribute_list_map;
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
	public static int Attribute_selection_method(Node N,HashMap<Double,Node>child_nodes){
		int attribute=0;
	    Double info=Double.POSITIVE_INFINITY;
		ArrayList<ArrayList<Double>>doc_List=N.getDocList();
		int doc_size=doc_List.size();
		Set<Integer>keys=attribute_list_map.keySet();
		for(Integer key:keys){
			ArrayList<Double>value_list=attribute_list_map.get(key);//属性值列表
			HashMap<Double,ArrayList<ArrayList<Double>>>divide_Map=new HashMap<Double,ArrayList<ArrayList<Double>>>();
			for(int k=0;k<value_list.size();k++){
				ArrayList<ArrayList<Double>>list=new ArrayList<ArrayList<Double>>();
				divide_Map.put(value_list.get(k),list);//初始化分类map
			}
			for(int j=0;j<doc_size;j++){
				divide_Map.get(doc_List.get(j).get(key)).add(doc_List.get(j));//将元组根据属性值加入对应的map的value中
			}
			double temp=0.0;
			Set<Double>values=divide_Map.keySet();
			for(Double value:values){
				ArrayList<ArrayList<Double>>list=divide_Map.get(value);
				temp=temp+(list.size()/doc_size)*getInfo(list);
			}
			
			if(temp<info){
				info=temp;
				attribute=key;
				for(Double value:values){
					ArrayList<ArrayList<Double>>list=divide_Map.get(value);
					Node node=new Node(list);
					child_nodes.put(value, node);
				}
			}
		}
	
		return attribute;
	}
	
	/**
	 * @description 节点N中所有元组的类别统计
	 * @param N
	 * @return HashMap<类别，元组数>
	 */
	public static HashMap<Double,Integer>statistics(ArrayList<ArrayList<Double>> doc_List){
	    HashMap<Double,Integer>statisticsMap=new HashMap<Double,Integer>();
		try{
			int size=doc_List.size();
			for(int i=0;i<size;i++){
				Double classify=doc_List.get(i).get(attribte_list_size);//提取出元组类别
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
	 * @description 计算某个点的信息增益（不纯度）
	 * @param N
	 * @return info
	 */
	public static Double getInfo(ArrayList<ArrayList<Double>> doc_List){
		Double info=0.0;
		Double temp=0.0;
		HashMap<Double,Integer>statisticsMap=statistics(doc_List);
		Double D=1.0*doc_List.size();
		Set<Double>classifys=statisticsMap.keySet();
		for(Double classify:classifys){
			Double p=(statisticsMap.get(classify)/D);
			temp=temp+p*Math.log(p);
		}
		info=-temp;
		return info;
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
		if(getInfo(N.getDocList())<0.5||attribute_list_map.size()==0){
			N.get_child_nodes().clear();
		    N.setClassify(nodeClassify(N));
		}
		else{
			HashMap<Double,Node>child_nodes=new HashMap<Double,Node>();
			int attribute=Attribute_selection_method(N,child_nodes);
			if(is_Contain_Null_Point(child_nodes)){
				N.get_child_nodes().clear();
				N.setClassify(nodeClassify(N));
			}
			else{
				attribute_list_map.remove(attribute);//删除划分属性
				N.setAttribute(attribute);
				N.set_child_nodes(child_nodes);
				Set<Double>keys=child_nodes.keySet();
				for(Double key:keys){
					getDecisionTree(child_nodes.get(key));
				}
			}
		}
	}
	
	public static boolean is_Contain_Null_Point(HashMap<Double,Node>child_nodes){
		Set<Double>keys=child_nodes.keySet();
		for(Double key:keys){
			if(child_nodes.get(key).getDocList().size()==0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @description 计算某篇帖子所属类别
	 * @param vector
	 * @return
	 */
	public static Double getResult(ArrayList<Double>vector,Node N){
		
		if(N.get_child_nodes().size()==0){
			return N.getClassify();
		}
		
		else{
			int attribute=N.getAttribute();
			Double key=vector.get(attribute);
			Node child_node=N.get_child_nodes().get(key);
			return getResult(vector,child_node);
		}
	}
	
	public static void process() throws IOException{
		ArrayList<Double>resultList=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			attribute_list_map.clear();
			attribute_list_map=getAttribte_Map();
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
		process();
	
	}


}
