import KelvinList.KelvinList;

import java.util.Scanner;

public class Main {

    // Appends another cave to the end of a target cave by using recursion
    public static void appendToCaveEnd(Cave cave, Cave caveToAppend) {
        if (cave.nextCave != null) {
            appendToCaveEnd(cave.nextCave, caveToAppend);
        } else {
            cave.nextCave = caveToAppend;
        }
    }

    // Generates a random number from 0-maximum (inclusive)
    public static int generateRandomNumber(int maximum) {
        return (int) (Math.random() * (maximum + 1));
    }

    public static Cave CreateRandomCave(CaveParameters params) {
        // Creation parameters
        final int minimumCaveLength = params.MinimumCaveLength;
        final int caveLength = generateRandomNumber(5) + minimumCaveLength;
        int branchRandomNumber;
        // Sets the chance of scanning an item in a room
        // Chance to get an item in a room = 1/itemFoundChance
        final int itemFoundChance = 2;
        int itemRandomNumber;
        int itemSeed;
        Cave initialCave = new Cave();

        for (int i = 0; i < caveLength; i++) {
            Cave newCave = new Cave();
            // Generate a random number of branching caves per room.
            if (params.IncludeBranches) {
                branchRandomNumber = generateRandomNumber(3);
                for (int j = 0; j < branchRandomNumber; j++) {
                    CaveParameters branchParameters = new CaveParameters();
                    branchParameters.MinimumCaveLength = 1;
                    branchParameters.IncludeBranches = false;
                    branchParameters.IncludeMonster = false;
                    branchParameters.IsBranch = true;
                    newCave.branches.append(CreateRandomCave(branchParameters));
                }
            }
            // Generate an item in the room
            itemRandomNumber = generateRandomNumber(itemFoundChance);
            if (itemRandomNumber == itemFoundChance) {
                itemSeed = generateRandomNumber(Entity.values().length - 1);
                newCave.containingEntity = Entity.values()[itemSeed];
            }
            // Sets the branch flag to true if correlated.
            if (params.IsBranch)
                newCave.isBranch = true;
            appendToCaveEnd(initialCave, newCave);
        }
        return initialCave;
    }

    public static Entity exploreCave(Scanner inputScanner, Cave cave) {
        // BEGIN
        System.out.println("Scanning current cave.");
        if (cave.containingEntity != null) {
            System.out.println("Found item in cave: " + cave.containingEntity);
        } else {
            System.out.println("No item in cave");
        }
        return cave.containingEntity;
    }

    public static KelvinList<Cave> scanBranches(Cave cave) {
        if (cave.branches.getSize() != 0) {
            System.out.println("There seems to be multiple paths...");
        }
        return cave.branches;
    }

    public static int printMenu(Scanner inputScanner) {
        System.out.println("MENU:");
        System.out.println("1. Continue forward");
        System.out.println("2. Use item");
        System.out.print("Enter your choice: ");
        return Integer.parseInt(inputScanner.nextLine());
    }

    public static void printInventory(KelvinList<Entity> inv) {
        System.out.println("Inventory:");
        int counter = 0;
        for (Entity e : inv) {
            System.out.println(counter + ". " + e);
            counter++;
        }
        return;
    }

    //Gets a valid number input from the user.
    public static int validatedNumberInput(Scanner inputScanner, String question, int maximum) {
        System.out.print(question);
        boolean validInput = false;

        int parsedNumber = 0;
        while (!validInput) {
            boolean isAllDigits = false;
            String input = inputScanner.nextLine();
            // check all characters are numbers.
            for (char character : input.toCharArray()) {
                if (!Character.isDigit(character)) {
                    System.out.println("Enter a valid number.");
                    break;
                }
                isAllDigits = true;
            }
            if (!isAllDigits)
                continue;
            parsedNumber = Integer.parseInt(input);

            if (parsedNumber < 0) {
                System.out.println("Enter a positive number.");
                continue;
            } else if (parsedNumber > maximum) {
                System.out.println("Enter a number within the range.");
                continue;
            }
            validInput = true;
        }
        return parsedNumber;
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        boolean foundMonster = false;

        CaveParameters params = new CaveParameters();
        Cave cave = CreateRandomCave(params);
        KelvinList<Entity> items = new KelvinList<Entity>();
        int roomsTraversed = 0;

        System.out.println("You have entered a cave to try find a monster hiding somewhere.");

        while (!foundMonster) {
            if (cave.nextCave == null)
                foundMonster = true;

            // Scan for item in current cave.
            System.out.println("Traversed " + roomsTraversed + " rooms.");
            Entity item = exploreCave(inputScanner, cave);
            if (item != null) {
                items.append(item);
                cave.containingEntity = null;
            }
            KelvinList<Cave> branches = scanBranches(cave);
            int branchAmount = branches.getSize();
            int choice = printMenu(inputScanner);

            if (choice == 1) {
                if (branchAmount != 0) {
                    cave.mainCaveNumber = generateRandomNumber(branchAmount);
                    // DEBUG
                    System.out.println("DEBUG: MAIN CAVE: " + cave.mainCaveNumber);
                    // END DEBUG
                    int path = validatedNumberInput(inputScanner, "Which path should I take? (0..." + (branchAmount) + ")", branchAmount);

                    // Checks if the player has entered the main cave path towards the monster, or a branch.
                    if (path == cave.mainCaveNumber) {
                        roomsTraversed++;
                        cave = cave.nextCave;
                    } else {
                        int randomiseBranch = generateRandomNumber(branchAmount - 1);
                        Cave selectedBranch = (Cave) cave.branches.retrieve(randomiseBranch).data;
                        HandleBranchTraversal(selectedBranch, items);
                        branches.remove(randomiseBranch);
                    }
                } else {
                    roomsTraversed++;
                    cave = cave.nextCave;
                }
            } else HandleInventory(items, inputScanner, cave);
            System.out.println();
        }
        System.out.println("Found the monster!");
    }

    public static void HandleBranchTraversal(Cave selectedBranch, KelvinList<Entity> items) {
        System.out.println("You seem to have entered a sub-section of the cave...");
        Scanner inputScanner = new Scanner(System.in);
        while (selectedBranch.nextCave != null) {
            Entity item = exploreCave(inputScanner, selectedBranch);
            if (item != null) {
                items.append(item);
                selectedBranch.containingEntity = null;
            }

            int choice = printMenu(inputScanner);
            if (choice == 1) {
                selectedBranch = selectedBranch.nextCave;
            } else {
                HandleInventory(items, inputScanner, selectedBranch);
            }

        }
        System.out.println();
        System.out.println("There seems to be a dead end...\nReturning back to parent cave.");
    }

    public static void HandleInventory(KelvinList<Entity> items, Scanner inputScanner, Cave currentCave) {
        // Check if inventory is empty
        if (items.getSize() == 0) {
            System.out.println("There are no items in your inventory.");
            System.out.println(); // Create new line
            return;
        }
        printInventory(items);
        int item_input = validatedNumberInput(inputScanner, "Which item would you like to use? ", items.getSize() - 1);
        Entity selectedItem = (Entity) items.retrieve(item_input).data;

        if (selectedItem == Entity.BRANCH_SCANNER) {
            // Check if in branch
            if (currentCave.isBranch) {
                System.out.println("You are already in a branch, this item would be useless...");
                return;
            }

            System.out.println(currentCave.mainCaveNumber);
        }
    }
}