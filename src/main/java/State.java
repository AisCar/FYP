public class State {
  private boolean[][] instructions = new boolean[2][2];
  private int oneNextState, zeroNextState;

  public State(boolean readZeroWriteOne, boolean readZeroMoveLeft, int zeroNextState,
  boolean readOneWriteOne, boolean readOneMoveLeft, int oneNextState){
    //instructions[x][0] are what to write (true = 1, false = 0)
    instructions[0][0] = readZeroWriteOne;
    instructions[1][0] = readOneWriteOne;
    //instructions[x][1] are which direction to move (true = left, false = right)
    instructions[0][1] = readZeroMoveLeft;
    instructions[1][1] = readOneMoveLeft;
    //next state is only instruction that can have more than two values, so use ints.
    this.zeroNextState = zeroNextState;
    this.oneNextState = oneNextState;
  }

  //true = write one, false = write zero
  public boolean getWrite(boolean readOne){
    if(readOne){
      return instructions[1][0];
    }
    else{
      return instructions[0][0];
    }
  }

  //true = move left, false = move right
  public boolean getMove(boolean readOne){
    if(readOne){
      return instructions[1][1];
    }
    else{
      return instructions[0][1];
    }
  }

  public int getNextState(boolean readOne){
    if(readOne){
      return oneNextState;
    }
    else{
      return zeroNextState;
    }
  }



}
