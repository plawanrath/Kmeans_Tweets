import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Jacard {
	
	HashMap<Integer,ArrayList<Double>> JaccardSet = new HashMap<Integer,ArrayList<Double>>();
	HashMap<Integer,Long> JaccardKeySet = new HashMap<Integer,Long>();
	HashMap<Integer,ArrayList<Long>> cluster = new HashMap<Integer,ArrayList<Long>>();
	public Jacard()
	{
		
	}
	
	private int getIntersection(String s1, String s2)
	{
		int count = 0;
		String[] words1 = s1.split(" ");
		String[] words2 = s2.split(" ");
		for(int i=0;i<words1.length;i++)
		{
			String w = words1[i];
			for(int j=0;j<words2.length;j++)
			{
				if(w.equals(words2[j]))
				{
					count++;
					break;
				}
			}
		}
		return count;
	}
	
	public double jaccardDistance(String s1,String s2)
	{
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		double intersection = this.getIntersection(s1, s2) * 1.0;
		String[] words1 = s1.split(" ");
		String[] words2 = s2.split(" ");
		double union = (words1.length * 1.0) + (words2.length * 1.0) - intersection;
		double distance = 1.0 - (intersection/union);
		distance = Math.round(distance * 10000) / 10000.0;
		return distance;
	}
	
	public int getMinIndex(ArrayList<Double> listOfValues)
	{
		int minIndex = 0;
		double minValue = listOfValues.get(0);
		for(int i=1;i<listOfValues.size();i++)
		{
			if(listOfValues.get(i) <= listOfValues.get(minIndex))
				minIndex = i;
		}
		return minIndex;
	}
	
	private double getMinValue(ArrayList<Double> listOfValues)
	{
		double minValue = listOfValues.get(0);
		for(int i=1;i<listOfValues.size();i++)
		{
			if(listOfValues.get(i) <= minValue)
				minValue = listOfValues.get(i);
		}
		return minValue;		
	}
	public void setJaccards(ArrayList<Long> centroidIds, HashMap<Long,String> tData,int K)
	{
			this.JaccardSet.clear();
			this.JaccardKeySet.clear();
			Iterator mp = tData.entrySet().iterator();
			int j=0;
			while(mp.hasNext())
			{
				Entry pair = (Entry) mp.next();
				long id = (Long) pair.getKey();
				String tweet = (String) pair.getValue();
				for(int i=0;i<K;i++)
				{
					String centroidTweet = tData.get(centroidIds.get(i)); 
					ArrayList<Double> temp;
					double res = this.jaccardDistance(centroidTweet, tweet);
					if(this.JaccardSet.get(j) == null) 
					{
						temp = new ArrayList<Double>();
						temp.add(res);
					}
					else
					{
						temp = this.JaccardSet.get(j);
						temp.add(res);
					}
					this.JaccardSet.put(j, temp);
				}
				this.JaccardKeySet.put(j, id);
				j++;
			}
	}

	public void putIntoClusters()
	{
		Iterator mp = this.JaccardSet.entrySet().iterator();
		while(mp.hasNext())
		{
			Entry pair = (Entry) mp.next();
			int key = (int) pair.getKey();
			long id = this.JaccardKeySet.get(key);
			ArrayList<Double> values = (ArrayList<Double>) pair.getValue();
			int minIndex = this.getMinIndex(values);
			ArrayList<Long> temp;
			if(this.cluster.get(minIndex) == null)
			{
				temp = new ArrayList<Long>();
				temp.add(id);
			}
			else
			{
				temp = this.cluster.get(minIndex);
				temp.add(id);
			}
			this.cluster.put(minIndex, temp);
		}
	}
	
	public ArrayList<Long> recomputeCentroids(HashMap<Long,String> tweetData)
	{
		//For that cluster, find the tweet that has the minimum distance to all other tweets in the cluster
		HashMap<Integer,ArrayList<Long>> prevCluster = this.cluster;
		ArrayList<Long> newCentroids = new ArrayList<Long>();
		Iterator mp = prevCluster.entrySet().iterator();
		while(mp.hasNext())
		{
			Entry pair = (Entry) mp.next();
			int key = (int) pair.getKey();
			ArrayList<Long> ids = (ArrayList<Long>) pair.getValue();
			ArrayList<Double> jaccardValues = new ArrayList<Double>();
			HashMap<Double,Integer> indexMapping = new HashMap<Double,Integer>();
			for(int i=0;i<ids.size();i++)
			{
				for(int j=i+1;j<ids.size();j++)
				{
					long s1 = ids.get(i);
					long s2 = ids.get(j);
					String str1 = tweetData.get(s1);
					String str2 = tweetData.get(s2);
					double res = this.jaccardDistance(str1, str2);
					indexMapping.put(res, i);
					jaccardValues.add(res);
				}
			}
			double minValue = this.getMinValue(jaccardValues);
			int index = indexMapping.get(minValue);
			long val = ids.get(index);
			newCentroids.add(val);
		}
		return newCentroids;
	}
	
	public boolean compareArrayLists(ArrayList<Long> l1, ArrayList<Long> l2)
	{
		for(int i=0;i<l1.size();i++)
		{
			if(!l1.contains(l2.get(i)))
				return false;
		}
		return true;
	}

	public boolean compareHashMaps(HashMap<Integer,ArrayList<Long>> h1, HashMap<Integer,ArrayList<Long>> h2)
	{
		Iterator mp = h1.entrySet().iterator();
		while(mp.hasNext())
		{
			Entry pair = (Entry) mp.next();
			ArrayList<Long> s1 = (ArrayList<Long>) pair.getValue();
			ArrayList<Long> s2 = (ArrayList<Long>) pair.getValue();
			if(!compareArrayLists(s1,s2))
				return false;
		}
		return true;
	}

}
