import java.util.ArrayList;

public class PopulationGenerator {
  public ArrayList<TuringMachine> turingMachines;
  private final int numStates;

  /*
  Constructor
  */

  public PopulationGenerator(int numStates, int numBusyBeavers){
    this.numStates = numStates;
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
        int nextStateZero = ((int)(100 * Math.random())) % (numStates + 1);
        boolean readOneWriteOne = Math.random() >= 0.5;
        boolean leftOne = Math.random()>= 0.5;
        int nextStateOne = ((int)(100 * Math.random())) % (numStates + 1);
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

  public ArrayList<TuringMachine> getPopulation(){
    return turingMachines;
  }

}
