package bgu.dl.features.collections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import pddl4j.PDDLObject;
import pddl4j.exp.AtomicFormula;
import pddl4j.exp.Exp;
import pddl4j.exp.NotAtomicFormula;
import pddl4j.exp.term.Constant;

import bgu.dl.features.learning.PossibleGroundedLiterals;
import bgu.dl.features.learning.ProblemDetails;

/**
 * @author Shashank Shekhar
 * BGU of the Negev
 * @throws FileNotFoundException 
 */
public class DataSet 
{	
	ProblemDetails details;
	PDDLObject pddlObject;
	ArrayList<ArrayList<ArrayList>> listOfParentChild;
	ArrayList<PossibleGroundedActions> groundedActions; 	
	ArrayList<ArrayList<ArrayList>> listParentChildSuccessors;
	ArrayList<ArrayList> initialParentChildStates;

	public DataSet() {
		this.details = null;
		this.pddlObject = null;
		this.listOfParentChild = new ArrayList<ArrayList<ArrayList>>();
		this.groundedActions = new ArrayList<PossibleGroundedActions>();
		this.listParentChildSuccessors = new ArrayList<ArrayList<ArrayList>>();
		this.initialParentChildStates = new ArrayList<ArrayList>();
	}

	public void dataSet(ProblemDetails details, PDDLObject object) {
		this.details = details;
		this.pddlObject = object;
		ArrayList dummy_state = new ArrayList();
		initialParentChildStates.add(dummy_state);
		initialParentChildStates.add(details.getInitialState());
		@SuppressWarnings("rawtypes")
		ArrayList initialState = this.details.getInitialState();
		@SuppressWarnings("rawtypes")
		ArrayList goalState = this.details.getGoalState();
	}	

	public void callForDatasetGeneration()
	{
		int counter = 0;
		File file;
		Writer writer;
		PossibleGroundedLiterals possibleGroundedLiterals = new PossibleGroundedLiterals(pddlObject);		
		ArrayList<AtomicFormula> listOfPossiblePropositions = new ArrayList<>();
		listOfPossiblePropositions = possibleGroundedLiterals.allPossibleLiteralsMayOccur();
		this.listParentChildSuccessors.add(initialParentChildStates);
		details.generateGroundedActions();
		// Call to prepare the data set
		try {
			file = new File("/home/bgumodo1/Dropbox/Bgu-Files/bgu.dl.heuristic/eclipse/bgu.learning/src/bgu/dl/features/train.txt");
			writer = new BufferedWriter(new FileWriter(file));
			String header = "";
			for (int i = 0; i < listOfPossiblePropositions.size(); i++) {
				header = header + listOfPossiblePropositions.get(i) + "\t";				
			}
			header = header +"target \n";
			writer.append(header);
			// Infinite call, pass the main training file.
			while (counter++ < 50000 && listParentChildSuccessors.size() > 0) 
			{		
				int randomIndex = randomNumber();
				System.out.println(" counter : "+counter);
				ArrayList<ArrayList> parentChildStates = listParentChildSuccessors.get(randomIndex); 
				if(parentChildStates.size()<2)
				{
					System.out.println("Error in parent child generation in the forward direction..!"); 
					System.out.println(listParentChildSuccessors.get(randomIndex).get(0));
					System.out.println(listParentChildSuccessors.get(randomIndex).get(1));
					System.out.println(parentChildStates.get(0).toString());
					System.out.println(parentChildStates.get(1).toString());
				}

				listParentChildSuccessors.remove(randomIndex);		
				ArrayList<ArrayList<ArrayList>> getParentChildSuccessors = getParentChildSuccessors(parentChildStates, writer, listOfPossiblePropositions);
				/*for (int i = 0; i < getParentChildSuccessors.size(); i++) {
					System.out.println(getParentChildSuccessors.get(i).size()); 
				}*/
				listParentChildSuccessors.addAll(getParentChildSuccessors);
			}
			writer.close();			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	/**
	 * Random Number Generator
	 * @return an index
	 */
	private int randomNumber() { 
		Random r = new Random();
		int Low = 0;
		int High = listParentChildSuccessors.size();
		int Result = r.nextInt(High-Low) + Low;
		return Result;
	}

	/**
	 * method generateSuccessorParentChild
	 * @return a list of two states (parent and its child)
	 * A state is also represented in form of a list
	 */
	private ArrayList<ArrayList<ArrayList>> getParentChildSuccessors(ArrayList<ArrayList> parentChildStates, Writer writer, ArrayList<AtomicFormula> listOfPossiblePropositions) 
	{
		// System.out.println("\t" + parentChildStates.size()); 
		ArrayList<ArrayList<ArrayList>> generateParentChildSuccessors = new ArrayList<ArrayList<ArrayList>>();
		ArrayList parentState = parentChildStates.get(0);
		ArrayList childState = parentChildStates.get(1); 
		ArrayList<ArrayList> newParentChildStates = null;

		@SuppressWarnings("rawtypes")
		ArrayList<ArrayList> allPossibleStatetsInForwardDirection = new ArrayList<>();
		allPossibleStatetsInForwardDirection = allPossibleStatetsInForwardDirection(childState);
		// System.out.println(allPossibleStatetsInForwardDirection.size()); 
		/**
		 * Keep in mind that, training data points will be computed using each child state.
		 * Call to the Fast Downward by passing the child state, and
		 * the goal state. 
		 */
		PossibleGroundedLiterals possibleGroundedLiterals = new PossibleGroundedLiterals(pddlObject);		
		ArrayList<Integer> listOfIntegersCorrespondingToLiterals = generateDataset(childState, listOfPossiblePropositions);
		// this target is not generating properly
		int target = targetByFastDownward(childState, this.details.getGoalState(), possibleGroundedLiterals);

		listOfIntegersCorrespondingToLiterals.add(target);
		// write each training point into a file
		String data = "";
		for (int i = 0; i < listOfIntegersCorrespondingToLiterals.size(); i++) {
			data = data +listOfIntegersCorrespondingToLiterals.get(i) + "\t";
		}		
		data = data + "\n";
		try {
			// System.out.println(data);
			writer.append(data);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		for (int i = 0; i < allPossibleStatetsInForwardDirection.size(); i++) 
		{
			if(! isSuccessorItsParentState(parentState,allPossibleStatetsInForwardDirection.get(i))) {
				// changed on Oct 17 - Shashank
				newParentChildStates = new ArrayList<ArrayList>();
				newParentChildStates.add(childState);
				newParentChildStates.add(allPossibleStatetsInForwardDirection.get(i));
				// System.out.println(newParentChildStates.size()); 
				generateParentChildSuccessors.add(newParentChildStates);
			}						
		}
		return generateParentChildSuccessors;
	}

	/**
	 * Function call to the fast downward planner | keep in mind that we will kill this call after certain time (say 30 minutes). 
	 * @param initialState
	 * @param goalState
	 * @return returns the target value;
	 */
	private Integer targetByFastDownward(ArrayList initialState, ArrayList goalState, PossibleGroundedLiterals possibleGroundedLiterals) 
	{
		int target = 1000000;
		ArrayList<Constant> listOfConstants = possibleGroundedLiterals.listOfConstants();
		// a call to create a problem file with new initial state
		generateProblemFile(initialState);		
		// a call to the fast downward python script
		try {
			String[] command = {
					"/home/bgumodo1/Documents/Copy-IITM/Research-Edited/Fast-Downward/fast-downward.py",
					"/home/bgumodo1/Dropbox/Bgu-Files/bgu.dl.heuristic/eclipse/bgu.learning/src/bgu/dl/features/learning/domain.pddl",
					"/home/bgumodo1/Dropbox/Bgu-Files/bgu.dl.heuristic/eclipse/bgu.learning/src/bgu/dl/features/learning/problem.pddl",
					"--heuristic",
					"h=ff()",
					"--search",
					"lazy_greedy(h, preferred=h)"
			};
			Process pro = Runtime.getRuntime().exec(command);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line = null;
			String[] planDetails = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("Plan length")) {
					planDetails = line.split(" ");
				}		
			}
			target = Integer.parseInt(planDetails[2]);
			// System.out.println(target);
		} catch (Exception e) {
			System.err.println("Error in writing the planner output in file !!");
		}
		return target;
	}

	/**
	 * Will write a new initial state 
	 * @param initialState
	 * @param goalState
	 * @param listOfConstants */
	private void generateProblemFile(ArrayList initialState) 
	{ 
		List<String> lines = new ArrayList<String>();
		String line = null;
		try {
			File f1 = new File("/home/bgumodo1/Dropbox/Bgu-Files/bgu.dl.heuristic/eclipse/bgu.learning/src/bgu/dl/features/learning/problem.pddl");
			FileReader fr = new FileReader(f1);
			BufferedReader br = new BufferedReader(fr);
			String str ="(:init ";
			for (int i = 0; i < initialState.size(); i++) {
				str = str + initialState.get(i).toString();
			}
			str = str + ")";
			while ((line = br.readLine()) != null) {
				if (line.contains(":init") || line.contains(":INIT") )
				{
					line = line.replace(line, str);
				}
				lines.add(line);
			}
			fr.close();
			br.close();

			FileWriter fw = new FileWriter(f1);
			BufferedWriter out = new BufferedWriter(fw);
			for(String s : lines)
			{
				out.write(s);
				out.write("\n");
			}
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Will write a new initial state 
	 * @param initialState
	 * @param goalState
	 * @param listOfConstants */
	private void generateProblemFileOld(ArrayList initialState) {
		String oldFileName = "/home/bgumodo1/Dropbox/Bgu-Files/bgu.dl.heuristic/eclipse/bgu.learning/src/bgu/dl/features/learning/problem.pddl";
		String newState = "";
		for (int i = 0; i < initialState.size(); i++) {
			newState = newState + initialState.get(i).toString();
		}
		String line = null;
		try {
			FileReader fr = new FileReader(oldFileName);
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter(oldFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			boolean flagInit = false;
			boolean flagGoal = false;
			while ((line = br.readLine()) != null) {
				if (!flagInit && line.contains("INIT")) {
					flagInit = true; continue;
				}
				if (flagInit) {
					line = br.readLine();
					line.replace(line, newState);
					flagInit = false;
				} 
				bw.write(line);
			}              
		}     //end try
		catch (Exception e) {
			System.err.println("Exception in updating the initial state");
		}
	}

	// Calling to feed -1, 0, 1 corresponding to each entry in the list of proposition
	private ArrayList<Integer> generateDataset(ArrayList initialState, ArrayList<AtomicFormula> listOfPossiblePropositions) 
	{
		ArrayList<Integer> listOfInt = new ArrayList<Integer>();
		for (int i = 0; i < listOfPossiblePropositions.size(); i++) {
			listOfInt.add(0);
		}
		for (int i = 0; i < listOfPossiblePropositions.size(); i++) {
			AtomicFormula af = listOfPossiblePropositions.get(i);
			int val = 0; 
			for (int j = 0; j < initialState.size(); j++) {
				AtomicFormula fromInit = (AtomicFormula)initialState.get(j);
				if (fromInit.getClass().equals(AtomicFormula.class)) {
					if (fromInit.toString().equals(af.toString())) {
						val = 1;
						break;
					}
				} else if (fromInit.getClass().equals(NotAtomicFormula.class)){
					if (fromInit.toString().equals(af.toString())) {
						val = -1;
						break;
					}
				}
			}
			try {
				listOfInt.set(i, val);
				// System.out.println("how many times?");
			} catch (Exception e) {
				System.out.println("error - updating values!");	
			}
		}
		return listOfInt;
	}

	private ArrayList<ArrayList> allPossibleStatetsInForwardDirection(ArrayList childNode) {
		ArrayList<PossibleGroundedActions> getApplicableActions = new ArrayList<PossibleGroundedActions>();
		getApplicableActions = getApplicableActions(childNode);
		if(getApplicableActions.size() == 0) {
			System.out.println("No actions are appicable on this child node: returning null");
			return null;
		}		
		ArrayList<ArrayList> listOfSuccessorStates = new ArrayList<ArrayList>();
		for (int i = 0; i < getApplicableActions.size(); i++) {
			getApplicableActions.get(i).printGroundedAction();
		}
		for (int i = 0; i < getApplicableActions.size(); i++) {
			listOfSuccessorStates.add(applyAction(getApplicableActions.get(i), childNode)); 
		}
		return listOfSuccessorStates;
	}

	/**
	 * Generate all applicable actions
	 * @param childNode
	 * @return applicable actions
	 */
	@SuppressWarnings("unused")
	private ArrayList<PossibleGroundedActions> getApplicableActions(ArrayList<AtomicFormula> childNode) 
	{
		ArrayList<PossibleGroundedActions> applicableActions = new ArrayList<PossibleGroundedActions>();
		groundedActions = new ArrayList<PossibleGroundedActions>();
		groundedActions.addAll(details.getgActions());
		// applicableActions.addAll(details.getgActions());
		@SuppressWarnings("rawtypes")
		Iterator itr = groundedActions.iterator();
		while(itr.hasNext()) 
		{
			PossibleGroundedActions ga = (PossibleGroundedActions) itr.next();
			ArrayList<AtomicFormula> preCond = ga.getPreCond();			
			if(isSubsetOf(preCond,childNode)) {
				applicableActions.add(ga);
			}
		}
		return applicableActions;
	}

	/**
	 * Check whether state g is a subset of current state curr
	 * @param g
	 * @param cur
	 * @return
	 */
	private boolean isSubsetOf(ArrayList<AtomicFormula> g, ArrayList<AtomicFormula> cur) {
		return(cur.containsAll(g));		
	}

	/**
	 * Applies a grounded action on the given state
	 * @param groundedAction
	 * @param childNode
	 * @return A successor state
	 */
	@SuppressWarnings("unused")
	private ArrayList<AtomicFormula> applyAction(PossibleGroundedActions groundedAction, ArrayList<AtomicFormula> childNode) 
	{
		ArrayList<AtomicFormula> successorState = new ArrayList<AtomicFormula>();
		successorState.addAll(childNode);
		ArrayList<Exp> removeNeg = new ArrayList<Exp>();
		// System.out.println(childNode.toString());
		groundedAction.printGroundedAction();

		/** Formula: S_{new} = {S_{curr} - ett^{-}(a)} U {eff^{+}(a)} */
		Iterator<AtomicFormula> itrNew = successorState.iterator();
		while(itrNew.hasNext()) {
			Exp exp = itrNew.next();
			if(groundedAction.getNegEff().contains(exp))
				removeNeg.add(exp);
		}
		/** Set Operations */
		successorState.removeAll(removeNeg);
		successorState.addAll(groundedAction.getPosEff());
		// System.out.println(successorState.toString());
		return successorState;
	}

	/**
	 * Check if the successor state is the parent node.
	 * @param newState
	 * @return
	 */
	private boolean isSuccessorItsParentState(ArrayList parentState, ArrayList<AtomicFormula> successorState) {
		if(parentState.containsAll(successorState) && successorState.containsAll(parentState))
			return true;
		return false;
	}
}

