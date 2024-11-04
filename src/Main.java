import java.util.Scanner;
import java.lang.Thread;


public class Main {

    // Appends another cave to the end of a target cave by using recursion
    public static void appendToCaveEnd(Cave cave, Cave caveToAppend)
    {
        if (cave.nextCave != null)
        {
            appendToCaveEnd(cave.nextCave, caveToAppend);
        } else
        {
            cave.nextCave = caveToAppend;
        }
    }

    public static Cave CreateRandomCave(CaveParameters params)
    {
        // Creation parameters
        final int minimumCaveLength = params.MinimumCaveLength;
        final int caveLength = (int) Math.round(Math.random()*5) + minimumCaveLength;
        int branchRandomNumber;
        // Sets the chance of scanning an item in a room
        // Chance to get an item in a room = 1/itemFoundChance
        final int itemFoundChance = 2;
        int itemRandomNumber;
        int itemSeed;
        Cave initialCave = new Cave();

        for (int i = 0; i < caveLength; i++)
        {
            Cave newCave = new Cave();
            // Generate a random number of branching caves per room.
            if (params.IncludeBranches)
            {
                branchRandomNumber = (int) Math.round(Math.random()*3);
                for (int j = 0; j < branchRandomNumber; j++)
                {
                    CaveParameters branchParameters = new CaveParameters();
                    branchParameters.MinimumCaveLength = 1;
                    branchParameters.IncludeBranches = false;
                    branchParameters.IncludeMonster = false;
                    branchParameters.IsBranch = true;
                    newCave.branches.append( CreateRandomCave(branchParameters) );
                }
            }
            // Generate an item in the room
            itemRandomNumber = (int) Math.round(Math.random()*itemFoundChance);
            if (itemRandomNumber == itemFoundChance)
            {
                itemSeed = (int) Math.floor(Math.random()*Entity.values().length);
                newCave.containingEntity = Entity.values()[itemSeed];
            }
            // Sets the branch flag to true if correlated.
            if (params.IsBranch)
                newCave.isBranch = true;
            appendToCaveEnd(initialCave, newCave);
        }
        return initialCave;
    }

    public static Cave exploreCave(Scanner inputScanner, Cave cave)
    {
        System.out.println("Scanning current cave.");
        if (cave.containingEntity != null)
        {
            System.out.println("Found item in cave: "+ cave.containingEntity);
        }

        int branchQuantity = cave.branches.getSize();
        if (branchQuantity == 0)
        {
            System.out.println(".");
        }
        System.out.println("There are "+branchQuantity+1+" sub caves, which one should I take?");

        if (cave.nextCave == null)
        {
            // Check if branch
            if (cave.isBranch)
            {

            }
        }
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        boolean foundMonster = false;

        CaveParameters params = new CaveParameters();
        Cave cave = CreateRandomCave(params);

        System.out.println("You have entered a cave to try find a monster hiding somewhere.");

        while (!foundMonster)
        {
            if (cave.nextCave == null)
                foundMonster = true;

            cave = exploreCave(inputScanner, cave);



        }

    }
}