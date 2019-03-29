
public class TapeCell{
  private TapeCell left;
  private TapeCell right;
  private boolean isOne; //true if symbol on tape cell is 1, false if it is 0

  public TapeCell(){
    isOne = false;
    left = null;
    right = null;
  }

  public void write(boolean isOne){
    this.isOne = isOne;
  }

  public boolean read(){
    return isOne;
  }

  public TapeCell getLeft(){
    return this.left;
  }

  public TapeCell getRight(){
    return this.right;
  }

  public void setLeft(TapeCell newCell){
    this.left = newCell;
  }

  public void setRight(TapeCell newCell){
    this.right = newCell;
  }

}
