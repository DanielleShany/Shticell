package com.shticell.engine;

import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.RangeDTO;
import com.shticell.engine.dto.SheetDTO;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {

    SheetDTO loadSheetFile(String filePath) throws JAXBException;

    SheetDTO showSheet();

    CellDTO getCellInfo(String cellId);

    void setCell(String cellId, String cellValue);

    Map<Integer,Integer> showVersionTable();

    SheetDTO showChosenVersion(int chosenVersion);


    void writeEngineToFile(String fileName) throws IOException;

    Engine readEngineFromFile(String fileName) throws IOException, ClassNotFoundException;

    RangeDTO addRange(String name, String cellsRange);

    void removeRange(String name);

    SheetDTO sortSheet(String rangeToSort,String columnsToSortBy);

    SheetDTO filterSheet(String rangeToFilter,String columnsToFilterBy,List<String> valuesToFilterBy);

    List<String> getUniqueColumnValues(String columnId);
}
