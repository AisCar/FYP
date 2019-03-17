import java.util.ArrayList;

public class TuringMachine implements Comparable<TuringMachine> {
  //general Turing machine variables
  int score;
  int shifts;
  ArrayList<State> states;

  //fitness variables
  protected int fitness = -2000000;
  protected boolean stateReachable[];
  protected int numHalts;
  protected int statesNotUsedCounter;

  //other variables
  boolean notHalting;
  boolean hasRun;

  //feature toggles
  private boolean reachableFitnessFeature;
  private boolean numHaltsFitnessFeature;
  private boolean stateUsageFitnessFeature;


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
    statesNotUsedCounter = 0;

    /*
    Note: Current highest known 5-state busy beaver score is 4098, with
    shift score S(5) = 47,168,870
    So don't quit before 50,000,000 shifts

    Note: Highest known S(n) for n >= 6 is much much larger than the maximum
    value of int in java (2,147,483,647) but finding the best 6+ state busy
    beaver probably isn't manageable in this project.
    */
    int maxShifts = 1000; //for n <= 4
    if(states.size() == 5){
      maxShifts = 5000000;
    }
    else if(states.size() > 5){
      maxShifts = 2000000000; //max int value, known values of S(6) are actually much higher!

    }
    //else if(states.size() > 5){maxShifts = 2000000000;} //This will take forever... maybe dont

    if(this.haltReachable()){ //Don't run if it absolutely cannot halt - just a waste of time (be very very sure that areStatesReachable is working perfectly TODO)
      while(notHalting && shifts < maxShifts){
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
          currentState.iterationLastUsed = shifts;
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
          currentState.iterationLastUsed = shifts;
          currentState = states.get(stateNum-1);
        }

        //If any state has not been used in 100*n iterations, increment statesNotUsedCounter
        if(shifts % (100*states.size()) == 0){
          for(State s : states){
            if(shifts - s.iterationLastUsed > 100*states.size()){
              statesNotUsedCounter++; //this is used to decrease fitness later on
            }
          }
        }

        //Check if Turing machine is stuck in a loop every so often
        if(shifts % 100 == 0){
          if(isInNonHaltingCycle(currentState, currentCell)){
            break;
          }
        }

      }//End while

      if(states.size() > 5){
        currentCell = null;
        Runtime.getRuntime().gc(); //Trying to reduce OOM errors for n = 6
      }

    }

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
  (used in genetic algorithm and for sorting TuringMachines)
  */

  protected int calculateFitness(){
    //Fitness Part 1: Busy Beaver score - TODO is multiplying score by 3 better or worse?
    int fitness = this.score * 3;

    //Fitness Part 2: All states are reachable from the initial state
    if(reachableFitnessFeature){
      //areStatesReachable(1); //now called inside run() method
      for(int i = 0; i < stateReachable.length; i++){
        if(stateReachable[i]){
          fitness = fitness + 2;
        }
        else{
          fitness = fitness - 5;
        }
      }

    }

    //Fitness Part 3: Is halting
    this.countHalts();
    if(numHaltsFitnessFeature){
      if(numHalts == 0){ //note: numHalts set in areStatesReachable
        //punish TM with no halt conditions
        fitness -= 10;
      }
      else if(numHalts == 1){
        //reward TM with exactly one halt condition
        fitness += 5;
      }
      else{
        //punish TM with multiple halt conditions
        fitness -= 3;
      }
    }

    //Fitness Part 4: Only uses a subset of its states for several iterations
    if(stateUsageFitnessFeature){
      fitness -= statesNotUsedCounter;
    }

    return fitness;

  }

  //Helper method for fitness method
  protected void areStatesReachable(int stateNum) {
    if(stateNum != 0 && (!stateReachable[stateNum - 1])){
      stateReachable[stateNum - 1] = true;
      State current = states.get(stateNum - 1);
      areStatesReachable(current.getNextState(false));
      areStatesReachable(current.getNextState(true));
    }
    //else if (stateReachable[stateNum - 1]) then state has already been assessed
  }

  //Helper method for fitness method
  protected void countHalts(){
      numHalts = 0;
      for(State state : states){
          if(state.getNextState(false) == 0){
              numHalts++;
          }
          if(state.getNextState(true) == 0){
              numHalts++;
          }
      }
  }



  /*
  Methods to (attempt to) determine whether or not the Turing machine will halt
  */

  //Determines if a halt condition exists inside a state that is reachable from the initial state
  protected boolean haltReachable(){
    boolean haltReachable = false;
    this.areStatesReachable(1);
    for(int i = 0; i < states.size(); i++){
      if(states.get(i).getNextState(false) == 0 || states.get(i).getNextState(true) == 0){
        if(stateReachable[i]){
          haltReachable = true;
        }
      }
    }
    return haltReachable;
  }


  //Tries to determine if Turing machine is stuck in a loop over uninitialised tape (reading only zeroes)
  protected boolean isInNonHaltingCycle(State current, TapeCell cell){
    //Obviously if about to halt, return false (prevents potential AL.get(-1) issue too)
    int next = current.getNextState(cell.readOne());
    if(next == 0) {
      return false;
    }

    /* If next cell == null, then all cells in that direction contain zeroes,
    which makes it easier to identify (non halting) cyclic behaviour */
    TapeCell nextCell = (current.getMove(cell.readOne())? cell.getLeft() : cell.getRight());
    if(nextCell == null){
      int stateNum = states.indexOf(current) + 1; //recall +1 because 0 reserved for halt condition
      if(isMovingOneDirectionIndefinitely(stateNum)){
        return true;
      }
      else if(isMovingMostlyOneDirectionIndefinitely(stateNum)){
        return true;
      }
    }

    return false; //Return false if failed to identify a non-halting cycle
  }


  //Determines if a TuringMachine will continue in one direction indefinitely (over tape which contains zeroes only i.e. TapeCells uninitialised)
  protected boolean isMovingOneDirectionIndefinitely(int initialStateNum){
    int current = initialStateNum;
    for(int i = 0; i <= states.size(); i++){
      //Check for halt conditions
      if(current == 0){
        return false;
      }
      int next = states.get(current-1).getNextState(false); //false because reading zeroes
      if(next == 0){
        return false;
      }

      //Check if Turing machine head will change direction
      if(states.get(current-1).getMove(false) != states.get(next-1).getMove(false)){ //falses because reading zeroes
        return false;
      }
      current = next;
    }
    /* If reading only zeroes and not changing direction for >= n shifts,
    then Turing machine is stuck moving in that direction indefinitely */
    return true;
  }

  //Like method above, but direction can change so long as no ones are written and end up further in same direction
  protected boolean isMovingMostlyOneDirectionIndefinitely(int initialStateNum){ //TODO rename
    int lefts = 0;
    int rights = 0;

    if(initialStateNum == 0){
      return false; //will halt straight away!
    }

    boolean movingLeft = states.get(initialStateNum - 1).getMove(false); //True if moving left, false if moving rigth

    int current = initialStateNum;
    for(int i = 0; i <= states.size(); i++){
      if(current == 0){//Check for halt condition
        return false;
      }

      //If current state writes a 1 to tape, then behaviour is too hard to predict
      if(states.get(current-1).getWrite(false)){
        return false;
      }

      if(states.get(current-1).getMove(false)){ //if tape head moves left
        lefts++;
      }
      else{ //if tape head moves right
        rights++;
      }

      int next = states.get(current-1).getNextState(false); //false because reading zeroes
      if(next == 0){//Check for halt condition
        return false;
      }

      current = next;
    }

    /* If no ones written and tape head is further in same direction (where all
    cells are uninitialised so there are no ones written to tape), then the
    tape head will continue to move in that direction forever, without writing
    any ones or halting */
    if(movingLeft){
      if(lefts >= rights){
        return true;
      }
    }
    else{//moving right
      if(rights >= lefts){
        return true;
      }
    }

    return false; //Unsure whether or not Turing machine will halt
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

  public void setReachableFitnessFeature(boolean reachableFitnessFeature){
    //sets feature toggle that enables/disables state reachability code in calculateFitness
    this.reachableFitnessFeature = reachableFitnessFeature;

  }

  public void setNumHaltsFitnessFeature(boolean numHaltsFitnessFeature){
    //sets feature toggle that enables/disables number of halt conditions code in calculateFitness
    this.numHaltsFitnessFeature = numHaltsFitnessFeature;

  }

  public void setStateUsageFitnessFeature(boolean stateUsageFitnessFeature){
    //sets feature toggle that enables/disables state usage code in calculateFitness
    this.stateUsageFitnessFeature = stateUsageFitnessFeature;

  }

}
