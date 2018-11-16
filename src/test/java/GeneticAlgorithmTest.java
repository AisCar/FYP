import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class GeneticAlgorithmTest {

  @Test
  public void mutationTest(){
    boolean[] chromosome = {true, false, true, false, true, false};
    GeneticAlgorithm ga = new GeneticAlgorithm();
    boolean[] mutatedChromosome = ga.mutate(chromosome);
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
    //TODO
  }

}
