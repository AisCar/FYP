import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class BusyBeaverPopulationTest {

  @Test
  public void testGenerateOneBusyBeaver(){
    System.out.println("testGenerateOneBusyBeaver");
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
    System.out.println("testGenerateMultipleBusyBeavers");
    //create three 2-state busy beavers
    BusyBeaverPopulation pop = new BusyBeaverPopulation(1,3);
    assertEquals(3, pop.turingMachines.size());
    //print out all state transition tables of all turing machines in population
    for(String s : pop.getStateTransitionTables()){
      System.out.println(s);
    }
  }

  /*
  TODO: more tests
  */
  
}
