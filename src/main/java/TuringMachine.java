import java.util.ArrayList;

public class TuringMachine implements Comparable<TuringMachine> {
  int score;
  int shifts;
  ArrayList<State> states;
  boolean notHalting;
  boolean hasRun;

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
        //System.out.println(stateNum-1);
        currentState = states.get(stateNum-1);
      }
    }//End while

    if(notHalting){
      score = -1;
    }
    //System.out.println("Score: " + score + " Shifts: " + shifts);
  }


  /*
  other methods
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


  public int getScore(){
    return score;
  }

  public int getShifts(){
    return shifts;
  }

  public ArrayList<State> getStates(){
    return states;
  }

  public boolean previouslyRun(){
    return hasRun;
  }

  //Note: do not use this unless wasAlreadyRun returns true
  public boolean isHalting(){
    return !notHalting;
  }


  /*
  Override compareTo for sorting of Turing Machine Collections
  */

  @Override
  public int compareTo(TuringMachine other){
    //return this.score - other.score; //Wait, descending order makes more sense than ascending?
    //TODO decide
    return (other.calculateFitness() - this.calculateFitness());
  }

  protected int calculateFitness(){
    //TODO
    int fitness = this.score;

    //Fitness Part 1: Is halting
    // -> Has one halt state (TODO numHalts is now set in areStatesReachable code)
    // -> Does halt (score != -1) (if has run, obvi)

    //Fitness Part 2: All states are reachable
    areStatesReachable(1);
    //TODO use the boolean array stateReachable to calculate fitness



    //Fitness Part 3: TODO Michael's suggestion from email - started on another branch

    return fitness;

  }


  protected void areStatesReachable(int stateNum){
    int index = stateNum - 1;
    System.out.println(index);
    if(index == -1){
      numHalts++;
    }
    else if(!stateReachable[index]) { //BUG: Getting index = n instead of n-1 TODO fix
      stateReachable[index] = true;
      State current = states.get(index);
      areStatesReachable(current.getNextState(false));
      areStatesReachable(current.getNextState(true));
    }

    /*
    (Running 6-state TMs)
    ...
    0
0
0
0
0
0
0
0
2
4
3
2
3
0
2
-1
0
4
2
6
Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: 6
        at TuringMachine.areStatesReachable(TuringMachine.java:181)
        at TuringMachine.areStatesReachable(TuringMachine.java:184)
        at TuringMachine.areStatesReachable(TuringMachine.java:184)
        at TuringMachine.areStatesReachable(TuringMachine.java:184)
        at TuringMachine.calculateFitness(TuringMachine.java:163)
        at TuringMachine.compareTo(TuringMachine.java:151)

    Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: 6
        at TuringMachine.areStatesReachable(TuringMachine.java:205)
        at TuringMachine.areStatesReachable(TuringMachine.java:209)
        at TuringMachine.areStatesReachable(TuringMachine.java:208)
        at TuringMachine.calculateFitness(TuringMachine.java:166)
        at TuringMachine.compareTo(TuringMachine.java:154)
        at TuringMachine.compareTo(TuringMachine.java:3)
        at java.util.ComparableTimSort.binarySort(ComparableTimSort.java:262)

        see also:

    Exception in thread "AWT-EventQueue-0" java.lang.IndexOutOfBoundsException: Index: 6, Size: 6 (6 state tm - was 5 for 5 state)
        at java.util.ArrayList.rangeCheck(ArrayList.java:653)
        at java.util.ArrayList.get(ArrayList.java:429)
        at TuringMachine.run(TuringMachine.java:90)
        at GeneticAlgorithm.run(GeneticAlgorithm.java:79)
        at UserInterface.runGeneticAlgorithm(UserInterface.java:279)
     */
  }
}
