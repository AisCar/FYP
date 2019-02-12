import java.util.ArrayList;
import java.util.Collections;

public class GeneticAlgorithm {
  //class variables
  private double mutationRate, crossoverRate;
  private ArrayList<TuringMachine> population;
  private PopulationGenerator pop;
  private Translator translator;
  private int numStates;
  private int numGenerations;
  private int maxGenerations = 10000;

  //increase mutation variables
  private boolean increaseMutation;
  private double currentMutationRate;


  /*
    Constructors
   */

  public GeneticAlgorithm(int populationSize, int numStates){//keep? remove?
    pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
    crossoverRate = 0.6;
    mutationRate = 0.1;
    currentMutationRate = mutationRate;
    this.numStates = numStates;
    numGenerations = 0;
    //run();
  }

  public GeneticAlgorithm(int populationSize, int numStates, double crossoverRate, double mutationRate){
    pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    currentMutationRate = mutationRate;
    numGenerations = 0;
    //run();
  }

  public GeneticAlgorithm(int populationSize, int numStates, int maxGenerations, double crossoverRate, double mutationRate){
    pop = new PopulationGenerator(numStates, populationSize);
    population = pop.getPopulation();
    translator = new Translator(numStates);
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    currentMutationRate = mutationRate;
    numGenerations = 0;
    this.maxGenerations = maxGenerations;
  }


  /*
    main genetic algorithm method
  */
    public void run(){
      numGenerations = 0;
      int score = 0;

      //increase mutation variables
      this.currentMutationRate = this.mutationRate;
      int mutationMultiplier = 1;
      int increaseGen = 0;

      while(numGenerations < maxGenerations){ //max int value = 2147483647
        //Provide user feedback
        if(numGenerations % 1000 == 0){
          //System.out.println("Running generation " + numGenerations + "...");
        }

        //Run every TuringMachine (that hasn't already been run) in the current population
        for(TuringMachine busyBeaver : population){
          if(!busyBeaver.previouslyRun()){
            busyBeaver.run();
          }
        }

        //Sort population (highest scoring in this generation will be first)
        Collections.sort(population); //This is part of current bug stack trace TODO

        //Check if score has increased
        TuringMachine tm = this.population.get(0);
        int currBestScore = tm.getScore();
        if(currBestScore > score){
          //update variables
          score = currBestScore;
          increaseGen = numGenerations;
          //update variables for the increase mutation feature
          currentMutationRate = mutationRate;
          mutationMultiplier = 1;
          //Print out details of new highest scoring tm
          System.out.println("New best score: " + score);
          System.out.println("State transition table of TM with score " + score);
          System.out.println("Generation: " + numGenerations);
          for(String row : translator.toStateTransitionTable(tm)){
            System.out.println(row);
          }


        }

        //Optional feature: If score hasn't increased in many generations, increase the mutation rate
        int generationsSinceChange = numGenerations - increaseGen;
        if(increaseMutation && (generationsSinceChange > 1) && (generationsSinceChange % 100000 == 0)){
          mutationMultiplier++;
          currentMutationRate = (mutationMultiplier * mutationRate);
          if(currentMutationRate > 1.0){
            currentMutationRate = 1.0;
          }
          System.out.println("Mutation rate is now " + currentMutationRate + "%");
        }

        if(numGenerations % 1000 == 0){
          //Print out a summary.
          System.out.println("Current highest scoring tm: ");
          for(String str : translator.toStateTransitionTable(population.get(0))){
            System.out.println(str);
          }
          System.out.println("Score: " + population.get(0).getScore());
          System.out.println("Highest score achieved: " + score);
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
      //TODO - proper convergence
  }


  /*
  Method to create next generation of Turing Machines using crossover, mutation
  */
  protected ArrayList<TuringMachine> nextGeneration(){
    //Sort current population by fitness (descending)
    Collections.sort(population);

    ArrayList<TuringMachine> nextGeneration = new ArrayList<TuringMachine>();


    //Perform selection
    nextGeneration = select();

    //Perform crossover
    nextGeneration = crossover(nextGeneration);

    //Perform mutation
    nextGeneration = mutation(nextGeneration);

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
    int numToCrossOver = (int) (machines.size() * crossoverRate);

    //Make sure to have an even number of parents
    if(numToCrossOver % 2 != 0){
      numToCrossOver++; //or -- instead?
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
    int numToMutate = (int) (machines.size() * currentMutationRate);

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


   public ArrayList<TuringMachine> select(){
    int numToSelect = population.size();
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


}
