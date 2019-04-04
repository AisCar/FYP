import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import static org.mockito.Mockito.*;


public class GeneticAlgorithmTest {
  GeneticAlgorithm ga;

  @Before
  public void initialise(){
    //currently used in testCrossover, testMutation, testSelection
    ArrayList<TuringMachine> pop = new ArrayList<TuringMachine>();
    TuringMachine tm1 = mock(TuringMachine.class);
    TuringMachine tm2 = mock(TuringMachine.class);
    TuringMachine tm3 = mock(TuringMachine.class);
    TuringMachine tm4 = mock(TuringMachine.class);
    when(tm1.getFitness()).thenReturn(5);
    when(tm2.getFitness()).thenReturn(10);
    when(tm3.getFitness()).thenReturn(-5);
    when(tm3.getFitness()).thenReturn(0);
    pop.add(tm1);
    pop.add(tm2);
    pop.add(tm3);
    pop.add(tm4);
    ga = new GeneticAlgorithm(1, pop);
  }

  @Test
  public void testMutation(){
    System.out.println("testMutation");
    boolean[] chromosome = {true, false, true, false, true, false};
    //GeneticAlgorithm ga = new GeneticAlgorithm();
    boolean[] mutatedChromosome = this.ga.mutateSingle(chromosome);
    //assertNotEquals(chromosome, mutatedChromosome);//This doesn't work, these are pointers
    boolean chromosomeHasChanged = false;
    for(int i = 0; i < chromosome.length; i++){
      if(chromosome[i] != mutatedChromosome[i]){
        chromosomeHasChanged = true;
      }
    }
    assertTrue(chromosomeHasChanged);
  }

  @Test
  public void testCrossover(){
    System.out.println("testCrossover");
    boolean[] parent1 = {true, true, true, true, true, true};
    boolean[] parent2 = {false, false, false, false, false, false};
    boolean[][] children = this.ga.crossoverSingle(parent1, parent2);
    String p1 = "";
    String p2 = "";
    String c1 = "";
    String c2 = "";
    int crossoverPoint = -1;
    for(int i = 0; i < parent1.length; i++){
      p1 = p1 + (parent1[i]? "1" : "0");
    }
    for(int i = 0; i < parent2.length; i++){
      p2 = p2 + (parent2[i]? "1" : "0");
    }

    boolean crossedOver = false;
    for(int i = 0; i < parent1.length; i++){
      c1 = c1 + (children[0][i]? "1" : "0");
      if(!crossedOver && (children[0][i] != parent1[i])){
        crossedOver = true;
        crossoverPoint = i;
      }
    }

    crossedOver = false;
    for(int i = 0; i < parent1.length; i++){
      c2 = c2 + (children[1][i]? "1" : "0");
      if(!crossedOver && (children[1][i] != parent2[i])){
        crossedOver = true;
        assertEquals(i, crossoverPoint);
      }
    }

    System.out.println("Parent Chromosomes: " + p1 + " " + p2);
    System.out.println("Child Chromosomes: " + c1 + " " + c2);
    assertTrue(crossedOver);

  }

  @Test
  public void testSelection(){
    System.out.println("testSelection");
    //ArrayList<TuringMachine> original = ga.getPopulation();
    ArrayList<TuringMachine> selected = new ArrayList<TuringMachine>(); //to suppress compilation failure
    //try{
      selected = ga.select(ga.getPopulation().size());
      /*
    }
    catch(GeneticAlgorithmException gae){
      System.out.println(gae.getMessage());
      fail();
    }*/
    assertEquals(4, selected.size());
    //TODO improve this test - would be nice to assert selectionProbs values...
  }


  @Test
  public void testNextGeneration(){
    System.out.println("\ntestNextGeneration");
    //needs its own ga because otherwise would overwrite mock TMs with real ones
    try{
      GeneticAlgorithm ga2 = new GeneticAlgorithm(10, 1, 1, 0.6, 0.1, 0.0);
      ArrayList<TuringMachine> initialPop = ga2.getPopulation();
      int fitness = 1;
      for(TuringMachine tm : ga2.getPopulation()){
        //actually running them would take too long so just setting an arbitrary fitness value (used in select())
        tm.fitness = fitness;
        fitness++;
      }

      /*
      for(TuringMachine tm : initialPop){
        System.out.println(tm);
      }
      System.out.println("-----");
      */

      ga2.nextGeneration();
      ArrayList<TuringMachine> nextPop = ga2.getPopulation();

      /*
      for(TuringMachine tm : nextPop){
        System.out.println(tm);
      }
      */
    }
    catch(GeneticAlgorithmException gae){
      fail();
    }
    //This isn't really a proper test but I'm satisifed just for now
    //TODO more exhaustive tests
  }


  @Test
  public void testRepair(){
    System.out.println("\ntestRepair");
    GeneticAlgorithm ga2 = new GeneticAlgorithm(4, new ArrayList<TuringMachine>());
    //set next state to 5 (101) and 7 (111)
    boolean[] gene = {true, false, true, false, true, false, true, true, true, true};
    //add same gene to chromosome 4 times (4 states are identical)
    boolean[] chromosome = new boolean[(4*gene.length)];
    for(int i = 0; i < (4*gene.length); i++){
      chromosome[i] = gene[i % gene.length];
    }

    //repair the chromosome
    chromosome = ga2.repair(chromosome);

    System.out.println("Old\tNew");
    for(int i = 0; i < gene.length; i++){
      System.out.println(gene[i] + "\t" + chromosome[i]);
    }
    //TODO asserts etc
  }


  @Test
  public void testElitism(){
    System.out.println("\ntestElitism");
    //Create 10 mock TuringMachines
    ArrayList<TuringMachine> turingMachines = new ArrayList<TuringMachine>();
    for(int i = 0; i < 10; i++){
      TuringMachine tm = mock(TuringMachine.class);
      when(tm.getFitness()).thenReturn(i); //note: collections.sort() wont work on these because theyre mock objs
      turingMachines.add(tm);
    }

    //Test that setting elitismRate and crossoverRate to values that sum to > 1 will cause an Exception
    try{
      GeneticAlgorithm gaInvalidSpy = spy(new GeneticAlgorithm(10, 1, 1, 0.5, 0.1, 0.6)); //pop size = 10, 1 state
      fail(); //should throw exception before this line executes
    }
    catch(GeneticAlgorithmException gae){
      //do nothing, this is expected
    }

    //Test Genetic Algorithm with elitismRate = 100%
    try{
      GeneticAlgorithm gaElitism100Spy = spy(new GeneticAlgorithm(10, 1, 1, 0.0, 0.0, 1.0));
      gaElitism100Spy.population = turingMachines; //try this instead
      ArrayList pop = gaElitism100Spy.nextGeneration();//this is where elitism happens
      verify(gaElitism100Spy).select(0);
      assertEquals(10, pop.size());
      for(int i = 0; i < 10; i++){
        //note: should be turingMachines(10-i) but sorting doesnt work on mock objects w no compareTo implementation
        assertEquals(pop.get(i), turingMachines.get(i));
      }
    }
    catch(GeneticAlgorithmException gae){
      fail();
    }

    //Test Genetic Algorithm with elitismRate = 50%
    try{
      GeneticAlgorithm gaElitism50Spy = spy(new GeneticAlgorithm(10, 1, 1, 0.5, 0.1, 0.5));
      gaElitism50Spy.population = turingMachines;
      ArrayList pop = gaElitism50Spy.nextGeneration(); //this is where elitism happens
      verify(gaElitism50Spy).select(5);
      assertEquals(10, pop.size());
      //assertEquals(pop.get(5), turingMachines.get(0));
      for(int i = 0; i < 5; i++){
        assertEquals(pop.get(i+5), turingMachines.get(i));
      }
    }
    catch(GeneticAlgorithmException gae){
      fail();
    }

    //Test Genetic Algorithm with elitismRate = 0%
    try{
      GeneticAlgorithm gaElitism0Spy = spy(new GeneticAlgorithm(10, 1, 1, 0.5, 0.1, 0.0));
      gaElitism0Spy.population = turingMachines;
      ArrayList pop = gaElitism0Spy.nextGeneration();//this is where elitism happens
      verify(gaElitism0Spy).select(10);
      assertEquals(10, pop.size());
      //more?
    }
    catch(GeneticAlgorithmException gae){
      fail();
    }
  }

  @Test
  public void testIncreaseMutation(){
    System.out.println("testIncreaseMutation");
    TuringMachine tm = mock(TuringMachine.class);
    when(tm.getFitness()).thenReturn(3);
    when(tm.getScore()).thenReturn(1);
    when(tm.previouslyRun()).thenReturn(true);
    ArrayList<TuringMachine> tmList = new ArrayList<TuringMachine>();
    tmList.add(tm);
    GeneticAlgorithm ga = new GeneticAlgorithm(3, tmList);
    ga.setNumGenerations(2000);
    ga.increaseMutationRate(true);
    ga.elitismRate = 0.2;
    ga.run(); //Should run for 10,000 generations
    assertEquals(ga.currentMutationRate, 0.8, 0.01);

  }


}
