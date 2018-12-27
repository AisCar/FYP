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
    //int numBitsForNextState = Integer.toBinaryString(numStates).length();
    int numBitsForNextState = getNumBitsNeeded(this.numStates);
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
        //nextStateZero = 0;
        /*
        for(int j = numBitsForNextState-1; j >= 0; j--){
          if(bitArray[k]){
            nextStateZero += Math.pow(2, j);
          }
          k++;
        }
        */
        boolean[] temp = new boolean[numBitsForNextState];
        for(int j = 0; j < numBitsForNextState; j++){
          temp[j] = bitArray[k];
          k++;
        }
        nextStateZero = binaryToInt(temp);

        writeOne = bitArray[k];
        k++;
        moveOne = bitArray[k];
        k++;
        /*
        nextStateOne = 0;
        for(int j = numBitsForNextState-1; j >= 0; j--){
          if(bitArray[k]){
            nextStateOne += Math.pow(2, j);
          }
          k++;
        }
        */
        temp = new boolean[numBitsForNextState];
        for(int j = 0; j < numBitsForNextState; j++){
          temp[j] = bitArray[k];
          k++;
        }
        nextStateOne = binaryToInt(temp);

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


  /*
  //Method to translate a TuringMachine object into a bit array
  public boolean[] toBitArray(TuringMachine tm){
    int numStates = tm.getStates().size();//TODO remove - is now an object variable
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
      int numBits = getNumBitsNeeded(this.numStates);
      boolean[] temp = intToBinary(s.getNextState(false), numBits);
      for(boolean b : temp){
        bitStream[bitIndex] = b;
        bitIndex++;
      }
      //Encode instructions for reading one
      //Encode write as one bit
      bitStream[bitIndex] = s.getWrite(true);
      bitIndex++;
      //Encode move as one bit
      bitStream[bitIndex] = s.getMove(true);
      bitIndex++;
      //Encode next state as several bits
      boolean[] temp2 = intToBinary(s.getNextState(false), numBits);
      for(boolean b : temp2){
        bitStream[bitIndex] = b;
        bitIndex++;
      }
    }
    return bitStream;
  }

  */

  //Method to translate a TuringMachine object into a bit array
  public boolean[] toBitArray(TuringMachine tm){
    //Temporary bug fix so that I can focus elsewhere and come back to this
    //(If it's dumb, but it works...)
    //TODO fix issues with original version (commented out below)
    String str = "";
    int numBits = getNumBitsNeeded(this.numStates);
    for(State s : tm.getStates()){
      str = str + (s.getWrite(false)? "1":"0");
      str = str + (s.getMove(false)? "1":"0");
      boolean[] zeroTempArr = intToBinary(s.getNextState(false), numBits);
      for(int i = 0; i < numBits; i++){
        str = str + (zeroTempArr[i]? "1":"0");
      }
      str = str + (s.getWrite(true)? "1":"0");
      str = str + (s.getMove(true)? "1":"0");
      boolean[] oneTempArr = intToBinary(s.getNextState(true), numBits);
      for(int i = 0; i < numBits; i++){
        str = str + (oneTempArr[i]? "1":"0");
      }
    }

    boolean[] bitArray = new boolean[str.length()];

    for(int i = 0; i < str.length(); i++){
      if(str.charAt(i)=='1'){
        bitArray[i] = true;
      }
      else{
        bitArray[i] = false;
      }
    }

    /*
    int bitIndex = 0;
    int bitsForNextState = getNumBitsNeeded(this.numStates);
    int sizeArray = this.numStates*2*(1+1+bitsForNextState);
    boolean[] bitArray = new boolean[sizeArray];

    for(State s : tm.getStates()){
      //Encode instructions for reading zero
      //Encode write as one bit (using boolean)
      bitArray[bitIndex] = s.getWrite(false);
      bitIndex++;
      //Encode move as one bit
      bitArray[bitIndex] = s.getMove(false);
      bitIndex++;
      //Encode next state as several bits
      int numBits = getNumBitsNeeded(this.numStates);
      boolean[] temp = intToBinary(s.getNextState(false), numBits);
      for(boolean b : temp){
        bitArray[bitIndex] = b;
        bitIndex++;
      }
      //Encode instructions for reading one
      //Encode write as one bit
      bitArray[bitIndex] = s.getWrite(true);
      bitIndex++;
      //Encode move as one bit
      bitArray[bitIndex] = s.getMove(true);
      bitIndex++;
      //Encode next state as several bits
      boolean[] temp2 = intToBinary(s.getNextState(false), numBits);
      for(boolean b : temp2){
        bitArray[bitIndex] = b;
        bitIndex++;
      }
    }
    */
    return bitArray;
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
  protected int getNumBitsNeeded(int num){
    int numBits = 0;
    while(num > (Math.pow(2,numBits) - 1)){
      numBits++;
    }
    return numBits;
  }

  private boolean[] intToBinary(int num, int numBits){
    boolean[] converted = new boolean[numBits];
    int index = 0;
    int sum = 0;
    for(int power = numBits - 1; power >= 0; power--){
      int twoToPower = (int)(Math.pow(2,power));
      if((sum + twoToPower) > num){
        converted[index] = false;
      }
      else{
        converted[index] = true;
        sum = sum + twoToPower;
      }
      index++;
    }
    return converted;
  }

  private int binaryToInt(boolean[] binaryArr){
    int num = 0;
    int power = binaryArr.length - 1;
    for(boolean b : binaryArr){
      if(b){
        num = num + (int) Math.pow(2,power);
      }
      power--;
    }
    return num;
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
