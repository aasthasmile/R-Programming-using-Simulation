package pkg;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

public class Demo {

	static void descriptivestats() {
		double[] inputArray = new double[] { 2, 3, 4, 5, 6, 8, 9, 10,9 };
		DescriptiveStatistics stats = new DescriptiveStatistics();

		// Add the data from the array
		for (int i = 0; i < inputArray.length; i++) {
			stats.addValue(inputArray[i]);
		}

		// Compute some statistics
		double mean = stats.getMean();
		double std = stats.getStandardDeviation();
		double median = stats.getPercentile(50);
		System.out.println("The mean is:" + mean + "\nThe standard Deviation is:" + std + "\nThe median is:" + median);

		NaturalRanking ranking = new NaturalRanking(NaNStrategy.MINIMAL, TiesStrategy.MAXIMUM);
		double[] data = { 20, 17, 30, 42.3, 17, 50, Double.NaN, Double.NEGATIVE_INFINITY, 17 };
		double[] ranks = ranking.rank(data);
		for (double d : ranks) {
			System.out.print(d+" ");
		}
		
		double cov=new Covariance().covariance(inputArray, data);
		System.out.println("\nThe covariance of x and y is:"+cov);

	}

	public static void main(String[] args) {
		Demo.descriptivestats();
	}

}
