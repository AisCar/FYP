import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import static org.mockito.Mockito.*;

public class TuringMachineTest {
  TuringMachine oneStateHalting;
  TuringMachine oneStateNonHalting;
  TuringMachine twoStateNonHalting;

  @Before
  public void intitialise(){
    State state1 = new State(true, true, 0, true, true, 0);
    ArrayList<State> states1 = new ArrayList<State>();
    states1.add(state1);
    this.oneStateHalting = new TuringMachine(states1);

    State state2 = new State(true, true, 1, true, true, 1);
    ArrayList<State> states2 = new ArrayList<State>();
    states2.add(state2);
    this.oneStateNonHalting = new TuringMachine(states2);

    ArrayList<State> states3 = new ArrayList<State>();
    states3.add(state2);
    states3.add(state1);
    this.twoStateNonHalting = new TuringMachine(states3);
  }

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

  @Test
  public void testTime(){
    //Benchmark against C code (have to comment out extra method calls if actually comparing)
    //Uses the same Turing machine as 3-state C test
    State first = new State(true, false,2,true,false,0);
    State second = new State(false,false,3,true,false,2);
    State third = new State(true,true,3,true,true,1);
    ArrayList<State> states = new ArrayList();
    states.add(first);
    states.add(second);
    states.add(third);
    TuringMachine tm = new TuringMachine(states);

    long start = System.nanoTime();
    tm.run();
    long end = System.nanoTime();
    System.out.println("Time taken: " + (end - start) + "nanoseconds");
    System.out.println("(Thats " + ((end-start)/1000) + "microseconds)");
  }

  @Test
  public void testSortTuringMachinesByFitness(){
    System.out.println("testSortTuringMachinesByFitness");
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

  @Test
  public void testCountHalts(){
      this.oneStateHalting.countHalts();
      assertEquals(2, this.oneStateHalting.numHalts);
      this.oneStateNonHalting.countHalts();
      assertEquals(0, this.oneStateNonHalting.numHalts);
      this.twoStateNonHalting.countHalts();
      assertEquals(2, this.twoStateNonHalting.numHalts);
  }

  @Test
  public void testFitnessCalculations(){
    System.out.println("\n\ntestFitnessCalculations");
    State state = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(state);
    TuringMachine tm = new TuringMachine(states);
    tm.run();
    //tm.areStatesReachable(1);
    int fitness = tm.getFitness();
    System.out.println("Fitness = " + fitness);

  }

  @Test
  public void testHaltReachable(){
    assertTrue(oneStateHalting.haltReachable());
    assertTrue(!oneStateNonHalting.haltReachable());
    assertTrue(!twoStateNonHalting.haltReachable());
  }

  @Test
  public void testIsInNonHaltingCycle(){
    //create spies here => don't count method invocations from other unit tests on same TMs
    TuringMachine spy1 = spy(oneStateNonHalting);
    TuringMachine spy2 = spy(oneStateHalting);
    State s1 = spy1.getStates().get(0); //There's only one state
    State s2 = spy2.getStates().get(0);
    assertTrue(spy1.isInNonHaltingCycle(s1, new TapeCell()));
    assertTrue(!spy2.isInNonHaltingCycle(s2, new TapeCell()));
    verify(spy1, times(1)).isMovingOneDirectionIndefinitely(anyInt());//actually, is 1, i think
    verify(spy2, never()).isMovingOneDirectionIndefinitely(anyInt());
    //more states?
  }

  @Test
  public void testIsMovingOneDirectionIndefinitely(){
    /* Note: This method should not be called directly w/o first assessing
    that TM is moving over blank cells. However. 1 state TMs only have 1 state
    and start on blank cells. So should work out here. */
    assertTrue(oneStateNonHalting.isMovingOneDirectionIndefinitely(1));
    assertTrue(!oneStateHalting.isMovingOneDirectionIndefinitely(1));
  }

  @Test
  public void testIsMovingMostlyOneDirectionIndefinitely(){
    /* Note: This method should not be called directly w/o first assessing
    that TM is moving over blank cells. However. 1 state TMs only have 1 state
    and start on blank cells. So should work out here. */
    assertTrue(!oneStateNonHalting.isMovingMostlyOneDirectionIndefinitely(1)); //writes ones
    assertTrue(!oneStateHalting.isMovingMostlyOneDirectionIndefinitely(1)); //next state is 0
  }

  @Test
  public void testRunIsCancelledAfter100Shifts(){ //because TM isMovingOneDirectionIndefinitely
    System.out.println("testRunIsCancelledAfter100Shifts");
    TuringMachine tmSpy = spy(oneStateNonHalting);
    when(tmSpy.haltReachable()).thenReturn(true);
    tmSpy.run();
    verify(tmSpy, times(1)).isInNonHaltingCycle(any(State.class), any(TapeCell.class));
    verify(tmSpy, times(1)).isMovingOneDirectionIndefinitely(anyInt());
    assertEquals(100, tmSpy.getShifts());
  }

  @Test
  public void testRunIsNotCancelledAfter100Shifts(){
    System.out.println("testRunIsNotCancelledAfter100Shifts");
    ArrayList<State> states = new ArrayList<State>();
    states.add(new State(true, false, 2, true, true, 2));
    states.add(new State(true, true, 1, false, true, 3));
    states.add(new State(true, false, 0, true, true, 4));
    states.add(new State(true, false, 4, false, false, 1));
    TuringMachine tm = new TuringMachine(states);
    TuringMachine spyTM = spy(tm);
    spyTM.run();
    verify(spyTM, times(1)).isInNonHaltingCycle(any(State.class), any(TapeCell.class));
    verify(spyTM, atMost(1)).isMovingOneDirectionIndefinitely(anyInt()); //is actually never but best not to assuem
    assertEquals(107, spyTM.getShifts());
  }

}
