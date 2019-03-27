import java.util.ArrayList;

public class PopulationGenerator {
  public ArrayList<TuringMachine> turingMachines;
  private final int numStates;

  /*
  Constructor
  */

  public PopulationGenerator(int numStates, int populationSize){
    this.numStates = numStates;
    turingMachines = generate(numStates, populationSize);
  }


  /*
  method that generates an arraylist of random TuringMachines
  */

  public ArrayList<TuringMachine> generate(int numStates, int populationSize){
    turingMachines = new ArrayList<TuringMachine>();

    for(int i = 0; i < populationSize; i++){
      ArrayList<State> states = new ArrayList<State>();
      for(int j = 0; j < numStates; j++){
        boolean readZeroWriteOne = Math.random() >= 0.5;
        boolean leftZero = Math.random()>= 0.5;
        int nextStateZero = ((int)(100 * Math.random())) % (numStates + 1);
        boolean readOneWriteOne = Math.random() >= 0.5;
        boolean leftOne = Math.random()>= 0.5;
        int nextStateOne = ((int)(100 * Math.random())) % (numStates + 1);
        State state = new State(readZeroWriteOne, leftZero, nextStateZero, readOneWriteOne, leftOne, nextStateOne);
        states.add(state);
      }
      turingMachines.add(new TuringMachine(states));

    }

    return turingMachines;
  }

  /*
  One lonely accessor method
  */

  public ArrayList<TuringMachine> getPopulation(){
    return turingMachines;
  }

}
