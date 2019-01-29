import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/*
UserInterface will:
1. Take input parameters for GeneticAlgorithm (population size, crossover rate,
mutation rate etc.) as well as n (number of states)
2. Display information about current program run
*/

public class UserInterface extends JFrame{
  //TODO: Rename other variables so that you can rename these ones w/o mixing them up
  private double crossover, mutation;
  private int population, generations;

  public UserInterface(){
    super("Busy Beavers");

    //set some default values
    this.crossover = 0.7;
    this.mutation = 0.05;

    //set up window details (move this line down?)
    setSize(600,400);

    //Panel 1: TODO
    JPanel p1 = new JPanel();
    JLabel title = new JLabel("Optimising Turing Machine Busy Beaver scores using a Genetic Algorithm");
    title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 16));
    p1.add(title);

    //Panel 2: Set crossover rate, mutation rate, population size and num generations
    JPanel p2 = new JPanel();
    p2.setLayout(new GridLayout(10,1));
    p2.setPreferredSize(new Dimension(175, 500));
    JLabel crossoverLabel = new JLabel("crossover rate");
    String str = crossover + "%";
    JLabel currCrossover = new JLabel(str);
    JLabel mutationLabel = new JLabel("mutation rate");
    JTextField crossoverRate = new JTextField(""+crossover+"");
    JTextField mutationRate = new JTextField("" + mutation + "");
    JCheckBox increaseMutation = new JCheckBox("increase mutation rate after several generations with no improvement");
    JLabel popSizeLabel = new JLabel("population size");
    JLabel numGenLabel = new JLabel("maximum number of generations"); //TODO make this optional?
    JTextField populationSize = new JTextField(1);
    JTextField numGenerations = new JTextField(1);
    p2.add(crossoverLabel);
    p2.add(crossoverRate);
    p2.add(mutationLabel);
    p2.add(mutationRate);
    p2.add(increaseMutation);
    p2.add(popSizeLabel);
    p2.add(populationSize);
    p2.add(numGenLabel);
    p2.add(numGenerations);

    //Panel 3: A large text area containing information ...
    JPanel p3 = new JPanel();
    JTextArea description = new JTextArea("TODO", 10, 10);
    description.setPreferredSize(new Dimension(375, 500));
    description.setEditable(false);
    //going for a console look?
    /*
    description.setForeground(Color.WHITE);
    description.setBackground(Color.BLACK);
    description.setFont(Font.getFont(Font.DIALOG_INPUT)); //not noticing a difference... uh oh.
    */
    p3.add(description);


    //Button to run the program
    JButton runGAButton = new JButton("Run Genetic Algorithm");
    runGAButton.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "SELECTED" : "DESELECTED");
                description.setText("Woohoo!");
                //this.getInputs();
                //this.startGeneticAlgorithm();
            }
        });


    //Add all panels to the frame
    this.getContentPane().add(BorderLayout.NORTH, p1);
    getContentPane().add(BorderLayout.WEST, p2); //p2a and p2b?
    getContentPane().add(BorderLayout.EAST, p3);
    getContentPane().add(BorderLayout.SOUTH, runGAButton);
    setVisible(true);
  }

  public static void main(String[] args) {
    //Run the user interface
    UserInterface ui = new UserInterface();
    ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

}
