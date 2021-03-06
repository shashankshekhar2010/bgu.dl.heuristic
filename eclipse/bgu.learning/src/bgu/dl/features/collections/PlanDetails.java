package bgu.dl.features.collections;

import java.util.ArrayList;

public class PlanDetails {
	private int plan_length;
	private ArrayList<String> generatedRealPlan;

	public PlanDetails()
	{
		this.plan_length = 1000000;
		this.generatedRealPlan = new ArrayList<String>();
	}
	
	public int getPlanLength() {
		return plan_length;
	}
	public void setPlanLength(int plan_length) {
		this.plan_length = plan_length;
	}
	public ArrayList<String> getGeneratedRealPlan() {
		return generatedRealPlan;
	}
	public void setGeneratedRealPlan(ArrayList<String> generatedRealPlan) {
		this.generatedRealPlan = generatedRealPlan;
	}	
}
