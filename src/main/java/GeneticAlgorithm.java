import java.util.ArrayList;
import java.util.BitSet;

public class GeneticAlgorithm{
    private double mutationRate, crossoverRate;
    ArrayList<BitSet> population;

    /*
    Constructors
     */

    public GeneticAlgorithm(int populationSize, int numStates){
        population = getPopulation(populationSize, numStates);
        crossoverRate = 0.3; //random guess
        mutationRate = 0.01;
        //run();
    }

    public GeneticAlgorithm(int populationSize, int numStates, double crossoverRate, double mutationRate){
        population = getPopulation(populationSize, numStates);
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
            for(BitSet busyBeaver : population){
                int score = 0;
                //translate to TM
                //run tm and get score
                //calculate fitness
                //calculate next generation
                //validate/repair all members of next gen (Could do this in nextGen method)
            }
        }
        //TODO
    }

    /*
    genetic operators
     */

    private void calculateFitness(){//score(s) from TuringMachine emulator from run()
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
    population management methods
     */

    private ArrayList<BitSet> getPopulation(int populationSize, int numStates){
        BusyBeaverPopulation pop = new BusyBeaverPopulation(numStates, populationSize);
        return pop.getBitSets();
    }

    private void repair(){
        //TODO
        //next state num could be > num states - modify to fix
        //any other issues?
    }

    private void nextGeneration(){
        //TODO
        //Handles crossover, mutation, repair - then updates population

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
