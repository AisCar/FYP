import java.util.concurrent.Callable;

public class TuringMachineRunTask implements Callable {
  private TuringMachine tm;
  private int numStates;

  public TuringMachineRunTask(TuringMachine tm){
    this.tm = tm;
    this.numStates = tm.getStates().size();
  }

  @Override
  public Object call() throws Exception {
    tm.run();
    this.tm = null; //just in case
    if(numStates > 5){
      //Encourage gc to collect all those TapeCells in tm
      Runtime.getRuntime().gc();
      Thread.sleep(5000); //sleep 5 sec - trying to let gc kick in - note 10 sec sleep at end of generation as well...
    }
    return null;
  }

  @Override
  public void finalize(){
    //this will create so much noise but it's worth it to see if it's working
    //TODO remove afterwards
    if(this.numStates >= 5){ //kinda redundant but w/e
      System.out.println("TuringMachineRunTask garbage collected");
    }
  }
}
