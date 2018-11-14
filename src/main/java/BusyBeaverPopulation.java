import java.util.ArrayList;
import java.util.BitSet;

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
  methods for translating TuringMachine objects into BitSets and state transition tables
   */

  public ArrayList<BitSet> getBitSets(){
    ArrayList<BitSet> bitSets = new ArrayList<BitSet>();
    for(TuringMachine tm : turingMachines){
      bitSets.add(getBitSet(tm));
    }
    return bitSets;
  }

  public BitSet getBitSet(TuringMachine tm){
    int numStates = tm.getStates().size();
    //get number of bits needed to represent the nextState attribute
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    //each state has 2 * (1 bit write, 1 bit move, numBitsForNextState bits next state)
    int stateLength = 2 * (2 + numBitsForNextState);
    BitSet tmBitSet = new BitSet(numStates * stateLength);//numStates*2*(2+numBitsForNextState));
    //boolean[] boolArray = new boolean[numStates * stateLength]; //Alternative to BitSet
    int bitIndex = 0;

    for(State s : tm.getStates()){
      //Encode instructions for reading zero
      //Encode write as one bit (using boolean)
      tmBitSet.set(bitIndex, s.getWrite(false));
      bitIndex++;
      //Encode move as one bit
      tmBitSet.set(bitIndex, s.getMove(false));
      bitIndex++;
      //Encode next state as several bits
      String binaryString = Integer.toBinaryString(s.getNextState(false));
      int bitSectionEnd = bitIndex + numBitsForNextState;
      for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
        if(binaryString.charAt(stringEnd-1) == '1'){
          tmBitSet.set(bitSectionEnd);
        }
        bitSectionEnd--;
      }
      bitIndex = bitIndex + numBitsForNextState;

      //Encode instructions for reading one
      //Encode write as one bit
      tmBitSet.set(bitIndex, s.getWrite(true));
      bitIndex++;
      //Encode move as one bit
      tmBitSet.set(bitIndex, s.getMove(true));
      bitIndex++;
      //Encode next state as several bits
      binaryString = Integer.toBinaryString(s.getNextState(true));
      bitSectionEnd = bitIndex + numBitsForNextState;
      for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
        if(binaryString.charAt(stringEnd-1) == '1'){
          tmBitSet.set(bitSectionEnd);
        }
        bitSectionEnd--;
      }
      bitIndex = bitIndex + numBitsForNextState;
    }
    return tmBitSet;
  }

  public ArrayList<String> getStateTransitionTables(){
    int i = 1;
    ArrayList<String> strings = new ArrayList<String>();
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

  public ArrayList<TuringMachine> getTuringMachines(){
    return turingMachines;
  }



}
