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
    for(String s : pop.getStateTransitionTable(pop.turingMachines.get(0))){
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
  public void testGetBitSet(){
    System.out.println("\nTest: testGetBitSet");
    BusyBeaverPopulation bbpop = new BusyBeaverPopulation(1,1);
    TuringMachine tm = bbpop.getTuringMachines().get(0);
    /*
    int numStates = tm.getStates().size();
    System.out.println("Size: " + numStates);
    int numBitsForNextState = Integer.toBinaryString(numStates).length();
    System.out.println("String length: " + numBitsForNextState);
    System.out.println("Total states length: " +  2 * (2 + numBitsForNextState));
    */
    boolean[] bitStream = bbpop.getBitSet(tm);
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

  /*
  TODO: more tests
  */

}
