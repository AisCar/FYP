import java.util.ArrayList;

public class Translator {
  private final int numStates;

  /*
  Constructor
  */

  public Translator(int numStates){
    this.numStates = numStates;
  }


  /*
  Translation methods
  */

  //Method to translate bit arrays into TuringMachine objects
  public TuringMachine toTuringMachine(boolean[] bitArray){
    ArrayList<State> states = new ArrayList<State>();
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

        temp = new boolean[numBitsForNextState];
        for(int j = 0; j < numBitsForNextState; j++){
          temp[j] = bitArray[k];
          k++;
        }
        nextStateOne = binaryToInt(temp);

        //create the corresponding state
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
      boolean[] temp2 = intToBinary(s.getNextState(true), numBits);
      for(boolean b : temp2){
        bitArray[bitIndex] = b;
        bitIndex++;
      }
    }
    return bitArray;
  }



  /*
  Helper methods
  */
  protected int getNumBitsNeeded(int num){
    int numBits = 0;
    while(num > (Math.pow(2,numBits) - 1)){
      numBits++;
    }
    return numBits;
  }

  protected boolean[] intToBinary(int num, int numBits){
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

  public int binaryToInt(boolean[] binaryArr){
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
  Batch translation methods (may be unnecessary)
  */

  public ArrayList<boolean[]> getBitArrays(ArrayList<TuringMachine> turingMachines){ //Probably not needed TODO remove?
    ArrayList<boolean[]> bitSets = new ArrayList<boolean[]>();
    for(TuringMachine tm : turingMachines){
      bitSets.add(toBitArray(tm));
    }
    return bitSets;
  }


}
