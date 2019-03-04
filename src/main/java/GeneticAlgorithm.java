import java.util.ArrayList;
import java.util.Collections;

public class GeneticAlgorithm {
  //class variables
  private double mutationRate, crossoverRate, elitismRate;
  protected ArrayList<TuringMachine> population;
  private Translator translator;
  private int numStates;
  private int numGenerations;
  private int maxGenerations = 10000;
  private boolean stopRunning = false;

  //increase mutation variables
  private boolean increaseMutation;
  private double currentMutationRate;

  //feature toggles
  private boolean reachableFitnessFeature;
  private boolean numHaltsFitnessFeature;
  private boolean stateUsageFitnessFeature;


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
    numGenerations = 0;
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
    numGenerations = 0;
    this.maxGenerations = maxGenerations;
    if(elitismRate + crossoverRate > 1.0 || elitismRate + mutationRate > 1.0){
      throw new GeneticAlgorithmException("Elitism/Crossover/Mutation rate too high");
    }
  }


  /*
    main genetic algorithm method
  */
    public void run(){
      numGenerations = 0; //counter for number of generations that have run
      int score = 0; //highest busy beaver score in this run of the genetic algorithm
      TuringMachine currBestTM; //best Turing machine in current generation

      //increase mutation variables
      this.currentMutationRate = this.mutationRate;
      int mutationMultiplier = 1;
      int increaseGen = 0;

      if(elitismRate == 1.0){
        maxGenerations = 1;
      }

      while((numGenerations < maxGenerations) && !stopRunning){ //max int value = 2147483647
        //Provide user feedback
        if(numStates > 4 || numGenerations % 1000 == 0){ //print less frequently if n <= 4 because these don't run so slowly
          System.out.println("Running generation " + numGenerations + "...");
        }

        //Run every TuringMachine (that hasn't already been run) in the current population
        for(TuringMachine busyBeaver : population){
          if(!busyBeaver.previouslyRun()){
            busyBeaver.setNumHaltsFitnessFeature(this.numHaltsFitnessFeature); //TODO: IS this best way to do this?
            busyBeaver.setReachableFitnessFeature(this.reachableFitnessFeature);//TODO: IS this best way to do this?
            busyBeaver.setStateUsageFitnessFeature(this.stateUsageFitnessFeature);//TODO: IS this best way to do this?
            busyBeaver.run();
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
          increaseGen = numGenerations;
          //update variables for the increase mutation feature
          currentMutationRate = mutationRate;
          mutationMultiplier = 1;
          //Print out details of new highest scoring tm
          System.out.println("New high score achieved in generation " + numGenerations);
          System.out.println("Score = " + score);
          System.out.println("(Fitness: " + currBestTM.getFitness() + " )");
          System.out.println(currBestTM.toString());

        }

        //Optional feature: If score hasn't increased in many generations, increase the mutation rate
        int generationsSinceChange = numGenerations - increaseGen;
        if(increaseMutation && (generationsSinceChange > 1) && (generationsSinceChange % 100 == 0)){ //NOTE: Changed from 10000(ridiculous) to 100(ok?)
          mutationMultiplier++;
          currentMutationRate = (mutationMultiplier * mutationRate);
          if(currentMutationRate > 1.0){
            currentMutationRate = 1.0;
          }
          System.out.println("Mutation rate is now " + currentMutationRate + "%");
        }

        if(numGenerations % 200 == 0 && numGenerations > 0){
          //Print out a summary.
          System.out.println("Generation " + numGenerations + " Summary\nCurrent highest scoring tm: ");
          currBestTM = this.getHighestScoringTM();
          System.out.println(currBestTM.toString());
          System.out.println("Score: " + currBestTM.getScore() + "\tFitness: " + currBestTM.getFitness());
        }



        //If max score found, exit
        if((numStates == 4 && score == 13)){
          break;
        }
        //If running another generation, create that population
        else if(numGenerations < maxGenerations-1){
          this.population = nextGeneration();
        }

        numGenerations++;
      }

      //Final summary
      currBestTM = this.getHighestScoringTM();
      System.out.println("\n\nTuring machine with highest Busy Beaver score in final population:");
      System.out.println("Score: " + currBestTM.getScore());
      System.out.println(currBestTM.toString());

      System.out.println("Highest score achieved across all generations: " + score); //This will be redundant if elitism is enabled (not yet implemented)

      System.out.println("\n\nTop 10 Turing machines with highest fitness in final population:");
      for (int i = 0; i < 10; i++) {
        currBestTM = population.get(i);
        System.out.println(currBestTM.toString() + "\nScore: " + currBestTM.getScore() + "\nShifts: " + currBestTM.getShifts() + "\nFitness: " + currBestTM.getFitness() +"\n\n");
      }
      //TODO - proper convergence
  }


  /*
  Method to create next generation of Turing Machines using crossover, mutation
  */
  protected ArrayList<TuringMachine> nextGeneration(){
    //Sort current population by fitness (descending)
    Collections.sort(population); //prob unnecessary - TODO remove

    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();

    int numElite = (int) (population.size() * elitismRate);

    //Perform selection
    try{
      nextGeneration = select(population.size() - numElite);
    }
    catch(GeneticAlgorithmException gae){
      //As of right now, this is caused by zero denominator which is caused by all zero fitnesses.
      //TODO handle - throw back up, should really cancel whole program
    }

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

  /* //moved to TuringMachine
  protected int calculateFitness(TuringMachine busyBeaver){
      int score = busyBeaver.getScore();
      //Could make this more complicated but for now is fine
      return score;
  }
  */

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
      numToCrossOver--; //was ++, now -- in case crossoverRate = 100% (or elitism causes not enough available for crossoverRate)
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
      //throw new GeneticAlgorithmException("Mutation rate too high: trying to mutate " + numToMutate + " Turing machines, method only received " +  machines.size());
    }


    //mutate the first numToMutate TMs in the population
    for(int i = 0; i < numToMutate; i++){
        boolean[] encodedChromosome = translator.toBitArray(machines.get(i));
        boolean[] mutatedBitArray = mutateSingle(encodedChromosome);
        machines.set(i, translator.toTuringMachine(mutatedBitArray));
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

      child = repair(child);
      return child;
   }


    public ArrayList<TuringMachine> select(int numToSelect) throws GeneticAlgorithmException{
      ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>();

      //In case of negative fitness scores, normalise all fitness values:
      int adjustment = 0;
      for(TuringMachine tm : population){
        if(tm.getFitness() < 0 && Math.abs(tm.getFitness()) >= adjustment){
          adjustment = Math.abs(tm.getFitness()) + 1;//+1 prevents zero probability
        }
      }

      //sum all fitnesses (plus adjustments, if needed) for selection probability denominator
      int denominator = 0;
      for(TuringMachine tm : population){
        denominator += (tm.getFitness() + adjustment);
      }

      //create an array of probabilities of selecting each TuringMachine (sum to 1)
      double[] selectionProbs = new double[population.size()];
      double probabilityInterval = 0.0;
      if(denominator != 0){
        for(int i = 0; i < population.size(); i++){
          double numerator = (double) population.get(i).getFitness() + adjustment;
          //selection probability = fitness of each tm / sum of fitnesses of all tms
          double intervalSize = numerator / denominator;
          probabilityInterval += intervalSize;
          selectionProbs[i] = probabilityInterval;
        }
      }
      else{
        throw new GeneticAlgorithmException("Zero denominator");
      }

      //finally select numToSelect TuringMachines and return them in an ArrayList
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
     }//end for loop through all chromosome bits

     //check that a halt condition exists (if not, insert one)
     if(!haltConditionExists){
       //TODO introduce one randomly - or not
     }

     return chromosome; //to prevent compilation errors
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
  public void setReachableFitnessFeature(boolean reachableFitnessFeature){ //dont like that these are duplicated here and in TuringMachine - consdier a redesign
    //sets feature toggle that enables/disables state reachability code in calculateFitness
    this.reachableFitnessFeature = reachableFitnessFeature;

  }

  public void setNumHaltsFitnessFeature(boolean numHaltsFitnessFeature){ //dont like that these are duplicated here and in TuringMachine
    //sets feature toggle that enables/disables number of halt conditions code in calculateFitness
    this.numHaltsFitnessFeature = numHaltsFitnessFeature;

  }

  public void setStateUsageFitnessFeature(boolean stateUsageFitnessFeature){ //dont like that these are duplicated here and in TuringMachine
    //sets feature toggle that enables/disables state usage code in calculateFitness
    this.stateUsageFitnessFeature = stateUsageFitnessFeature;

  }

  public void setStopRunning(boolean b){
    this.stopRunning = b;
  }


}
