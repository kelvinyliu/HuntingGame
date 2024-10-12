import KelvinList.KelvinList;

public class Cave
{
    Entity containingEntity;
    Cave nextCave;
    KelvinList<Cave> branches = new KelvinList<Cave>();
}
