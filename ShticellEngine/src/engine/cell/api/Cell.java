package engine.cell.api;

import engine.sheet.coordinate.Coordinate;
import java.util.Set;

public interface Cell  {
    String getId ();
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
    int getVersion();
    Set<Cell> getDependsOn();
    Set<Cell> getInfluencingOn();
    void setVersion(int currVersion);
    void deleteCell();
    void deleteDependency(Cell deleteMe);
    boolean calculateEffectiveValue();
    void addDependency(Cell referencedCell);
    void addInfluence(Cell thisCell);
    void removeFromInfluenceOn(Cell originCell);
    void deleteMeFromInfluenceList();
}