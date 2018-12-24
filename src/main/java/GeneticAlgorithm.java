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
    population = pop.getPopulation();
    crossoverRate = 0.5;
    mutationRate = 0.01;
    //run();
  }

  public GeneticAlgorithm(int populationSize, int numStates, double crossoverRate, double mutationRate){
    pop = new BusyBeaverPopulation(numStates, populationSize);
    population = pop.getPopulation();
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
          int fitness = calculateFitness(busyBeaver);//what was I doing here?
        }
        nextGeneration();
      }
      //TODO
  }


  /*
  Method to create next generation of Turing Machines using crossover, mutation
  */
  private void nextGeneration(){//ArrayList<TuringMachine> population){//ArrayList<boolean[]> population){
    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();
    //Sort population by fitness
    //Add Turing Machines unmodified to the next generation
    //Select Turing Machines for crossover
    //perform crossover
    //Select chromosomes for mutation
    //Perform mutation
    //(add each to new generation)
    //(Also validate and repair each)
    //Update the population in population manager (and in here)
    this.pop.setPopulation(nextGeneration);
    //start the next generation

  }

  /*
  genetic operators
   */

  private int calculateFitness(TuringMachine busyBeaver){
      int score = busyBeaver.getScore();
      //insert calculations here...
      //TODO
      /*
      Future considerations: Bringing S(n) into the fitness function instead of
      saying "Fitness = Sigma(n)"
      But for now, fitness = Busy Beaver Function Sigma(n)
      */
      return score;
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
   method to repair new Turing Machines that are invalid
  */

   private void repair(ArrayList<boolean[]> turingMachine){//population){
     //TODO
     //next state num could be > num states - modify to fix
     //any other issues?
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
