import java.util.concurrent.Callable;

public class TuringMachineRunTask implements Callable {
  private TuringMachine tm;

  public TuringMachineRunTask(TuringMachine tm){
    this.tm = tm;
  }

  @Override
  public Object call() throws Exception {
    tm.run();
    return null;
  }
}
