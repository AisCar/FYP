import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;

public class TuringMachineTest {

  @Test
  public void TestOneStateBusyBeaver(){
    State state = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(state);
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    assertEquals(1, tm.getScore());
    assertEquals(1, tm.getShifts());
  }

  //TODO: 2, 3, 4 state Busy Beavers

  //TODO: non-halting Busy Beaver(s)

  @Test
  public void testSortTuringMachinesByFitness(){
    //Create 1 state Turing Machine
    State state = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(state);

    //Create arraylist to store 10 of the above Turing Machine with diff scores
    ArrayList<TuringMachine> arrayList = new ArrayList<TuringMachine>();
    for(int i = 0; i < 10; i ++){
      TuringMachine tm = new TuringMachine(states);
      //create arbitrary scores in ascending order
      tm.score = i;
      arrayList.add(tm);
    }

    //Test that Turing Machines can be sorted - descending instead of ascending
    assertEquals(arrayList.get(0).score, 0);
    assertEquals(arrayList.get(9).score, 9);
    Collections.sort(arrayList);
    assertEquals(arrayList.get(0).score, 9);
    assertEquals(arrayList.get(9).score, 0);
  }

}
