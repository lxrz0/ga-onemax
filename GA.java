import java.util.Arrays;
import java.util.Random;

/**
 * Simple (skeleton) GA for the OneMax problem.
 *
 * @author Fernando Otero
 * @version 1.1
 */
public class GA {
    /**
     * Number of bits of the individual encoding.
     */
    private static final int BITS = 100;

    /**
     * The population size.
     */
    private static final int POPULATION_SIZE = 100;

    /**
     * The number of generations.
     */
    private static final int MAX_GENERATION = 100;

    private static final int N_ELITES = 5;

    private static final int TOURNAMENT_SIZE = 3;

    /**
     * Random number generation.
     */
    private Random random = new Random();

    /**
     * The current population;
     */
    private boolean[][] population = new boolean[POPULATION_SIZE][BITS];

    /**
     * Fitness values of each individual of the population.
     */
    private int[] fitness = new int[POPULATION_SIZE];

    /**
     * Starts the execution of the GA.
     */
    public void run() {
        // --------------------------------------------------------------//
        // initialises the population //
        // --------------------------------------------------------------//
        initialise();

        // --------------------------------------------------------------//
        // evaluates the propulation //
        // --------------------------------------------------------------//
        evaluate();

        for (int g = 0; g < MAX_GENERATION; g++) {
            // ----------------------------------------------------------//
            // creates a new population //
            // ----------------------------------------------------------//

            System.out.println("Generation: " + g);

            boolean[][] newPopulation = new boolean[POPULATION_SIZE][BITS];

            // retrieve best fitnesses from previous population
            int[] elites = findBestN(N_ELITES);

            // System.out.println(Arrays.toString(elites));

            // insert elites
            // for (int i=0; i<N_ELITES;i++) {
            //     newPopulation[i] = population[elites[i]].clone();
            // }

            for (int i = 0; i<N_ELITES; i++) {
                int idx = elites[i]; // get the index from the elites array
                newPopulation[idx] = population[idx]; // assign the VALUE that the index which is stored in the elites array is pointing 
            }
            
            for (int i = N_ELITES; i < POPULATION_SIZE-1; i+=2) {
                // select two parents using roulette selection
                int parent1 = select();
                int parent2 = select();

                double odds = random.nextDouble();
                double crossOverProbability = 0.6;
                double mutationProbability = 0.4;

                // perform a one-point crossover
                boolean[][] offspring = {population[parent1], population[parent2]};

                if (mutationProbability >= odds) {
                    // mutate the offspring
                    offspring[0] = mutation(parent1);
                    offspring[1] = mutation(parent2);
                }

                if (crossOverProbability >= odds) {
                    boolean[][] crossoverChildren = crossover(parent1, parent2);
                    offspring[0] = crossoverChildren[0];
                    offspring[1] = crossoverChildren[1];
                }


                // add the offspring to the new population
                newPopulation[i] = offspring[0];
                newPopulation[i+1] = offspring[1];
            }

            population = newPopulation;

            // TODO

            // ----------------------------------------------------------//
            // evaluates the new population //
            // ----------------------------------------------------------//
            evaluate();

            int bestFitness = fitness[0];

            for(int i=0;i<fitness.length;i++) {
                if (fitness[i] > bestFitness) {
                    bestFitness = fitness[i];
                }
            }

            // System.out.println("Generation #" + (g+1) + " Max fitness: " + bestFitness);

            System.out.println("Here are the top 5 best fitness values");
            // System.out.println(Arrays.toString(findBestN(8)));
            findBestN(5);
        }

        // prints the value of the best individual

        // TODO
    }

    /**
     * Method returns the best N fitness values from the population (elitism implemetation)
     * @param n
     * @return
     */
    private int[] findBestN (int n) {
        int [] elite = new int[n]; // create new array of N length to store elite fitness members of the gene pool
        
        // copy fitness values
        int[] copyFitness = (int[]) fitness.clone();

        for (int e=0; e < n; e++) {
            int best = 0;

            for (int i=1; i<POPULATION_SIZE; i++) {
                if (copyFitness[best] < copyFitness[i]) {
                    best = i;
                }
            }

            elite[e] = best;

            // This marks the index of the selected value as "removed", making sure it doesn't have a score that will be considered
            copyFitness[best] = 0;
        }

        // System.out.println((fitness[elite[0]]));
        for (int i : elite) {
            System.out.print(fitness[i]);
            System.out.print(" | ");
        }
        System.out.println("");

        return elite;
    }

    /**
     * Retuns the index of the selected parent using a roulette wheel.
     *
     * @return the index of the selected parent using a roulette wheel.
     */
    private int select() {

        // tournament selection implementation
        int bestIndex = random.nextInt(TOURNAMENT_SIZE);
        
        for(int i = 0; i < TOURNAMENT_SIZE; i++ ) {
            int index = random.nextInt(POPULATION_SIZE); // random selection of population
            if (fitness[index] > fitness[bestIndex]) {
                bestIndex = index;
            }
        }

        return bestIndex;
        
        // prepares for roulette wheel selection
        // double[] roulette = new double[POPULATION_SIZE];
        // double total = 0;

        // for (int i = 0; i < POPULATION_SIZE; i++) {
        //     total += fitness[i];
        // }

        // double cumulative = 0.0;

        // for (int i = 0; i < POPULATION_SIZE; i++) {
        //     roulette[i] = cumulative + (fitness[i] / total);
        //     cumulative = roulette[i];
        // }

        // roulette[POPULATION_SIZE - 1] = 1.0;

        // int parent = -1;
        // double probability = random.nextDouble();

        // // selects a parent individual
        // for (int i = 0; i < POPULATION_SIZE; i++) {
        //     if (probability <= roulette[i]) {
        //         parent = i;
        //         break;
        //     }
        // }

        // return parent;
    }

    /**
     * Initialises the population.
     */
    private void initialise() {
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[i].length; j++) {
                // initialize the popuations
                population[i][j] = random.nextBoolean();
            }
        }
    }

    /**
     * Calculates the fitness of each individual.
     */
    private void evaluate() {
        for (int i = 0; i < population.length; i++) {
            int sum = 0;
            for (int j = 0; j < population[i].length; j++) {
                if (population[i][j] == true)
                    sum++;
            }

            fitness[i] = sum;
        }
    }

    private boolean[][] crossover(int first, int second) {
        int cut = random.nextInt(BITS);

        boolean[] firstChild = new boolean[BITS];
        boolean[] secondChild = new boolean[BITS];

        for (int i = 0; i < population[first].length; i++) {
            // just push non sliced elements over to the new child since they are uneffected
            // by the cross-over
            if (i < cut) {
                firstChild[i] = population[first][i];
                secondChild[i] = population[second][i];
            } else {
                firstChild[i] = population[second][i];
                secondChild[i] = population[first][i];
            }
        }

        boolean[][] offspring = { firstChild, secondChild };

        return offspring;
    }

    private boolean[] mutation(int parent) {
        // select a random point within the array and flip it to introduce randomness
        int index = random.nextInt(BITS);
        
        // clone the selected parent from the population as to not directly modify
        boolean[] selectedInvidivual = population[parent].clone();

        selectedInvidivual[index] = !selectedInvidivual[index];

        return selectedInvidivual;
    }

    public static void main (String[] args) {
        GA geneticAlgorithm = new GA();
        geneticAlgorithm.run();
    }
}

