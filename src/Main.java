import KelvinList.KelvinList;

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

    public static Entity exploreCave(Scanner inputScanner, Cave cave)
    {
        // BEGIN
        System.out.println("Scanning current cave.");
        if (cave.containingEntity != null)
        {
            System.out.println("Found item in cave: "+ cave.containingEntity);
        } else
        {
            System.out.println("No item in cave");
        }
        return cave.containingEntity;
    }

    public static KelvinList<Cave> scanBranches(Cave cave)
    {
        if (cave.branches != null)
        {
            System.out.println("There seems to be multiple paths...");
        }
        return cave.branches;
    }

    public static int printMenu(Scanner inputScanner, boolean containsBranches)
    {
        System.out.println("MENU:");
        System.out.println("1. Continue forward");
        System.out.println("2. Use item");
        System.out.print("Enter your choice: ");
        return Integer.parseInt(inputScanner.nextLine());
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        boolean foundMonster = false;

        CaveParameters params = new CaveParameters();
        Cave cave = CreateRandomCave(params);
        KelvinList<Entity> items = new KelvinList<Entity>();

        System.out.println("You have entered a cave to try find a monster hiding somewhere.");

        while (!foundMonster)
        {
            if (cave.nextCave == null)
                foundMonster = true;

            // Scan for item in current cave.
            Entity item = exploreCave(inputScanner, cave);
            if (item != null)
                items.append(item);

            KelvinList<Cave> branches = scanBranches(cave);
            int branchAmount = branches.getSize();

            if (branchAmount > 0)
            {
                System.out.println("Which path should I take? (1..."+(branchAmount+1)+")");
            }
            int choice = printMenu(inputScanner, false);
            System.out.println();
            if (choice == 1)
            {
                cave = cave.nextCave;
            }
            else if (choice == 2) {

            }
        }
        System.out.println("Found the monster!");
    }
}