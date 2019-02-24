import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.BitSet;

public class TranslatorTest {
  TuringMachine tm;
  Translator t;

  @Before
  public void initialise(){
    State s = new State(true, false, 1, false, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(s);

    this.tm = new TuringMachine(states);
    this.t = new Translator(1);
  }

  @Test
  public void testToBitArray(){
    System.out.println("\nTest: testToBitArray");
    /*
    int numStates = tm.getStates().size();
    System.out.println("Size: " + numStates);
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    System.out.println("String length: " + numBitsForNextState);
    System.out.println("Total states length: " +  2 * (2 + numBitsForNextState));
    */
    boolean[] bitStream = this.t.toBitArray(tm);
    assertEquals(6, bitStream.length);
    String bitString = "";
    for(int i = 0; i < bitStream.length; i++){
      if(bitStream[i]) bitString += "1";
      else bitString += "0";
    }
    System.out.println("Randomly generated Turing machine chromosome: " + bitString);
    //TODO make this a proper test
  }

  @Test
  public void testConvertIntToBitSet(){ //legacy? can remove?
    //testing int to binary conversion before putting it into getBitSet method
    int number = 9;
    String binaryString = Integer.toBinaryString(number);
    //BitSet bitSet = new BitSet(10);//10 bits available, but only 4 needed.
    boolean[] bitSet = new boolean[10];
    int bitSetEnd = 9;
    for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
      if(binaryString.charAt(stringEnd-1) == '1'){
        bitSet[bitSetEnd] = true;
      }
      //shouldn't need an else because booleans are false by default
      bitSetEnd--;
    }

    // assert 1's are true and 0's are false (9 = 0000001001)
    assertTrue(bitSet[6]);
    assertTrue(bitSet[9]);
    assertTrue(!bitSet[0]);

  }

  @Test
  public void testBitArrayToIntConversion(){
    boolean[] array = {true, false, true};//101 = 5
    int number = 0;
    int k = 0;
    for(int j = array.length-1; j >= 0; j--){
      if(array[k]){
        number += Math.pow(2, j);
      }
      k++;
    }

    assertEquals(number, 5);
  }

  @Test
  public void testTuringMachineToBitArrayAndBack(){
    System.out.println("\ntestTuringMachineToBitArrayAndBack");
    //Print original Turing machine
    System.out.println(this.tm.toString());
    //Convert it to a binary representation
    boolean[] bitArray = this.t.toBitArray(this.tm);
    String binaryStr = "Binary chromosome representation: ";
    for(boolean b : bitArray){
      binaryStr = binaryStr + (b?"1":"0");
    }
    //Print the binary representation
    System.out.println(binaryStr);
    //Convert it back to a TuringMachine
    TuringMachine tm2 = this.t.toTuringMachine(bitArray);
    //Print the resulting Turing machine
    System.out.println(tm2.toString());

    //Confirm that twice translated TuringMachine is identical to original
    State s1 = this.tm.getStates().get(0);
    State s2 = tm2.getStates().get(0);
    assertEquals(s1.getWrite(false), s2.getWrite(false));
    assertEquals(s1.getWrite(true), s2.getWrite(true));
    assertEquals(s1.getMove(false), s2.getMove(false));
    assertEquals(s1.getMove(true), s2.getMove(true));
    assertEquals(s1.getNextState(false), s2.getNextState(false));
    assertEquals(s1.getNextState(true), s2.getNextState(true));
  }

  @Test
  public void testGetNumBits(){
    assertEquals(1, this.t.getNumBitsNeeded(1));
    assertEquals(2, this.t.getNumBitsNeeded(2));
    assertEquals(3, this.t.getNumBitsNeeded(4));
    assertEquals(3, this.t.getNumBitsNeeded(7));
    assertEquals(4, this.t.getNumBitsNeeded(8));
  }


}
