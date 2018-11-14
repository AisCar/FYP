import java.util.ArrayList;
import java.util.BitSet;

public class GeneticAlgorithm {
  private double mutationRate, crossoverRate;
  private ArrayList<TuringMachine> population;
  private BusyBeaverPopulation pop;

  /*
    Constructors
   */

  public GeneticAlgorithm(int populationSize, int numStates){//keep? remove?
    pop = new BusyBeaverPopulation(numStates, populationSize);
    population = pop.getTuringMachines();
    crossoverRate = 0.5;
    mutationRate = 0.01;
    //run();
  }

  public GeneticAlgorithm(int populationSize, int numStates, double crossoverRate, double mutationRate){
    pop = new BusyBeaverPopulation(numStates, populationSize);
    population = pop.getTuringMachines();
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    //run();
  }

  /*
    main genetic algorithm method
  */

    public void run(){
      int numGenerations = 10; //To start with - TODO max num generations and solution convergence
      for(int i = 0; i < numGenerations; i++){
        for(TuringMachine busyBeaver : population){
          busyBeaver.run();
          //int score = busyBeaver.getScore();
          calculateFitness(busyBeaver);
        }
        nextGeneration(pop.getBitSets());//If this implementation, then will have to update in bbPop too...TODO
      }
      //TODO
  }

  /*
  population management methods
 */

  private void repair(ArrayList<BitSet> population){
    //TODO
    //next state num could be > num states - modify to fix
    //any other issues?
  }

  private void nextGeneration(ArrayList<BitSet> population){
    //TODO
    //Handles crossover, mutation, repair - then updates population 
  }

  /*
  genetic operators
   */

  private void calculateFitness(TuringMachine busyBeaver){
      int score = busyBeaver.getScore();
      //insert calculations here...
      //TODO
  }

  private void crossover(BitSet parent1, BitSet parent2){
      //TODO
  }

  private void mutate(BitSet parent){
      //TODO
      /*
      Note to self: java BitSet - flip(int bitIndex) - Sets the bit at the specified index to the complement of its current value.
       */
   }


  /*
  accessor/mutator methods
   */
  public void setMutationRate(float mutationRate){
    this.mutationRate = mutationRate;
  }

  public void setCrossoverRate(float crossoverRate){
    this.crossoverRate = crossoverRate;
  }

}
