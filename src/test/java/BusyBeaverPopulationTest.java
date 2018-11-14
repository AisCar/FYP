import org.junit.Test;
import static org.junit.Assert.assertEquals;
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
    BitSet bitSet = bbpop.getBitSet(bbpop.getTuringMachines().get(0));
    System.out.println("Testing: " + bitSet.toString());
    //TODO make this a proper test / better BitSet conversion test coverage
  }

  @Test
  public void testConvertIntToBitSet(){
    //testing int to binary conversion before putting it into getBitSet method
    int number = 9;
    String binaryString = Integer.toBinaryString(number);
    BitSet bitSet = new BitSet(10);//10 bits available, but only 4 needed.
    int bitSetEnd = 10;
    for(int stringEnd = binaryString.length(); stringEnd > 0; stringEnd--){
      if(binaryString.charAt(stringEnd-1) == '1'){
        bitSet.set(bitSetEnd);
      }
      bitSetEnd--;
    }
    //System.out.println(bitSet.toString());
    assertEquals(bitSet.toString(), "{7, 10}"); //i.e. 0000001001

  }

  /*
  TODO: more tests
  */

}
