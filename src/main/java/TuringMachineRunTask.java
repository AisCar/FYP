import java.util.concurrent.Callable;

public class TuringMachineRunTask implements Callable {
  private TuringMachine tm;

  public TuringMachineRunTask(TuringMachine tm){
    this.tm = tm;
  }

  @Override
  public Object call() throws Exception {
    tm.run();
    if(tm.getStates().size() > 5){
      this.tm = null; //just in case
      //Encourage gc to collect all those TapeCells in tm
      Runtime.getRuntime().gc();
      Thread.sleep(5000); //sleep 5 sec - trying to let gc kick in - note 10 sec sleep at end of generation as well...
    }
    else{
      this.tm = null; //just in case
    }
    return null;
  }
}
