import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class TuringMachineTest {

  @Test
  public void TestOneStateBusyBeaver(){
    State state = new State(true, true, 0, true, true, 0);
    ArrayList<State> states = new ArrayList<State>();
    states.add(state);
    TuringMachine tm = new TuringMachine(states);
    assertEquals(1, tm.getScore());
    assertEquals(1, tm.getShifts());
  }

  //TODO: 2, 3, 4 state Busy Beavers

  //TODO: non-halting Busy Beaver(s)

}
