import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class solution {
	
	public ArrayList<Double> SSE(ArrayList<Long> centroids, HashMap<Integer,ArrayList<Long>> cluster, HashMap<Long, String> tweetData)
	{
		ArrayList<Double> res = new ArrayList<Double>();
		for(int i=0;i<centroids.size();i++)
		{
			double result = 0.0;
			String centString = tweetData.get(centroids.get(i));
			ArrayList<Long> ids = cluster.get(i);
			for(int j=0;j<ids.size();j++)
			{
				String valString = tweetData.get(ids.get(j));
				Jacard jd = new Jacard();
				double rval = jd.jaccardDistance(centString, valString);
				rval = Math.pow(rval, 2);
				result = result + rval;
			}
			res.add(result);
		}
		return res;
	}
	
	public static void main(String[] args) {
		int K = 25;
		readData rd = new readData("Tweets.json");
		ArrayList<Long> initialSeeds = rd.parseInitialCentroids("InitialSeeds.txt");
		rd.createJsonObjects();
		Jacard jd = new Jacard();
		jd.setJaccards(initialSeeds, rd.tweetDataList, K);
		jd.putIntoClusters();
		HashMap<Integer,ArrayList<Long>> prevCluster = jd.cluster;
		HashMap<Integer,ArrayList<Long>> newCluster;
		ArrayList<Long> newCentroids;
		int iterations = 0;
		do
		{
			newCentroids = jd.recomputeCentroids(rd.tweetDataList);
			jd.setJaccards(newCentroids, rd.tweetDataList, K);
			jd.putIntoClusters();
			newCluster = jd.cluster;
			iterations++;
		}
		while(!jd.compareHashMaps(prevCluster,newCluster) && iterations <= 25);
		Iterator mp = jd.cluster.entrySet().iterator();
		while(mp.hasNext())
		{
			Entry pair = (Entry) mp.next();
			System.out.print(pair.getKey() + " - " );
			ArrayList<Long> values = (ArrayList<Long>) pair.getValue();
			for(int i=0;i<values.size();i++)
			{
				System.out.print(values.get(i) + " ");
			}
			System.out.println();
		}
		solution s1 = new solution();
		ArrayList<Double> sseValues = s1.SSE(newCentroids, jd.cluster, rd.tweetDataList);
		System.out.println("SSE");
		for(int i=0;i<sseValues.size();i++)
		{
			System.out.println(i + " = " + sseValues.get(i));
		}
	}
}
