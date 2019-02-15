import java.util.ArrayList;

public class TuringMachine implements Comparable<TuringMachine> {
  int score;
  int shifts;
  ArrayList<State> states;
  boolean notHalting;
  boolean hasRun;
  int fitness = -2000000; //int? can change

  //variables for new feature TODO comment
  boolean stateReachable[];
  int numHalts;


  /*
  Constructors
  */

  public TuringMachine(ArrayList<State> states){
    shifts = 0;
    score = 0;
    this.states = states;
    hasRun = false;
    numHalts = 0;

    stateReachable = new boolean[states.size()];
    for(int i = 0; i < stateReachable.length; i++) {
      stateReachable[i] = false;
    }
  }

  public TuringMachine(){
    //TODO: Input is bitstream - translate to arraylist
    //then call above constructor which runs the tm
    //may not need this depending on later implementation
  }


  /*
  main method
  */

  public void run(){
    TapeCell currentCell = new TapeCell();
    State currentState = states.get(0);
    int stateNum = 1;
    shifts = 0;//redundant unless we decide to run twice
    score = 0;
    notHalting = true;
    hasRun = true;

    //For now: halt after 1million shifts (or reach halt condition)- may change later
    while(notHalting && shifts < 1000){//1000000000){ //remember: max int = 2,147,483,647 //TODO uncomment once tests run
      //If read one from tape
      if(currentCell.readOne()){
        //write
        currentCell.writeOne(currentState.getWrite(true));
        //adjust score accordingly
        if(!currentState.getWrite(true)) score--; //Writing a zero decreases score
        //move
        currentCell = currentState.getMove(true) ? moveLeft(currentCell) : moveRight(currentCell);
        shifts++;
        //next state
        stateNum = currentState.getNextState(true);
        if(stateNum == 0) { //check if halting
          notHalting = false;
          break;
        }
        currentState = states.get(stateNum-1);
      }
      //If read zero from tape
      else{//Read a zero
        //write
        currentCell.writeOne(currentState.getWrite(false));
        //adjust score accordingly
        if(currentState.getWrite(false)) score++;
        //move
        currentCell = currentState.getMove(false) ? moveLeft(currentCell) : moveRight(currentCell);
        shifts++;
        //next state
        stateNum = currentState.getNextState(false);
        if(stateNum == 0) { //check if halting
          notHalting = false;
          break;
        }
        currentState = states.get(stateNum-1);
      }
    }//End while

    if(notHalting){
      score = -1;
    }
    //System.out.println("Score: " + score + " Shifts: " + shifts);
  }


  /*
  Turing machine head movement methods
  */

  private TapeCell moveLeft(TapeCell cell){
    if(cell.getLeft() == null){
      cell.setLeft(new TapeCell());
      cell.getLeft().setRight(cell); //previously cell.left.right = cell;
    }
    cell = cell.getLeft();
    return cell;
  }

  private TapeCell moveRight(TapeCell cell){
    if(cell.getRight() == null){
      cell.setRight(new TapeCell());
      cell.getRight().setLeft(cell); //cell.right.left = cell;
    }
    cell = cell.getRight();
    return cell;
  }

  /*
  Method to calculate fitness score
  (used in genetic algorithm and in sorting TuringMachines)
  */

  protected int calculateFitness(){
    //TODO
    int fitness = this.score; //Now have a class variable called fitness as well, so yeah don't forget when writin the code

    //Fitness Part 1: Is halting
    // -> Has one halt state (TODO numHalts is now set in areStatesReachable code)
    // -> Does halt (score != -1) (if has run, obvi)

    //Fitness Part 2: All states are reachable
    areStatesReachable(1);
    //TODO use the boolean array stateReachable to calculate fitness



    //Fitness Part 3: TODO Michael's suggestion from email - started on another branch

    return fitness;

  }

  //Helper method for fitness method
  protected void areStatesReachable(int stateNum) {
    if (stateNum == 0) {
      numHalts++;
    }
    else if (!stateReachable[stateNum - 1]) {
      stateReachable[stateNum - 1] = true;
      State current = states.get(stateNum - 1);
      areStatesReachable(current.getNextState(false));
      areStatesReachable(current.getNextState(true));
    }
  }


  /*
  Overriden methods
  */

  @Override
  public int compareTo(TuringMachine other){
    return (other.getFitness() - this.getFitness());
  }

  @Override
  public String toString(){
    String str = "State\tRead\tWrite\tMove\tNext State\n";
    int currState = 1;
    for(State s : this.states){
      str = str + currState + "\t0" + (s.getWrite(false)? "\t1" : "\t0") + (s.getMove(false)? "\tleft\t" : "\tright\t")
              + s.getNextState(false) + "\n" + currState + "\t1" + (s.getWrite(true)? "\t1" : "\t0")
              + (s.getMove(true)? "\tleft\t" : "\tright\t") + s.getNextState(true) + "\n";
      currState++;
    }
    return str;
  }


  /*
  Getters and setters
  */

  public int getScore(){
    return score;
  }

  public int getShifts(){
    return shifts;
  }

  public ArrayList<State> getStates(){
    return states;
  }

  public int getFitness(){
    if(fitness == -2000000){ //default for uninitialised int is zero, but fitness could actually be zero
      fitness = this.calculateFitness();
    }
    return fitness;
  }

  public boolean previouslyRun(){
    return hasRun;
  }

  //Note: do not use this unless wasAlreadyRun returns true
  public boolean isHalting(){
    return !notHalting;
  }

}
