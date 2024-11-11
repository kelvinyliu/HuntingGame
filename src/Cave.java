import KelvinList.KelvinList;

public class Cave
{
    Entity containingEntity;
    Cave nextCave;
    boolean isBranch;
    Integer mainCaveNumber; // Used to set the randomised branch number to make the BRANCH_SCANNER function.
    KelvinList<Cave> branches = new KelvinList<Cave>();
}
