package bgu.dl.features.collections;

import pddl4j.exp.action.Action;


/**
 * @author Shashank Shekhar
 * BGU of the Negev
 *
 */
public class intermediateAction {
	String new_action = null;
	public intermediateAction(String str) {
		this.new_action = str;		
	}
	
	Action intermediate_action = new Action(new_action);
	
}
