package pkg;

import java.util.ArrayList;
import cc.mallet.util.*;
import java.util.PriorityQueue;

import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;


/*All the events in the priority Queue is of type MessageArrivalEvents having three fields implmented using ArrayList
 * <event code,time of Arrival ,Channelno/MessageId>*/
class MessageArrivalEvents extends ArrayList implements Comparable {

	/*Comparator Method checks the MessageArrival time ,good weather arrival time and bad weather arrival time and depending 
	 * on the time ,it sorts the value in the priority queue.ArrayList second position stores the arrival times.
	 * */
	@Override
	public int compareTo(Object o1) {
		// TODO Auto-generated method stub
		MessageArrivalEvents mae = (MessageArrivalEvents) this;
		int time1 = (int) mae.get(1);
		MessageArrivalEvents mae2 = (MessageArrivalEvents) o1;
		int time2 = (int) mae2.get(1);
		if (time1 > time2)
			return 1;
		else if (time2 > time1)
			return -1;
		else
			return 0;
	}
}

public class DiscreteEventSimulation {

	//check whether the Weather is Good Weather so boolean isGood set to True
	public boolean isGood = false;
	
	//check whether the Weather is Bad Weather so boolean isbad set to True
	public boolean isbad = false;
	
	/*The state for three dedicated channel is handled using stateforChannel variables to determine
	 * which of the channel is free and which is busy(0-Available,1-Busy)*/
	public int stateForChannel1 = 0;
	public int stateForChannel2 = 0;
	public int stateForChannel3 = 0;
	
	//Poisson process sampling with lambda is 2 per hour
	public int lambda = 2; 
	public int samplePoissonValue = 0;
	
	//sampling Value using Normal distribution to determine whether weather is good or bad
	public int durationGoodWeather;
	public int durationBadWeather;
	public int sampleX = 0; //Good Weather sampling value
	
	//Constants provided in Assignment 3
	public static final double meanGoodWeather = 90; //90 minutes
	public static final double meanBadWeather = 60; //1 hour =60 minutes
	public static final double standardDeviationGoodWeather = 10; //10 minutes
	public static final double standardDeviationBadWeather = 20;   //20 minutes
	public static final int changeHourstoMinutes=60;
	
	//In one simulation of [0,100] hours time interval ,number of messages lost.
	public int messagesLostCounter = 0;
	
	//In one simulation of [0,100] hours time interval ,track(counting) number of messages sent using poisson process with uniform distribution
	public int noofsentMessages;
	
	//At end of 100th hour, total number of messages sent by Wolf-communication System.
	public static int totalsentMessages;
	
	//At end of 200th hour, total number of messages (including k+1 additional copies) sent by Wolf-communication System.
	public static int totalResendMessages;
	
	//At end of 10000th Simulation ,probability of messages being lost for Excercise 1.
	public double probabilityOfLostMessages;
	
	//At end of 10000th Simulation ,probability of messages being lost for Excercise 4(with a k additional copies).
	public double probabilityOfLostMessagesPrime;
	
	//Number of additional copies
	public int k=1;
	
	
	
	/*Excercise 1 Implementing the ESTA algorithm that performs a discrete-event simulation of the Wolf-Communications message processing system over the time interval of [0,100],
	 *  where time is measured in minutes.Assume four following kinds of events: 
	 *  1) Message Arrived(MA) 2.Message Processed(MP) 3)Good Weather(GW) 4)Bad Weather(BW)*/
	public static int eventSimulation() {
		
		DiscreteEventSimulation eventSim = new DiscreteEventSimulation();
		
		/*Radio-transmitted messages are processed as per the Channel availability ,hence we need a priorityQueue to
		 * implement MessageArrival Events according to their arrival time(priority).
		 */
		PriorityQueue priorityQueue = new PriorityQueue();

		MessageArrivalEvents maEvent = new MessageArrivalEvents();
		
		/* Radio-transmitted messages arrive according to Poisson process ,therefore define X=(-log (1-U))/lambda 
		 * or equivalently X=(-log U)/lambda where U has has the continuous uniform distribution over the interval (0,1)
		 * and lambda is the message inter-arrival time */
		 
		UniformRealDistribution poissonSampled = new UniformRealDistribution();
		int sampleValue = (int) (((-1) * ((double) Math.log(poissonSampled.sample()) / (double) eventSim.lambda)) * 60);
		
		/*Every Event added to PriorityQueue is made of three Values:<Message Code,Arrival time,messageId/Channel No>
		 * where Message Code = Message Arrived(MA) <MA,arrival-time,messageId>
		 * 						Message Processed(MP) <MP,arrival-time,channelNo at which message processed>
		 * 						GoodWeather(GW) or BadWeather(BW) <GW/BW,arrival-time,null>
		 */
		
		maEvent.add(0, "MA");
		maEvent.add(1, sampleValue);
		maEvent.add(2, 0);
		
		eventSim.samplePoissonValue = sampleValue;
		
		/*Message arriving in the time interval [0,100] are added to the Future Event List or priorityQueue.
		 * Last Message arrives at 100th hour.
		 */
		while (eventSim.samplePoissonValue <= 100*changeHourstoMinutes) {
			
			priorityQueue.add(maEvent);
			//System.out.println("<" + maEvent.get(0) + "," + maEvent.get(1) + "," + maEvent.get(2) + ">");

			//Incrementing the messageId by one after adding one Message Arrival.
			eventSim.noofsentMessages += 1;
			
			//Poisson process X=(-log U)/lambda used to Sample out a Value.
			sampleValue = (int) (((-1) * ((double) Math.log(1-poissonSampled.sample()) / (double) eventSim.lambda))* 60);
			
			eventSim.samplePoissonValue = eventSim.samplePoissonValue + sampleValue;

			maEvent = new MessageArrivalEvents();
			maEvent.add(0, "MA");
			maEvent.add(1, eventSim.samplePoissonValue);
			maEvent.add(2, eventSim.noofsentMessages);
		}
	
		//During 0-100 hours i.e. 6000 minutes, total number of messages sent by Wolf-communication System
		totalsentMessages = eventSim.noofsentMessages;
		
		/*The Good Weather is normally distributed with mean and standardDeviation as 90 and 10 minutes respectively.*/
	
		NormalDistribution sampleValueGoodWeather = new NormalDistribution(meanGoodWeather,standardDeviationGoodWeather);
		int sampleX = (int) sampleValueGoodWeather.sample();
		eventSim.durationGoodWeather = sampleX;
		
		 /* Good Weather event always occurs at T=0.*/
		MessageArrivalEvents goodWeatherFirst = new MessageArrivalEvents();
		goodWeatherFirst.add(0, "GW");
		goodWeatherFirst.add(1, 0);
		goodWeatherFirst.add(2, "null");
		priorityQueue.add(goodWeatherFirst);
		
		/* A Good Weather Event Triggers a Bad Weather Future Event added to FEL or priorityQueue*/
		eventSim.isbad = true;
		MessageArrivalEvents badEvent = new MessageArrivalEvents();
		badEvent.add(0, "BW");
		badEvent.add(1, sampleX);
		badEvent.add(2, "null");
		priorityQueue.add(badEvent);

		
		/*While the FEL i.e. priority Queue is not empty,process the events in
		 sequential order*/
		while (!priorityQueue.isEmpty()) {
			
			MessageArrivalEvents e1 = (MessageArrivalEvents) priorityQueue.poll();
			// System.out.println("<" + e1.get(0) + "," + e1.get(1) + "," + e1.get(2) + ">");

			// Checking The FEL Future event List for the MA(Message Arrived)
			if (e1.get(0) == "MA") {
				
				/*Initially checking which channel is free.If no channel is free then the message is lost and
				 * the messageLostCounter is incremented by one.*/
				
				if (eventSim.stateForChannel1 == 1 && eventSim.stateForChannel2 == 1 && eventSim.stateForChannel3 == 1) {
					eventSim.messagesLostCounter += 1;
					//System.out.println("Lost message count: "+eventSim.messagesLostCounter );
				} 
				else {
					
					/*Deﬁne CDF F(x)=P(F−1(U)≤ x) then using Inverse Transform Method X = F−1(U), where U has the
					 * continuous uniform distribution over the interval (0,1).
					 * Good Weather has F(x) =x then X = F−1(U)
					 * Bad Weather has F(x) =x^3 then X = F−1(cube-root(U)) 
					 */
					UniformRealDistribution sampleUniformValue = new UniformRealDistribution(0.0, 1.0);
					
					int sampleU = (int) (sampleUniformValue.sample() * changeHourstoMinutes);

					if (eventSim.isbad == true) 
						sampleU = (int) (Math.cbrt(sampleUniformValue.sample()) * changeHourstoMinutes);
					
					
					/*Every Processed Message added to PriorityQueue is ArrayList of three Values:<Message Code,Arrival time,Channel No>
					 * where Message Code = Message Processed(MP) <"MP",arrival-time,channelNo at which message processed>
					 */ 						
					
					MessageArrivalEvents messageProcessed = new MessageArrivalEvents();

					messageProcessed.add(0, "MP"); 

					messageProcessed.add(1, ((int) e1.get(1) + sampleU));
					
					/*Three dedicated channels-channel1 ,channel2,channel3 are available to handle the incoming messages .
					 * When Message arrives,it need to be processed through one of the three possible available channels
					 *depending on which channel is currently available.(0-Available,1-Busy) */

					if (eventSim.stateForChannel1 == 0) {
						messageProcessed.add(2, 1);
						eventSim.stateForChannel1 = 1;
					} else if (eventSim.stateForChannel2 == 0) {
						messageProcessed.add(2, 2);
						eventSim.stateForChannel2 = 1;
					} else if (eventSim.stateForChannel3 == 0) {
						messageProcessed.add(2, 3);
						eventSim.stateForChannel3 = 1;
					}
					
					priorityQueue.add(messageProcessed);
				}
				
			} 
			
			/* Checking The FEL Future event List for the MP(Message processed) so that the channel that was Busy(1)
			 * processing message can now be set to available(0).*/
			else if (e1.get(0) == "MP") {
				
				/*Extracting the Channel No at which Message has been processed
				 * and setting the channel as free.(0-AVAILABLE ,1-BUSY)*/
				int i = (int) e1.get(2);
				
				switch (i) {
				case 1: { eventSim.stateForChannel1 = 0; 	break;	}
				case 2: { eventSim.stateForChannel2 = 0;	break; 	}
				case 3: { eventSim.stateForChannel3 = 0; 	break;	}
				default: break;
				}

			} 
			
			else if (e1.get(0) == "GW" && (int) e1.get(1) < 100*changeHourstoMinutes) {
				
				/*The Good Weather is normally distributed with mean and standardDeviation as 90 and 10 minutes respectively.*/
				NormalDistribution sampleValueGood= new NormalDistribution(meanGoodWeather,standardDeviationGoodWeather);
				int sampleX1 = (int) sampleValueGood.sample();
				eventSim.durationGoodWeather = sampleX1;
				
				/* A Good Weather Event Triggers a Bad Weather Future Event added to FEL or priorityQueue*/
				eventSim.isbad = true;
				
				MessageArrivalEvents badEvent1 = new MessageArrivalEvents();
				badEvent1.add(0, "BW");
				badEvent1.add(1, ((int) e1.get(1) + sampleX1));
				badEvent1.add(2, "null");
				
				priorityQueue.add(badEvent1);

			} 
			
			else if (e1.get(0) == "BW" && (int) e1.get(1) < 100*changeHourstoMinutes) {
				
				/*The Bad Weather is normally distributed with mean and standardDeviation as 60 and 20 minutes respectively.*/
				NormalDistribution sampleValueBadWeather = new NormalDistribution(meanBadWeather,standardDeviationBadWeather);
				int sampleY = (int) sampleValueBadWeather.sample();
				eventSim.durationBadWeather = sampleY;
				
				/* A Bad Weather Event Triggers a Good Weather Future Event added to FEL or priorityQueue*/
				eventSim.isGood = true;
				
				MessageArrivalEvents goodEvent = new MessageArrivalEvents();
				goodEvent.add(0, "GW");
				goodEvent.add(1, ((int) e1.get(1) + sampleY));
				goodEvent.add(2, "null");
				
				priorityQueue.add(goodEvent);
				
			}
		}
		/*Total number of messages lost in one Simulation of Radio transmitted message for a time interval of [0,100]*/
		return eventSim.messagesLostCounter;
		
	}
 
	public static int eventSimulation(int k) {
      
		DiscreteEventSimulation eventSim = new DiscreteEventSimulation();
	

        PriorityQueue priorityQueue = new PriorityQueue();

		MessageArrivalEvents maEvent = new MessageArrivalEvents();
		
		UniformRealDistribution poissonSampled = new UniformRealDistribution();
		int sampleValue = (int) (((-1) * ((double) Math.log(poissonSampled.sample()) / (double) eventSim.lambda)) * 60);
		
		maEvent.add(0, "MA");
		maEvent.add(1, sampleValue);
		maEvent.add(2, 0);
		
		eventSim.samplePoissonValue = sampleValue;
		
		int orginialTotalMessages=0;
		
		while (eventSim.samplePoissonValue <= 200*changeHourstoMinutes) {
			
			
			//System.out.println("<" + maEvent.get(0) + "," + maEvent.get(1) + "," + maEvent.get(2) + ">");

			eventSim.noofsentMessages += 1;
			
			sampleValue = (int) (((-1) * ((double) Math.log(1-poissonSampled.sample()) / (double) eventSim.lambda))* 60);
			
			eventSim.samplePoissonValue = eventSim.samplePoissonValue + sampleValue;

			
			
			if(eventSim.samplePoissonValue >= 100*changeHourstoMinutes && eventSim.samplePoissonValue <= 200*changeHourstoMinutes){
				for(int i=0;i<=k;i++){
					maEvent = new MessageArrivalEvents();
					maEvent.add(0, "MA");
					maEvent.add(1, (int)(eventSim.samplePoissonValue + i*60));
					maEvent.add(2, eventSim.noofsentMessages);
					priorityQueue.add(maEvent);
			       
				}
			}
			else{
			maEvent = new MessageArrivalEvents();
			maEvent.add(0, "MA");
			maEvent.add(1, eventSim.samplePoissonValue);
			maEvent.add(2, eventSim.noofsentMessages);
			orginialTotalMessages=eventSim.noofsentMessages;
			priorityQueue.add(maEvent);
			}
			
		}
		
									
		int additionalMessages=(k+1)*(eventSim.noofsentMessages-orginialTotalMessages);
		
		totalResendMessages= eventSim.noofsentMessages+additionalMessages;
		
		/*The Good Weather is normally distributed with mean and standardDeviation as 90 and 10 minutes respectively.
		 * Good Weather event always occurs at T=0.
		 */
		NormalDistribution sampleValueGoodWeather = new NormalDistribution(meanGoodWeather,standardDeviationGoodWeather);
		int sampleX = (int) sampleValueGoodWeather.sample();
		eventSim.durationGoodWeather = sampleX;
		
		MessageArrivalEvents goodWeatherFirst = new MessageArrivalEvents();
		goodWeatherFirst.add(0, "GW");
		goodWeatherFirst.add(1, 0);
		goodWeatherFirst.add(2, "null");
		priorityQueue.add(goodWeatherFirst);
		
		/* A Good Weather Event Triggers a Bad Weather Future Event added to FEL or priorityQueue*/
		eventSim.isbad = true;
		MessageArrivalEvents badEvent = new MessageArrivalEvents();
		badEvent.add(0, "BW");
		badEvent.add(1, sampleX);
		badEvent.add(2, "null");
		priorityQueue.add(badEvent);

		
		boolean[] checkMessageProcessed=new boolean[eventSim.noofsentMessages+1];
		int[] messagecounter=new int[eventSim.noofsentMessages+1];
		
		/*While the FEL i.e. priority Queue is not empty,process the events in
		 sequential order*/
		while (!priorityQueue.isEmpty()) {
			
			MessageArrivalEvents e1 = (MessageArrivalEvents) priorityQueue.poll();
		
			//System.out.println("<" + e1.get(0) + "," + e1.get(1) + "," + e1.get(2) + ">");
			
			if (e1.get(0) == "MA") {
				
				if (eventSim.stateForChannel1 == 1 && eventSim.stateForChannel2 == 1 && eventSim.stateForChannel3 == 1) {
					
					if((int)e1.get(1)<=100*changeHourstoMinutes){					
					             eventSim.messagesLostCounter += 1;
					             //System.out.println("Message lost counter:"+eventSim.messagesLostCounter);
					            
					}
					
					else if((int)e1.get(1)>=100*changeHourstoMinutes &&(int)e1.get(1)<=200*changeHourstoMinutes ){
					
						if(!checkMessageProcessed[(int)e1.get(2)]){
							
							messagecounter[(int)e1.get(2)]+=1;
						
							if(messagecounter[(int)e1.get(2)]== k+1 )
							 
								eventSim.messagesLostCounter += 1;
						      //System.out.println("Message lost counter:"+eventSim.messagesLostCounter);
						}
						else{
							eventSim.messagesLostCounter += 0;
						}
					}
					//System.out.println("Lost message count: "+eventSim.messagesLostCounter );
				} 
				else {
					UniformRealDistribution sampleUniformValue = new UniformRealDistribution(0.0, 1.0);
					
					int sampleU = (int) (sampleUniformValue.sample() * changeHourstoMinutes);

					if (eventSim.isbad == true) 
						sampleU = (int) (Math.cbrt(sampleUniformValue.sample()) * changeHourstoMinutes);
					
					MessageArrivalEvents messageProcessed = new MessageArrivalEvents();

					messageProcessed.add(0, "MP"); 

					messageProcessed.add(1, ((int) e1.get(1) + sampleU));
					
					if (eventSim.stateForChannel1 == 0) {
						messageProcessed.add(2, 1);
						eventSim.stateForChannel1 = 1;
					} else if (eventSim.stateForChannel2 == 0) {
						messageProcessed.add(2, 2);
						eventSim.stateForChannel2 = 1;
					} else if (eventSim.stateForChannel3 == 0) {
						messageProcessed.add(2, 3);
						eventSim.stateForChannel3 = 1;
					}
					
					priorityQueue.add(messageProcessed);
					
					checkMessageProcessed[((int)e1.get(2))]= true;
				}
				
			} 
		        else if (e1.get(0) == "MP") {
				
		        	int i = (int) e1.get(2);
				
				switch (i) {
				case 1: { eventSim.stateForChannel1 = 0; 	break;	}
				case 2: { eventSim.stateForChannel2 = 0;	break; 	}
				case 3: { eventSim.stateForChannel3 = 0; 	break;	}
				default: break;
				}

			} 
			
			
			else if (e1.get(0) == "GW" && (int) e1.get(1) < 200*changeHourstoMinutes) {
				
				/*The Good Weather is normally distributed with mean and standardDeviation as 90 and 10 minutes respectively.*/
				NormalDistribution sampleValueGood= new NormalDistribution(meanGoodWeather,standardDeviationGoodWeather);
				int sampleX1 = (int) sampleValueGood.sample();
				eventSim.durationGoodWeather = sampleX1;
				
				/* A Good Weather Event Triggers a Bad Weather Future Event added to FEL or priorityQueue*/
				eventSim.isbad = true;
				
				MessageArrivalEvents badEvent1 = new MessageArrivalEvents();
				badEvent1.add(0, "BW");
				badEvent1.add(1, ((int) e1.get(1) + sampleX1));
				badEvent1.add(2, "null");
				
				priorityQueue.add(badEvent1);

			} 
			
			else if (e1.get(0) == "BW" && (int) e1.get(1) < 200*changeHourstoMinutes) {
				
				/*The Bad Weather is normally distributed with mean and standardDeviation as 60 and 20 minutes respectively.*/
				NormalDistribution sampleValueBadWeather = new NormalDistribution(meanBadWeather,standardDeviationBadWeather);
				int sampleY = (int) sampleValueBadWeather.sample();
				eventSim.durationBadWeather = sampleY;
				
				/* A Bad Weather Event Triggers a Good Weather Future Event added to FEL or priorityQueue*/
				eventSim.isGood = true;
				
				MessageArrivalEvents goodEvent = new MessageArrivalEvents();
				goodEvent.add(0, "GW");
				goodEvent.add(1, ((int) e1.get(1) + sampleY));
				goodEvent.add(2, "null");
				
				priorityQueue.add(goodEvent);
				
			}
		}
				
		return eventSim.messagesLostCounter;
	}
	

	static double imc(int n, double delta,boolean ResendMessages,int k ) {
		
		int messagesLostIndividual = 0;
		int messagesSentIndividual = 0;
		int totalMessagesSent = 0;
		int totalmessagesLost = 0;
		int sumsq = 0;

		for (int i = 0; i < n; i++) {
			
			
			if(ResendMessages==true && k>=1){
				messagesLostIndividual = DiscreteEventSimulation.eventSimulation(k);
				messagesSentIndividual = DiscreteEventSimulation.totalResendMessages;
			}
			else{
			    messagesLostIndividual = DiscreteEventSimulation.eventSimulation();
			    messagesSentIndividual = DiscreteEventSimulation.totalsentMessages;
			}
			
			totalmessagesLost = totalmessagesLost + messagesLostIndividual;
			totalMessagesSent = totalMessagesSent + messagesSentIndividual;
			
			sumsq = sumsq + messagesLostIndividual * messagesLostIndividual;
		}
		double lambda = totalmessagesLost / n;
		double sigmasq = (sumsq - (lambda * lambda * n)) / (n - 1);
		double stdDeviation = (double) Math.sqrt(sigmasq);
		double se = (double) Math.sqrt(sigmasq / n);
		double re = se / lambda;
		double qdelta = (double) StatFunctions.qnorm((1 + delta) / 2, false);
		double ci_left = lambda - qdelta * se;
		double ci_right = lambda + qdelta * se;
		double prob=((double) totalmessagesLost / (double) totalMessagesSent);

		System.out.println("Total Number of samples:" + n );
		System.out.println("Total number of messages lost :" + totalmessagesLost );
		System.out.println("Total number of messages sent :" + totalMessagesSent);
		System.out.println("Average number of messages that get lost:" + lambda );
		System.out.println(
				"Probability that a sent message get lost:" + prob );
		System.out.println("The qdelta is:" + qdelta );
		System.out.println("Sample variance:" + sigmasq );
		System.out.println("Standard error:" + se );
		System.out.println(delta + "confidence interval: [" + ci_left + "," + ci_right + "]\n\n");

		return prob;
	}

	static void ControlVariable() {
		// if N is the number of lost Messages then M is number of sent messages

		//1000 additional pre-processing samples to compute control variable constant c
		double lostMessages[] = new double[1000];
		double sentMessages[] = new double[1000];
		double z[] = new double[10000];
		
		for (int i = 0; i < 1000; i++) {
			lostMessages[i] = DiscreteEventSimulation.eventSimulation();
			sentMessages[i] = DiscreteEventSimulation.totalsentMessages;
		}
		
		Univariate lost = new Univariate(lostMessages);
		Univariate sent = new Univariate(sentMessages);
		
		double covarianceOfSentAndLostMessages = StatFunctions.cov(lost, sent);

		double VarianceofsentMessages = sent.variance();
		double VarianceoflostMessages = lost.variance();
		double ExpectationofSentMessages = sent.mean();
		//double ExpectationoflostMessages = lost.mean();
		
		double c= (-1) * ((double) (covarianceOfSentAndLostMessages) / (double) (VarianceofsentMessages));
		
		double lostMessagesnew[] = new double[10000];
		double sentMessagesnew[] = new double[10000];
		
		for (int i = 0; i < 10000; i++) {
			
			lostMessagesnew[i] = DiscreteEventSimulation.eventSimulation();
			sentMessagesnew[i] = DiscreteEventSimulation.totalsentMessages;
			
			// Control variable equation
			z[i] = lostMessagesnew[i] + c * (sentMessagesnew[i] - ExpectationofSentMessages);
		}
		
		Univariate finalZ = new Univariate(z);
		double VarianceofZ = finalZ.variance();
		
		System.out.println("Covariance is:\t" + covarianceOfSentAndLostMessages);
		System.out.println("Constant   is:\t" + c);
		System.out.println("Variance of N:\t" + VarianceoflostMessages);
		System.out.println("Variance of Z:\t" + VarianceofZ);
		System.out.println("Percentage Reduction in Variance is:\t" +((VarianceoflostMessages-VarianceofZ)/(VarianceoflostMessages))*100);

	}
	
	static void SimulationResults(){
		
		DiscreteEventSimulation eventObj=new DiscreteEventSimulation();	
		
		System.out.println("Start");
		eventObj.k = 1;
		DiscreteEventSimulation.eventSimulation();
		eventObj.probabilityOfLostMessages=DiscreteEventSimulation.imc(100000, 0.90,false,0);
		
		DiscreteEventSimulation.eventSimulation(eventObj.k);
		eventObj.probabilityOfLostMessagesPrime=DiscreteEventSimulation.imc(100000, 0.90,true,eventObj.k);
		
		System.out.println("Initiallly "+eventObj.probabilityOfLostMessagesPrime+" <"+(eventObj.probabilityOfLostMessages/2));
	    	
		while((eventObj.probabilityOfLostMessagesPrime<(eventObj.probabilityOfLostMessages/2)) && eventObj.k>=1){
			eventObj.k+=1;
			
			System.out.println("k "+eventObj.k );
			
			
			//DiscreteEventSimulation.eventSimulation();
			eventObj.probabilityOfLostMessages=DiscreteEventSimulation.imc(100000, 0.90,false,0);
			
			DiscreteEventSimulation.eventSimulation(eventObj.k);
			eventObj.probabilityOfLostMessagesPrime=DiscreteEventSimulation.imc(100000, 0.90,true,eventObj.k);
			//eventObj.k++;
			
			System.out.println(eventObj.probabilityOfLostMessagesPrime*100+" <"+(eventObj.probabilityOfLostMessages/2)*100);
		}
	}

	
	



	public static void main(String[] args) {

		// Excercise 1
		System.out.println("Excercise 1: \n No of messages lost  :" + DiscreteEventSimulation.eventSimulation());

		// Excercise 2
		System.out.println("\n\n\nExcercise 2:");
		//DiscreteEventSimulation.imc(10000,0.90,false);

		// Excercise 3
		System.out.println("\n\n\nExcercise 3:");
		//DiscreteEventSimulation.ControlVariable();
		
		System.out.println("\n\n\nExcercise 4:");
	    DiscreteEventSimulation.SimulationResults();
		
	}

}
