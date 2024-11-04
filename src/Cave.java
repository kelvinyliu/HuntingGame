import KelvinList.KelvinList;

public class Cave
{
    Entity containingEntity;
    Cave nextCave;
    Cave parentCave;
    boolean isBranch;
    KelvinList<Cave> branches = new KelvinList<Cave>();
}
