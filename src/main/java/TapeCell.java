
public class TapeCell{
  private TapeCell left;
  private TapeCell right;
  //private int value;
  private boolean isOne; //true if value is 1, false if value is 0

  public TapeCell(){
    //value = 0;
    isOne = false;
    left = null;
    right = null;
  }

  public void writeOne(boolean isOne){
    this.isOne = isOne;
  }

  public boolean readOne(){
    return isOne;
  }
/*
  public void write(int value){
    if(value!= 0 || value!=1){
      //throw new Exception("Attempting to write non binary value");
    }
    this.value = value;
  }

  public int read(){
    return this.value;
  }
*/

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

  @Override
  public void finalize(){ //For memory management debugging
    //System.out.println("TapeCell garbage collected!");
  }


}
