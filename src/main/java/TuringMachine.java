import java.util.ArrayList;

public class TuringMachine implements Comparable<TuringMachine> {
  int score;
  int shifts;
  ArrayList<State> states;


  /*
  Constructors
  */

  public TuringMachine(ArrayList<State> states){
    shifts = 0;
    score = 0;
    this.states = states;
    //Validation should have already taken place somewhere
    //run(states);
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

    //For now: halt after 1million shifts (or reach halt condition)- may change later
    while(true && shifts < 1000000000){ //remember: max int = 2,147,483,647
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
        if(stateNum == 0) break; //check if halting
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
        if(stateNum == 0) break; //check if halting
        currentState = states.get(stateNum-1);
      }
    }//End while
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


  /*
  Override compareTo for sorting of Turing Machine Collections
  */

  @Override
  public int compareTo(TuringMachine other){
    //return this.score - other.score; //Wait, descending order makes more sense than ascending?
    //TODO decide
    return (other.score - this.score);
  }

}
