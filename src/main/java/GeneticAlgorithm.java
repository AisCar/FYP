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
  protected void nextGeneration(){
    //Need to implement other methods first, will look like:
    /*
    //Sort current population by fitness (descending)
    Collections.sort(population);

    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();

    //perform crossover and mutation
    nextGeneration.addAll(crossover());
    nextGeneration.addAll(mutation());

    //Perform elitist selection
    //TODO

    //Update the population in population manager
    this.pop.setPopulation(nextGeneration);
    population = nextGeneration;
    */
  }

  /*
  genetic operators
   */

  protected int calculateFitness(TuringMachine busyBeaver){
      int score = busyBeaver.getScore();
      /*
      Future considerations: Bringing S(n) into the fitness function instead of
      saying "Fitness = Sigma(n)"
      But for now, fitness = Busy Beaver Function Sigma(n)
      */
      return score;
  }

  protected ArrayList<TuringMachine> crossover(){
    //TODO come back to this once select is implemented
    ArrayList<TuringMachine> machines = new ArrayList<TuringMachine>();
    int numParents = (int)(population.size()*crossoverRate);
    if(numParents % 2 != 0) { //need an even number of parents
      numParents++;
    }

    ArrayList<TuringMachine> temp = new ArrayList<TuringMachine>();
    temp.addAll(select(numParents));//Note: Not yet implemented
    ArrayList<boolean[]> temp2 = new ArrayList<boolean[]>();

    for(int i = 0; i < numParents; i += 2){
      boolean[] parent1 = pop.toBitArray(temp.get(i));
      boolean[] parent2 = pop.toBitArray(temp.get(i+1));
      //temp2.addAll
      boolean[][] children = crossoverSingle(parent1,parent2);
      temp2.add(children[0]);
      temp2.add(children[1]);
    }

    return machines;
  }

  protected boolean[][] crossoverSingle(boolean[] parent1, boolean[] parent2){
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

    //TODO: Validation and repairs

    boolean[][] children = {child1, child2};
    return children;
  }

  protected ArrayList<TuringMachine> mutation(){
    //TODO come back to this once select is implemented
    ArrayList<TuringMachine> machines = new ArrayList<TuringMachine>();
    //Select TuringMachines from current population using mutationRate
    int numToMutate = (int) (population.size() * mutationRate);
    if(numToMutate != 0){
      ArrayList<TuringMachine> temp = select(numToMutate);//TODO implement select
      //Translate them to bit arrays
      ArrayList<boolean[]> bitMachines = new ArrayList<boolean[]>();
      //mutate the bit arrays
      for(boolean[] bitArray : bitMachines){
        boolean[] mutatedBitArray = mutateSingle(bitArray);
        //TODO validation - here or there?
        TuringMachine mutatedTuringMachine = pop.toTuringMachine(mutatedBitArray);
        machines.add(mutatedTuringMachine);
      }
    }
    return machines;
  }

  protected boolean[] mutateSingle(boolean[] parent){
      int geneToMutate = (int) (Math.random() * (parent.length - 1));
      boolean[] child  = new boolean[parent.length];
      for(int i = 0; i < parent.length; i++){
        child[i] = parent[i];
      }
      child[geneToMutate] = !parent[geneToMutate];
      return child;
   }

   public ArrayList<TuringMachine> select(int numToSelect){
     ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>();
     //TODO

     return selected;
   }


   /*
   method to repair new Turing Machines that are invalid
  */

   protected void repair(ArrayList<boolean[]> turingMachine){//population){
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
