import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.BitSet;

public class PopulationGeneratorTest {

  @Test
  public void testGenerateOneBusyBeaver(){
    //create one 2-state busy beaver
    PopulationGenerator pop = new PopulationGenerator(2,1);
    assertEquals(1, pop.turingMachines.size());
    assertEquals(2, pop.turingMachines.get(0).getStates().size());
  }


  @Test
  public void testGenerateMultipleBusyBeavers(){
    //create three 2-state busy beavers
    PopulationGenerator pop = new PopulationGenerator(2, 3);
    assertEquals(3, pop.turingMachines.size());
    assertEquals(2, pop.turingMachines.get(0).getStates().size());
  }


}
