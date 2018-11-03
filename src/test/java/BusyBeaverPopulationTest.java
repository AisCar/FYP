import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class BusyBeaverPopulationTest {

  @Test
  public void testGenerateOneBusyBeaver(){
    BusyBeaverPopulation pop = new BusyBeaverPopulation(1,1);
    assertEquals(pop.turingMachines.size(), 1);
    //this is awful test coverage - TODO improve
  }
}
