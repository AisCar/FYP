import java.util.ArrayList;

public class BusyBeaverPopulation {
  public ArrayList<TuringMachine> turingMachines;

  /*
  Constructor
  */

  public BusyBeaverPopulation(int numStates, int numBusyBeavers){
    turingMachines = generate(numStates, numBusyBeavers);
  }


  /*
  method that generates a random arraylist of TuringMachines
  */

  public ArrayList<TuringMachine> generate(int numStates, int numBusyBeavers){
    //generate numBusyBeavers TuringMachines
    turingMachines = new ArrayList<TuringMachine>();
    for(int i = 0; i < numBusyBeavers; i++){
      //generate numStates states
      ArrayList<State> states = new ArrayList<State>();
      for(int j = 0; j < numStates; j++){
        boolean readZeroWriteOne = Math.random() >= 0.5;
        boolean leftZero = Math.random()>= 0.5;
        int nextStateZero = (int)(100 * Math.random()) % (numStates + 1);
        boolean readOneWriteOne = Math.random() >= 0.5;
        boolean leftOne = Math.random()>= 0.5;
        int nextStateOne = (int)(100 * Math.random()) % (numStates + 1);
        State state = new State(readZeroWriteOne, leftZero, nextStateZero, readOneWriteOne, leftOne, nextStateOne);
        states.add(state);
      }//end inner for loop (generate random states)

      if(!validate(states)) {
        states = repair(states);
      }

      turingMachines.add(new TuringMachine(states));

    }//end outer for loop (generate random turing machines)

    return turingMachines;
  }

  /*
  methods that ensure the validity of the busy beavers generated in method above
  (i.e. the TuringMachines do contain a halt condition)
  */

  public boolean validate(ArrayList<State> states){
    for(State s : states){
      if(s.getNextState(true) == 0 || s.getNextState(false) == 0){
        //If any state has a nextState == 0, then there is a halt condition
        return true;
      }
    }
    //no state contains a halt condition => cannot be a halting TuringMachine
    return false;
  }

  private ArrayList<State> repair(ArrayList<State> states){
    //TODO - introduce halt condition in a random state
    return states;
  }


  /*
  TODO  implement methods that get turing machines in diff formats
  state transition diagrams for UserInterface
  bit strings for GeneticAlgorithm

  note: java.util.BitSet comes with a lot of methods that could use for mutation etc.
  https://docs.oracle.com/javase/6/docs/api/java/util/BitSet.html
  */

  public ArrayList<String> getStateTransitionTables(){
    int i = 1;
    ArrayList<String> strings = new ArrayList();
    for(TuringMachine tm : turingMachines){
      strings.add("Turing Machine " + i);
      i++;
      strings.addAll(getStateTransitionTable(tm));
    }
    return strings;
  }

  public ArrayList<String> getStateTransitionTable(TuringMachine tm){
    ArrayList<String> strings = new ArrayList<String>();
    strings.add("State\tRead\tWrite\tMove\tNext State");
    int stateNum = 1;
    for(State s : tm.getStates()){
      String stateString = "";
      boolean readOne = false;
      for(int i = 0; i < 2; i++){
        String direction = s.getMove(readOne)? "left" : "right";
        int write = s.getWrite(readOne)? 1 : 0;
        stateString = stateString + stateNum + "\t" + i + ":\t " + write + "\t" + direction + "\t" + s.getNextState(readOne) + "\n";
        readOne = true;
      }
      strings.add(stateString);
      stateNum++;
    }
    return strings;
  }



}
