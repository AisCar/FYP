import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

public class GeneticAlgorithm {
  //class variables
  protected double mutationRate, crossoverRate, elitismRate;
  protected ArrayList<TuringMachine> population;
  private Translator translator;
  private int numStates;
  private int currGeneration;
  private int maxGenerations = 10000;
  private boolean stopRunning = false;
  private boolean isRunning;
  private int highestScore;

  //increase mutation variables
  private boolean increaseMutation;
  protected double currentMutationRate;

  //feature toggles
  private boolean reachableFitnessFeature;
  private boolean numHaltsFitnessFeature;
  private boolean stateUsageFitnessFeature;
  private boolean multithreading;


  /*
    Constructors
   */

  public GeneticAlgorithm(int numStates, ArrayList<TuringMachine> testPopulation){
    //IMPORTANT: Constructor for testing purposes only
    this.numStates = numStates;
    population = testPopulation;
    translator = new Translator(numStates);
    crossoverRate = 0.6;
    mutationRate = 0.1;
    elitismRate = 0.0;
    currentMutationRate = mutationRate;
    this.numStates = numStates;
    currGeneration = 0;
  }


  public GeneticAlgorithm(int populationSize, int numStates, int maxGenerations, double crossoverRate, double mutationRate, double elitismRate) throws GeneticAlgorithmException {
    this.numStates = numStates;
    PopulationGenerator pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    this.elitismRate = elitismRate;
    currentMutationRate = mutationRate;
    currGeneration = 0;
    this.maxGenerations = maxGenerations;
    if(elitismRate + crossoverRate > 1.0 || elitismRate + mutationRate > 1.0){
      throw new GeneticAlgorithmException("Elitism/Crossover/Mutation rate too high");
    }
  }


  /*
    main genetic algorithm method
  */
    public void run(){
      currGeneration = 0; //counter for number of generations that have run
      int score = 0; //highest busy beaver score in this run of the genetic algorithm
      TuringMachine currBestTM; //best Turing machine in current generation
      this.isRunning = true;

      //increase mutation variables
      this.currentMutationRate = this.mutationRate;
      int increaseGen = 0;

      if(elitismRate == 1.0){
        maxGenerations = 1;
      }

      while((currGeneration < maxGenerations) && !stopRunning){ //max int value = 2147483647
        //Provide user feedback
        if(numStates > 4 && (currGeneration < 100 || currGeneration % 100 == 0)){//give an indication of how fast ga is running for n > 4
          System.out.println("Running generation " + currGeneration + "...");
        }
        else if(currGeneration % 1000 == 0){ //print less frequently if n <= 4 because these don't run so slowly
          System.out.println("Running generation " + currGeneration + "...");
        }

        //Run all Turing machines in population
        if(multithreading){
          //Create Tasks for all Turing machines in the population (that haven't been run already in a previous generation)
          ForkJoinPool fjp = new ForkJoinPool();
          ArrayList<Callable<TuringMachineRunTask>> tmTasks = new ArrayList<Callable<TuringMachineRunTask>>();
          for(TuringMachine turingMachine : population){
            if(!turingMachine.previouslyRun()){
              turingMachine.setNumHaltsFitnessFeature(this.numHaltsFitnessFeature);
              turingMachine.setReachableFitnessFeature(this.reachableFitnessFeature);
              turingMachine.setStateUsageFitnessFeature(this.stateUsageFitnessFeature);
              tmTasks.add(new TuringMachineRunTask(turingMachine));
            }
          }

          //Then run all of those tasks (which simply call TuringMachine's run method)
          try{
            fjp.invokeAll(tmTasks);
          }
          catch(RejectedExecutionException ree){
            System.out.println("Warning, InterruptedException occurred: " + ree.getMessage() + "\n(Now running remaining Turing machines on single thread for the rest of this generation)");
            fjp.shutdown(); //finish existing Tasks and shutdown executor
            for(TuringMachine turingMachine : population) { //Then run whatever it didn't get around to on single thread
              if (!turingMachine.previouslyRun()) {
                turingMachine.run();
              }
            }
          }
          tmTasks = null; //Can't be garbage collected in this generation if not yet dereferenced
          fjp = null;
        }
        else{ //not multithreading
          for(TuringMachine turingMachine : population){
            if(!turingMachine.previouslyRun()){
              turingMachine.setNumHaltsFitnessFeature(this.numHaltsFitnessFeature);
              turingMachine.setReachableFitnessFeature(this.reachableFitnessFeature);
              turingMachine.setStateUsageFitnessFeature(this.stateUsageFitnessFeature);
              turingMachine.run();
            }
          }
        }

        //Sort population (descending by fitness)
        Collections.sort(population);

        //Check if score has increased
        currBestTM = this.getHighestScoringTM();
        int currBestScore = currBestTM.getScore();
        if(currBestScore > score){
          //update variables
          score = currBestScore;
          increaseGen = currGeneration;
          //update variables for the increase mutation feature
          currentMutationRate = mutationRate;
          //Print out details of new highest scoring tm
          System.out.println("New high score achieved in generation " + currGeneration);
          System.out.println("Score = " + score);
          System.out.println("(Fitness: " + currBestTM.getFitness() + ")");
          System.out.println(currBestTM.toString());

        }

        //Optional feature: If score hasn't increased in many generations, increase the mutation rate
        int generationsSinceChange = currGeneration - increaseGen;
        if(increaseMutation && (generationsSinceChange > 1) && (generationsSinceChange % 100 == 0)){
          currentMutationRate = currentMutationRate + 0.05;
          if(currentMutationRate + elitismRate > 1.0){
            currentMutationRate = 1.0 - elitismRate;
          }
          System.out.println("Mutation rate is now " + currentMutationRate + "%");
        }

        if(numStates >= 4 && currGeneration % 200 == 0 && currGeneration > 0){
          //Print out a summary.
          System.out.println("Generation " + currGeneration + " Summary\nCurrent highest scoring tm: ");
          currBestTM = this.getHighestScoringTM();
          System.out.println(currBestTM.toString());
          System.out.println("Score: " + currBestTM.getScore() + "\tFitness: " + currBestTM.getFitness());
        }



        //If max score found, exit
        if((numStates == 4 && score == 13)){
          break;
        }
        //If running another generation, create that population
        else if(currGeneration < maxGenerations-1){
          this.population = nextGeneration();
        }

        callGarbageCollector(currGeneration);

        currGeneration++;
      }

      this.highestScore = score;

      this.isRunning = false;

      //Final summary
      currBestTM = this.getHighestScoringTM();
      System.out.println("\n\nTuring machine with highest Busy Beaver score in final population:");
      System.out.println("Score: " + currBestTM.getScore());
      System.out.println(currBestTM.toString());
  }


  /*
  Method to create next generation of Turing Machines using crossover, mutation
  */
  protected ArrayList<TuringMachine> nextGeneration(){
    //Sort current population by fitness (descending)
    Collections.sort(population);

    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();

    int numElite = (int) (population.size() * elitismRate);

    //Perform selection
    nextGeneration = select(population.size() - numElite);

    //Perform crossover
    nextGeneration = crossover(nextGeneration);

    //Perform mutation
    nextGeneration = mutation(nextGeneration);

    //If elitismRate > 0, add fittest Turing machines to nextGeneration unmodified
    for(int i = 0; i < numElite; i++){
      nextGeneration.add(population.get(i));
    }

    //Return the new population
    return nextGeneration;

  }

  /*
  genetic operators
   */

   //Fitness function in TuringMachine

  protected ArrayList<TuringMachine> crossover(ArrayList<TuringMachine> machines){
    //Shuffle population
    Collections.shuffle(machines);
    int numToCrossOver = (int) (population.size() * crossoverRate);
    if(numToCrossOver > machines.size()){
      System.out.println("Warning: Attempted to cross over more chromosomes than were available for crossover");
      numToCrossOver = machines.size();//is this needed?
    }

    //Make sure to have an even number of parents
    if(numToCrossOver % 2 != 0){
      numToCrossOver--;
    }

    //Perform crossover on the first numToCrossOver parents in the population
    for(int i = 0; i < numToCrossOver; i+=2){
      //Encode pairs of TuringMachines as binary chromosomes
      boolean[] parent1 = this.translator.toBitArray(machines.get(i));
      boolean[] parent2 = this.translator.toBitArray(machines.get(i+1));

      //Perform crossover on the pair, get 2 child chromosomes
      boolean[][] children = crossoverSingle(parent1,parent2);

      //Decode new chromosomes and add replace parent turing machines with their children
      machines.set(i, this.translator.toTuringMachine(children[0]));
      machines.set((i+1), this.translator.toTuringMachine(children[1]));

    }

    //Return population containing crossed over chromosomes
    return machines;
  }


  protected boolean[][] crossoverSingle(boolean[] parent1, boolean[] parent2){
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

    child1 = repair(child1);
    child2 = repair(child2);

    boolean[][] children = {child1, child2};
    return children;
  }


  protected ArrayList<TuringMachine> mutation(ArrayList<TuringMachine> machines){
    Collections.shuffle(machines);
    int numToMutate = (int) (population.size() * currentMutationRate);

    if(numToMutate > machines.size()){
      //With increaseMutation enabled, numToMutate could become larger than the number of machines that can be mutated
      numToMutate = machines.size();
    }

    //mutate the first numToMutate TMs in the (shuffled) selected population
    for(int i = 0; i < numToMutate; i++){
        boolean[] encodedChromosome = translator.toBitArray(machines.get(i));
        boolean[] mutatedBitArray = mutateSingle(encodedChromosome);
        machines.set(i, translator.toTuringMachine(mutatedBitArray));
    }
    return machines;
  }

  protected boolean[] mutateSingle(boolean[] parent){
      int bitToMutate = (int) (Math.random() * (parent.length - 1));
      boolean[] child  = new boolean[parent.length];
      for(int i = 0; i < parent.length; i++){
        child[i] = parent[i];
      }
      child[bitToMutate] = !parent[bitToMutate];

      child = repair(child);
      return child;
   }


    public ArrayList<TuringMachine> select(int numToSelect){
      ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>();

      //In case of negative fitness scores, normalise all fitness values:
      int adjustment = 0;
      for(TuringMachine tm : population){
        if(tm.getFitness() <= 0 && Math.abs(tm.getFitness()) >= adjustment){
          adjustment = Math.abs(tm.getFitness()) + 1;//+1 prevents zero probability
        }
      }

      //sum all fitnesses (plus adjustments, if needed) for selection probability denominator
      int denominator = 0;
      for(TuringMachine tm : population){
        denominator += (tm.getFitness() + adjustment);
      }

      //create an array of probabilities of selecting each TuringMachine (sum to 1)
      double[] probabilities = new double[population.size()];
      double probabilitySum = 0.0;

      if(denominator != 0){
        for(int i = 0; i < population.size(); i++){
          double numerator = (double) population.get(i).getFitness() + adjustment;
          //selection probability = fitness of each tm / sum of fitnesses of all tms
          double currentProb = numerator / denominator;
          probabilitySum += currentProb;
          probabilities[i] = probabilitySum;
        }
      }
      else{
        throw new ArithmeticException("Dividing by zero");
      }

      //finally select numToSelect TuringMachines and return them in an ArrayList
      double rand;
      while(selected.size() < numToSelect){
        rand = Math.random();
        //find the TuringMachine corresponding to rand
        for(int i = 0; i < probabilities.length; i++){
          if(rand <= probabilities[i]){
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
     int numBitsForNextState = translator.getNumBitsNeeded(numStates);
     int geneLength = 4 + (2 * numBitsForNextState);
     boolean haltConditionExists = false;
     boolean[] nextStateBinary = new boolean[numBitsForNextState];
     int nextStateInt;
     int index;

     //loop through every gene (state) in the chromosome (TuringMachine)
     for(int i = 0; i < (chromosome.length); i+= geneLength){
       //move and write can't be wrong - only repair nextState sections

       //check that next state is in valid range when reading zero
       index = i + 2;
       for(int j = 0; j < numBitsForNextState; j++){
         nextStateBinary[j] = chromosome[index];
         index++;
       }
       nextStateInt = translator.binaryToInt(nextStateBinary);
       if(nextStateInt > numStates){
         //if not in valid range, change the value of nextStateInt
         nextStateInt = nextStateInt % numStates;
         //System.out.println("Debug: (new value = " + nextStateInt +")");
         boolean[] validNextState = translator.intToBinary(nextStateInt, numBitsForNextState);
         //update corresponding section of chromosome
         index = i + 2;
         for(int j = 0; j < numBitsForNextState; j++){
           chromosome[index] = validNextState[j];
           index++;
         }
       }
       if(nextStateInt == 0){
         haltConditionExists = true;
       }

       //check that next state is in valid range when reading one
       index = i + (geneLength - numBitsForNextState);
       for(int j = 0; j < numBitsForNextState; j++){
         nextStateBinary[j] = chromosome[index];
         index++;
       }
       nextStateInt = translator.binaryToInt(nextStateBinary);
       if(nextStateInt > numStates){
         //if not in valid range, change the value of nextStateInt
         nextStateInt = nextStateInt % numStates;
         boolean[] validNextState = translator.intToBinary(nextStateInt, numBitsForNextState);
         //update corresponding section of chromosome
         index = i + (geneLength - numBitsForNextState);
         for(int j = 0; j < numBitsForNextState; j++){
           chromosome[index] = validNextState[j];
           index++;
         }

       }
       if(nextStateInt == 0){
         haltConditionExists = true;
       }
     }
     return chromosome;
   }


   /*
   Method to encourage garbage collection (helper method for run method)
   */
   private void callGarbageCollector(int currGeneration){
     if(numStates > 5 || (numStates == 5 && (currGeneration + 1) % 500 == 0) || (currGeneration + 1) % 100000 == 0){
      try{
        System.out.println("Memory currently available: " + Runtime.getRuntime().freeMemory());
        Runtime.getRuntime().gc(); //encourage garbage collection
        Thread.sleep(10000); //put main thread to sleep for 10 seconds so that garbage collector has time to kick in before next generation spawns a bunch of threads
      }
      catch(InterruptedException ie){
        //Do nothing
      }
    }
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

  //TODO setElitismRate - be wary of too large though

  public ArrayList<TuringMachine> getPopulation(){
    return this.population;
  }

  public void increaseMutationRate(boolean bool){
    increaseMutation = bool;
  }

  public void setNumGenerations(int maxGenerations){
    this.maxGenerations = maxGenerations;
  }

  public TuringMachine getHighestScoringTM(){
    int bestScore = -1;
    TuringMachine bestTM = population.get(0);
    for(TuringMachine tm : population){
      if(tm.getScore() > bestScore){
        bestTM = tm;
        bestScore = tm.getScore();
      }
    }
    return bestTM;
  }

  public void setReachableFitnessFeature(boolean reachableFitnessFeature){
    this.reachableFitnessFeature = reachableFitnessFeature;
  }

  public void setNumHaltsFitnessFeature(boolean numHaltsFitnessFeature){
    this.numHaltsFitnessFeature = numHaltsFitnessFeature;
  }

  public void setStateUsageFitnessFeature(boolean stateUsageFitnessFeature){
    this.stateUsageFitnessFeature = stateUsageFitnessFeature;
  }

  public void kill(){
    this.stopRunning = true;
  }

  public boolean isRunning(){
    return this.isRunning;
  }

  public int getHighestScore(){
    return this.highestScore;
  }

  public void enableMultithreading(boolean b){
    this.multithreading = b;
  }

  public String getSummary(){
    String summary = "Running generation " + currGeneration + "...\n\n" +
    "crossover rate: " + this.crossoverRate + "\tmutation rate: " + this.currentMutationRate +
    "\nelitism rate: " + this.elitismRate + "\t\tpopulation size: " + population.size();
    TuringMachine tm = this.getHighestScoringTM();
    summary = summary + "\n\nCurrent best Turing machine: \n" + tm.toString()
    + "\nscore = " + tm.getScore() + "\t\tnum shifts = " + tm.getShifts();
    return summary;
  }

  public String getFinalSummary(){
    String summary = "Ran " + currGeneration + " generations\n";
    TuringMachine tm = this.getHighestScoringTM();
    summary = summary + "\nCurrent best Turing machine: \n" + tm.toString()
    + "\nscore = " + tm.getScore() + "\t\tnum shifts = " + tm.getShifts();
    //TODO more?
    return summary;

  }
}
