import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class GeneticAlgorithmTest {
  GeneticAlgorithm ga;

  @Before
  public void initialise(){
    ga = new GeneticAlgorithm();
  }

  @Test
  public void mutationTest(){
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
  public void crossoverTest(){
    //TODO - this isnt an actual test, really
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

    //if(p1 != null && p2 != null && c1 != null && c2 != null)
    System.out.println("Parent Chromosomes: " + p1 + " " + p2);
    System.out.println("Child Chromosomes: " + c1 + " " + c2);
    assertTrue(crossedOver);

  }

  //TODO test the other crossover and mutation methods once select is implemented
  //TODO test and implement select and nextGeneration



}
