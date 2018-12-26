import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.BitSet;

public class BusyBeaverPopulationTest {

  @Test
  public void testGenerateOneBusyBeaver(){
    System.out.println("\nTest: testGenerateOneBusyBeaver");

    //create one 2-state busy beaver
    BusyBeaverPopulation pop = new BusyBeaverPopulation(2,1);
    assertEquals(1, pop.turingMachines.size());

    //print out the state transition table of the one busy beaver
    for(String s : pop.toStateTransitionTable(pop.turingMachines.get(0))){
      System.out.println(s);
    }
  }


  @Test
  public void testGenerateMultipleBusyBeavers(){
    System.out.println("\nTest: testGenerateMultipleBusyBeavers");

    //create three 2-state busy beavers
    BusyBeaverPopulation pop = new BusyBeaverPopulation(1,3);
    assertEquals(3, pop.turingMachines.size());

    //print out all state transition tables of all turing machines in population
    for(String s : pop.getStateTransitionTables()){
      System.out.println(s);
    }
  }


  @Test
  public void testToBitArray(){
    System.out.println("\nTest: testToBitArray");
    BusyBeaverPopulation bbpop = new BusyBeaverPopulation(1,1);
    TuringMachine tm = bbpop.getPopulation().get(0);
    /*
    int numStates = tm.getStates().size();
    System.out.println("Size: " + numStates);
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    System.out.println("String length: " + numBitsForNextState);
    System.out.println("Total states length: " +  2 * (2 + numBitsForNextState));
    */
    boolean[] bitStream = bbpop.toBitArray(tm);
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
  public void testConvertIntToBitSet(){
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
    BusyBeaverPopulation bbpop = new BusyBeaverPopulation(1,1);
    TuringMachine tm1 = bbpop.getPopulation().get(0);
    for(String str : bbpop.toStateTransitionTable(tm1)){
      System.out.println(str);
    }
    boolean[] bitArray = bbpop.toBitArray(tm1);
    //System.out.println(bitArray);
    for(boolean b : bitArray){
      System.out.println(b?"1":"0");
    }
    TuringMachine tm2 = bbpop.toTuringMachine(bitArray);
    for(String str : bbpop.toStateTransitionTable(tm2)){
      System.out.println(str);
    }
    State s1 = tm1.getStates().get(0);
    State s2 = tm2.getStates().get(0);

/*
    assertEquals(s1.getWrite(false), s2.getWrite(false));
    assertEquals(s1.getWrite(true), s2.getWrite(true));
    assertEquals(s1.getMove(false), s2.getMove(false));
    assertEquals(s1.getMove(true), s2.getMove(true));
    assertEquals(s1.getNextState(false), s2.getNextState(false));
    assertEquals(s1.getNextState(true), s2.getNextState(true));
*/

  }

  /*
  TODO: more tests
  */

}
