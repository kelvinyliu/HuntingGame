import KelvinList.KelvinList;

import java.io.*;
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
                newCave.mainCaveNumber = generateRandomNumber(branchRandomNumber);
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
        System.out.print("");
        return validatedNumberInput(inputScanner, "Enter your choice: ", 2);
    }

    public static void printInventory(KelvinList<Entity> inv) {
        if (inv.getSize() == 0) {
            System.out.println("There are no items in your inventory.");
            return;
        }
        System.out.println("Inventory:");
        int counter = 0;
        for (Entity e : inv) {
            System.out.println(counter + ". " + e);
            counter++;
        }
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

    public static void main(String[] args) throws IOException {
        Scanner inputScanner = new Scanner(System.in);
        SaveDataRecord saveData = LoadGameData();
        boolean foundMonster = false;

        CaveParameters params = new CaveParameters();
        Cave cave = CreateRandomCave(params);
        KelvinList<Entity> items = new KelvinList<Entity>();
        int roomsTraversed = 0;

        DisplayGameStatistics(inputScanner, false, items, roomsTraversed, saveData);
        PrintSeparator();
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
                    if (branchAmount < cave.mainCaveNumber)
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
                        HandleBranchTraversal(saveData, selectedBranch, items);
                        branches.remove(randomiseBranch);
                    }
                } else {
                    roomsTraversed++;
                    cave = cave.nextCave;
                }
            } else HandleInventory(items, inputScanner, saveData, cave);
            PrintSeparator();
        }
        System.out.println("Found the monster!");
        saveData.totalRoomsTraversed += roomsTraversed;
        // Handles managing the save data.
        if (saveData.minimumRooms == -1) {
            saveData.minimumRooms = roomsTraversed;
        } else {
            if (roomsTraversed < saveData.minimumRooms)
                saveData.minimumRooms = roomsTraversed;
        }
        // Show the save data / game instance statistics to the player.
        DisplayGameStatistics(inputScanner, true, items, roomsTraversed, saveData);
        CreateSaveFile(saveData);
    }

    public static void PrintSeparator() {
        System.out.println("-".repeat(20));
    }

    public static void DisplayGameStatistics(Scanner inputScanner, boolean endOfGame, KelvinList<Entity> items, int roomsTraversed, SaveDataRecord saveData) {
        PrintSeparator();
        System.out.println("You have " + saveData.totalGold + " gold.");
        System.out.println("You traversed " + saveData.totalRoomsTraversed + " rooms in total.");
        if (endOfGame) {
            System.out.println("You traversed " + roomsTraversed + " rooms this run.");
            printInventory(items);
        }
    }

    public static void HandleBranchTraversal(SaveDataRecord saveData, Cave selectedBranch, KelvinList<Entity> items) {
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
                HandleInventory(items, inputScanner, saveData, selectedBranch);
            }

        }
        System.out.println();
        System.out.println("There seems to be a dead end...\nReturning back to parent cave.");
    }

    public static void HandleInventory(KelvinList<Entity> items, Scanner inputScanner, SaveDataRecord saveData, Cave currentCave) {
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
            int branchAmount = currentCave.branches.getSize();
            // Checks if there are no branches.
            if (branchAmount == 0) {
                System.out.println("There are no branches in this room...");
                return;
            }
            // Checks if main cave > amount of branches
            if (branchAmount < currentCave.mainCaveNumber)
                currentCave.mainCaveNumber = generateRandomNumber(branchAmount);
            System.out.println(currentCave.mainCaveNumber);
        } else if (selectedItem == Entity.GOLD) {
            System.out.println("Added 1 gold to wallet.");
            saveData.totalGold++;
        } else {
            System.out.println("Item error");
            return;
        }
        items.remove(item_input);
    }

    public static boolean SaveFileExists(String name) {
        File saveFile = new File(name);
        return saveFile.exists();
    }

    public static void CreateSaveFile(SaveDataRecord saveDataRecord) throws IOException {
        final String saveFileName = "save.txt";
        PrintWriter writer = new PrintWriter(new FileWriter(saveFileName));

        writer.println(saveDataRecord.totalRoomsTraversed);
        writer.println(saveDataRecord.minimumRooms);
        writer.println(saveDataRecord.totalGold);

        writer.close();
    }

    public static SaveDataRecord LoadGameData() throws IOException {
        final String saveFileName = "save.txt";
        SaveDataRecord saveData = new SaveDataRecord();

        if (SaveFileExists(saveFileName)) {
            BufferedReader reader = new BufferedReader(new FileReader(saveFileName));

            saveData.totalRoomsTraversed = Integer.parseInt(reader.readLine());
            saveData.minimumRooms = Integer.parseInt(reader.readLine());
            saveData.totalGold = Integer.parseInt(reader.readLine());

            reader.close();
        }
        return saveData;
    }
}