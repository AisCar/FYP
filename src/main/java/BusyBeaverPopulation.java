import java.util.ArrayList;
import java.util.BitSet;

public class BusyBeaverPopulation {
  public ArrayList<TuringMachine> turingMachines;
  private final int numStates;

  /*
  Constructor
  */

  public BusyBeaverPopulation(int numStates, int numBusyBeavers){
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
  Translation methods
  */

  //Method to translate bit arrays into TuringMachine objects
  public TuringMachine toTuringMachine(boolean[] bitArray){
    ArrayList<State> states = new ArrayList<State>();
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    int stateLength = 2 * (2 + numBitsForNextState);

    if(bitArray.length == (stateLength*numStates)){
      int k = 0;
      boolean writeZero, moveZero, writeOne, moveOne;
      int nextStateZero, nextStateOne;
      //for all states in bit array
      for(int i = 0; i < numStates; i++){
        //convert the binary values to state object parameters
        writeZero = bitArray[k];
        k++;
        moveZero = bitArray[k];
        k++;
        nextStateZero = 0;
        for(int j = numBitsForNextState-1; j >= 0; j--){ //TODO test just this on its own
          if(bitArray[k]){
            nextStateZero += Math.pow(2, j);
          }
          k++;
        }

        writeOne = bitArray[k];
        k++;
        moveOne = bitArray[k];
        nextStateOne = 0;
        for(int j = numBitsForNextState-1; j >= 0; j--){
          if(bitArray[k]){
            nextStateOne += Math.pow(2, j);
          }
          k++;
        }

        //and create the corresponding state
        State state = new State(writeZero, moveZero, nextStateZero, writeOne, moveOne, nextStateOne);
        states.add(state);
      }
    }
    else{
      //TODO error handling
      System.out.println("uh oh!");
    }

    TuringMachine tm = new TuringMachine(states);
    return tm;
  }




  //Method to translate a TuringMachine object into a bit array
  public boolean[] toBitArray(TuringMachine tm){
    int numStates = tm.getStates().size();
    //get number of bits needed to represent the nextState attribute
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    //each state has 2 * (1 bit write, 1 bit move, numBitsForNextState bits next state)
    int stateLength = 2 * (2 + numBitsForNextState);
    boolean[] bitStream = new boolean[numStates * stateLength];
    int bitIndex = 0;

    for(State s : tm.getStates()){
      //Encode instructions for reading zero
      //Encode write as one bit (using boolean)
      bitStream[bitIndex] = s.getWrite(false);
      bitIndex++;
      //Encode move as one bit
      bitStream[bitIndex] = s.getMove(false);
      bitIndex++;
      //Encode next state as several bits
      String binaryString = Integer.toBinaryString(s.getNextState(false));
      int bitSectionEnd = bitIndex + numBitsForNextState - 1;
      for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
        if(binaryString.charAt(stringEnd-1) == '1'){
          bitStream[bitSectionEnd] = true;
        }
        /*
        else{
          bitStream[bitSectionEnd] = false; //Prob not needed, is false by default
        }
        */
        bitSectionEnd--;
      }
      bitIndex = bitIndex + numBitsForNextState;

      //Encode instructions for reading one
      //Encode write as one bit
      bitStream[bitIndex] = s.getWrite(true);
      bitIndex++;
      //Encode move as one bit
      bitStream[bitIndex] = s.getMove(true);
      bitIndex++;
      //Encode next state as several bits
      binaryString = Integer.toBinaryString(s.getNextState(true));
      bitSectionEnd = bitIndex + numBitsForNextState - 1;
      for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
        if(binaryString.charAt(stringEnd-1) == '1'){
          bitStream[bitSectionEnd] = true;
        }
        /*
        else{
          bitStream[bitSectionEnd] = false;
        }
        */
        bitSectionEnd--;
      }
      bitIndex = bitIndex + numBitsForNextState;
    }
    return bitStream;
  }


  //Method to translate TuringMachine objects to state transition tables
  //(for not-yet-implemented user interface - may change a lot later)
  public ArrayList<String> toStateTransitionTable(TuringMachine tm){
    ArrayList<String> strings = new ArrayList<String>();
    strings.add("State\tRead\tWrite\tMove\tNext State");
    int stateNum = 1;
    for(State s : tm.getStates()){
      String stateString = "";
      boolean readOne = false;
      for(int i = 0; i < 2; i++){
        String direction = s.getMove(readOne)? "left" : "right";
        int write = s.getWrite(readOne)? 1 : 0;
        stateString = stateString + stateNum + "\t" + i + "\t " + write +
          "\t" + direction + "\t" + s.getNextState(readOne) + "\n";
        readOne = true;
      }
      strings.add(stateString);
      stateNum++;
    }
    return strings;
  }

  /*
  Helper translation methods
  */
  private int getNumBitsNeeded(int num){
    int numBits = 0;
    while(num > (Math.pow(2,numBits) - 1)){
      numBits++;
    }
    return numBits;
  }

  private boolean[] intToBinary(int num, int numBits){
    boolean[] converted = new boolean[numBits];
    int index = 0;
    for(int power = numBits - 1; power >= 0; power--){
      int twoToPower = Math.pow(2,power);
      if((sum + twoToPower) > num){
        converted[index] = false;
      }
      else{
        converted[index] = true;
        sum = sum + twoToPower
      }
      index++;
    }
    return converted;
  }


  /*
  Getters and setters
  */

  public ArrayList<TuringMachine> getPopulation(){
    return turingMachines;
  }

  public void setPopulation(ArrayList<TuringMachine> nextGeneration){
    this.turingMachines = nextGeneration;
  }

  public ArrayList<boolean[]> getBitArrays(){ //Probably not needed TODO remove?
    ArrayList<boolean[]> bitSets = new ArrayList<boolean[]>();
    for(TuringMachine tm : turingMachines){
      bitSets.add(toBitArray(tm));
    }
    return bitSets;
  }

  public ArrayList<String> getStateTransitionTables(){
    int i = 1;
    ArrayList<String> strings = new ArrayList<String>();
    for(TuringMachine tm : turingMachines){
      strings.add("Turing Machine " + i);
      i++;
      strings.addAll(toStateTransitionTable(tm));
    }
    return strings;
  }


}
