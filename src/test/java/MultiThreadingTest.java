import org.junit.Test;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import static org.mockito.Mockito.*;

public class MultiThreadingTest {

  @Test
  public void testTuringMachineRunTask(){
    System.out.println("testTuringMachineRunTask");
    State s = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(s);
    TuringMachine tm = new TuringMachine(states);
    TuringMachine tmSpy = spy(tm);
    TuringMachineRunTask tmTask = new TuringMachineRunTask(tmSpy);
    try{
      tmTask.call();
    }
    catch(Exception e){
      System.out.println(e.getMessage() + "\n" + e.getStackTrace());
      fail();
    }
    verify(tmSpy, times(1)).run();
  }

  @Test
  public void testRunMultiThreadedGeneticAlgorithmTest(){
    System.out.println("testRunMultiThreadedGeneticAlgorithmTest");
    try {
      int popSize = 10; //keep it small
      int numStates = 4;
      int numGenerations = 10;
      double crossoverRate = 0.6;
      double mutationRate = 0.05;
      double elitismRate = 0.1;
      GeneticAlgorithm ga = new GeneticAlgorithm(popSize, numStates, numGenerations, crossoverRate, mutationRate, elitismRate);
      ga.enableMultithreading(true);
      ga.run();
      //Ok what now?
    }
    catch(GeneticAlgorithmException gae){
      fail();
    }
  }


}
