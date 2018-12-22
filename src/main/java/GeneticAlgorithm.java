import java.util.ArrayList;
import java.util.BitSet;

public class GeneticAlgorithm {
  private double mutationRate, crossoverRate;
  private ArrayList<TuringMachine> population;
  private BusyBeaverPopulation pop;

  /*
    Constructors
   */

   protected GeneticAlgorithm(){
     //Constructor for tests only - will update later TODO
   }

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

  private void repair(ArrayList<boolean[]> population){
    //TODO
    //next state num could be > num states - modify to fix
    //any other issues?
  }

  private void nextGeneration(ArrayList<boolean[]> population){
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

  private void crossover(boolean[] parent1, boolean[] parent2){
    if(parent1.length != parent2.length){
      //throw new Exception("Parent chromosomes have different lengths");
      //todo error handling
    }
    /*
    To consider: Crossover at random points? Crossover only at state boundaries?
    */
    int crossoverPoint = (int) (Math.random() * (parent1.length - 1));
    boolean[] child1 = new boolean[parent1.length];
    boolean[] child2 = new boolean[parent1.length];
    for(int i = 0; i < crossoverPoint; i++){
      child1[i] = parent1[i];
      child2[i] = parent2[i];
    }
    for(int i = crossoverPoint; i < parent1.length; i++){
      child1[i] = parent2[i];
      child2[i] = parent1[i];
    }
    //wait what now? Am I overwriting parents or keeping them?
    //If keeping them, then how do I add these children to the population?
    //TODO come back to this once you make decisions about generation management
  }

  protected boolean[] mutate(boolean[] parent){
      int geneToMutate = (int) (Math.random() * (parent.length - 1));
      boolean[] child  = new boolean[parent.length];
      for(int i = 0; i < parent.length; i++){
        child[i] = parent[i];
      }
      child[geneToMutate] = !parent[geneToMutate];
      return child;
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
