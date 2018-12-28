import java.util.ArrayList;
import java.util.Collections;

public class GeneticAlgorithm {
  private double mutationRate, crossoverRate;
  private ArrayList<TuringMachine> population;
  private PopulationGenerator pop;
  private Translator translator;


  /*
    Constructors
   */

   protected GeneticAlgorithm(){
     //Constructor for tests only - will update later TODO
   }

  public GeneticAlgorithm(int populationSize, int numStates){//keep? remove?
    pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
    crossoverRate = 0.5;
    mutationRate = 0.01;
    //run();
  }

  public GeneticAlgorithm(int populationSize, int numStates, double crossoverRate, double mutationRate){
    pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
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
        for(TuringMachine busyBeaver : population){//TODO don't rerun TuringMachines carried over and already run
          busyBeaver.run();
        }
        nextGeneration();
      }
      //TODO - proper convergence
  }


  /*
  Method to create next generation of Turing Machines using crossover, mutation
  */
  protected void nextGeneration(){
    //Sort current population by fitness (descending)
    Collections.sort(population);

    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();

    //perform crossover and mutation
    nextGeneration.addAll(crossover());
    nextGeneration.addAll(mutation());

    //Perform elitist selection
    double elitismRate = 0.2;
    int numElite = (int) (population.size() * elitismRate);
    nextGeneration.addAll(population.subList(0, numElite));

    //Update the population
    population = nextGeneration;

  }

  /*
  genetic operators
   */

  protected int calculateFitness(TuringMachine busyBeaver){
      int score = busyBeaver.getScore();
      //Could make this more complicated but for now is fine
      return score;
  }


  protected ArrayList<TuringMachine> crossover(){
    ArrayList<TuringMachine> machines = new ArrayList<TuringMachine>();

    //Select parent TuringMachines
    ArrayList<TuringMachine> parents = new ArrayList<TuringMachine>();
    int numParents = (int)(population.size()*crossoverRate);
    if(numParents % 2 != 0) { //need an even number of parents
      numParents++;
    }
    parents.addAll(select(numParents));

    for(int i = 0; i < numParents; i += 2){
      //Encode pairs of TuringMachines as binary chromosomes
      boolean[] parent1 = this.translator.toBitArray(parents.get(i));
      boolean[] parent2 = this.translator.toBitArray(parents.get(i+1));

      //Perform crossover on the pair, get 2 child chromosomes
      boolean[][] children = crossoverSingle(parent1,parent2);

      //Decode new chromosomes and add to ArrayList
      machines.add(this.translator.toTuringMachine(children[0]));
      machines.add(this.translator.toTuringMachine(children[1]));
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

    if(!isValidChromosome(child1)){
      child1 = repair(child1);
    }
    if(!isValidChromosome(child2)){
      child2 = repair(child2);
    }

    boolean[][] children = {child1, child2};
    return children;
  }


  protected ArrayList<TuringMachine> mutation(){
    ArrayList<TuringMachine> machines = new ArrayList<TuringMachine>();
    int numToMutate = (int) (population.size() * mutationRate);

    if(numToMutate != 0){
      //select TuringMachines from the population to mutate
      ArrayList<TuringMachine> selectedForMutation = select(numToMutate);
      for(TuringMachine tm : selectedForMutation){
        //encode, mutate and decode each selected TuringMachine
        boolean[] encodedChromosome = translator.toBitArray(tm);
        boolean[] mutatedBitArray = mutateSingle(encodedChromosome);
        TuringMachine mutatedTuringMachine = translator.toTuringMachine(mutatedBitArray);

        //add mutated result to list of TuringMachines that the method returns
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
      if(!isValidChromosome(child)){
        child = repair(child);
      }
      return child;
   }


   public ArrayList<TuringMachine> select(int numToSelect){
     /*
     Create an array of cumulative selection probability upperbounds where the
     first element is k times more likely than the last to be chosen
     Each interval has size (j/denominator) for j descending in range (1, k)
     */
     int k = population.size();
     int denominator = 0;
     for(int i = k; i > 0; i--){
       denominator += i;
     }

     double[] selectionProbs = new double[population.size()];

     if(denominator != 0){
       double cumulativeProbability = 0.0;
       for(int i = 0; i < population.size(); i++){
         double interval = ((float) k) / denominator;
         cumulativeProbability += interval;
         selectionProbs[i] = cumulativeProbability;
         k--;
       }
     }

     //finally select numToSelect TuringMachines and return them in an ArrayList
     ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>();
     double rand;
     while(selected.size() < numToSelect){
       rand = Math.random();
       //find the TuringMachine corresponding to rand
       for(int i = 0; i < selectionProbs.length; i++){
         if(rand <= selectionProbs[i]){
           //Add the corresponding TM to the arraylist
           selected.add(population.get(i));
           break; //out of for loop, not while
         }
       }
     }
     return selected;
   }


   /*
   method to repair new Turing Machines that are invalid
  */

   protected boolean[] repair(boolean[] chromosome){
     //TODO
     //no halt condition -> introduce one somewhere (Where??)
     //next state num could be > num states - modify to fix
     //any other issues?
     return chromosome; //to prevent compilation errors
   }

   protected boolean isValidChromosome(boolean[] chromosome){
     //TODO
     /*
     Potential invalid chromosomes (that we can detect):
     - have no halt condition
     - nextState > numStates
     - ???
     */
     return true; //to prevent compilation errors
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

  public ArrayList<TuringMachine> getPopulation(){//same name as method in PopulationGenerator ... should rename?
    return this.population;
  }

}
