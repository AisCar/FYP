import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;

public class TuringMachineTest {

  @Test
  public void testOneStateBusyBeaver(){
    State state = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(state);
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    assertEquals(1, tm.getScore());
    assertEquals(1, tm.getShifts());
  }

  @Test
  public void testTwoStateBusyBeaver(){
    ArrayList<State> states = new ArrayList<State>();
    states.add(new State(true, false, 2, true, true, 2));
    states.add(new State(true, true, 1, true, true, 0));
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    assertEquals(4, tm.getScore());
    assertEquals(6, tm.getShifts());
  }

  @Test
  public void testThreeStateBusyBeaver(){
    ArrayList<State> states = new ArrayList<State>();
    states.add(new State(true, false, 2, true, false, 0));
    states.add(new State(false, false, 3, true, false, 2));
    states.add(new State(true, true, 3, true, true, 1));
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    assertEquals(6, tm.getScore());
    assertEquals(14, tm.getShifts());
  }

  @Test
  public void testFourStateBusyBeaver(){
    ArrayList<State> states = new ArrayList<State>();
    states.add(new State(true, false, 2, true, true, 2));
    states.add(new State(true, true, 1, false, true, 3));
    states.add(new State(true, false, 0, true, true, 4));
    states.add(new State(true, false, 4, false, false, 1));
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    assertEquals(13, tm.getScore());
    assertEquals(107, tm.getShifts());
  }

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

  @Test
  public void testAreStatesReachable() {
    System.out.println("\n\ntestAreStatesReachable");
    ArrayList<State> states = new ArrayList<State>();
    states.add(new State(true, false, 2, true, true, 2));
    states.add(new State(true, true, 1, true, true, 0));
    states.add(new State(true, true, 1, true, true, 0)); //3rd state same as 2nd
    TuringMachine tm = new TuringMachine(states);
    tm.areStatesReachable(1);
    //System.out.println(tm.stateReachable[0] + " " + tm.stateReachable[1] + " " + tm.stateReachable[2]);
    assertTrue(tm.stateReachable[0]);
    assertTrue(tm.stateReachable[1]);
    assertTrue(!tm.stateReachable[2]);

  }

}
