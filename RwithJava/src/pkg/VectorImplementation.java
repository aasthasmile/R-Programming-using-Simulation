package pkg;

import org.rosuda.JRI.Rengine;


public class VectorImplementation {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String javavector="c(1,2,3,4,5)";
		
		Rengine engine=new Rengine(new String[] { "--no-save" }, false, null);
		engine.eval("rVector="+javavector);
		engine.eval("meanVal=mean(rVector)");
		double mean = engine.eval("meanVal").asDouble();
		System.out.println("Mean of given vector is=" + mean);
		
	}

}
