package com.shticell.ui.console;

import com.shticell.engine.dto.SheetDTO;
import com.shticell.ui.console.menu.impl.Menu;
import com.shticell.ui.console.utils.SheetPrinter;
import com.shticell.engine.Engine;
import com.shticell.engine.dto.CellDTO;
import jakarta.xml.bind.JAXBException;
import com.shticell.ui.console.menu.impl.SimpleActionMenuItem;
import com.shticell.ui.console.menu.impl.SubMenuItem;
import com.shticell.ui.console.utils.TablePrinter;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private Engine engine;

    public ConsoleUI(Engine engine) {
        this.scanner = new Scanner(System.in);
        this.engine = engine;
    }

    public void start() {
        Menu mainMenu = createMainMenu();
        while (true) {
            mainMenu.display();
            int choice = getUserChoice();
            if (choice == 8) {
                mainMenu.executeOption(choice);
                break;
            }// This should ideally be handled as a MenuItem action
            mainMenu.executeOption(choice);
            //createNewRange();
        }
    }

    private Menu createMainMenu() {
        Menu menu = new Menu("Main Menu");
        Menu subMenu = new Menu("Save/Load current system state");

        menu.addMenuItem(new SimpleActionMenuItem("Load a file", this::handleOption1));

        menu.addMenuItem(new SimpleActionMenuItem("Show spreadsheet", this::handleOption2));

        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to see its data", this::handleOption3));

        menu.addMenuItem(new SimpleActionMenuItem("Choose a cell to update", this::handleOption4));

        menu.addMenuItem(new SimpleActionMenuItem("Show versions", this::handleOption5));

        menu.addMenuItem(new SubMenuItem("Save/Load current system state",subMenu));
        subMenu.addMenuItem(new SimpleActionMenuItem("Save current system state", this::handleOption6));
        subMenu.addMenuItem(new SimpleActionMenuItem("Load saved system state",this::handleOption7));
        subMenu.addMenuItem(new SimpleActionMenuItem("Back to main menu",()->{}));

        menu.addMenuItem(new SimpleActionMenuItem("Sort sheet", this::handleOption8));

        menu.addMenuItem(new SimpleActionMenuItem("Exit", this::handleExit));

        return menu;
    }



    private void handleOption1() {
        System.out.println("Please enter the full path to the XML file you want to load or Q/q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path.trim()))
            return;
        try {
            engine.loadSheetFile(path);
            System.out.println("File loaded successfully");
        } catch (JAXBException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private void handleOption2() {
        try {
            SheetDTO result = engine.showSheet();
            SheetPrinter.printSheet(result);
        }
        catch (IllegalStateException e){
            System.out.println("Error showing sheet: " + e.getMessage());
        }
    }

    private void handleOption3() {
        try {
            String cellId;
            boolean flag = true;
            while (flag) {
                System.out.println("Please select a cell to view its details or Q/q to go back to the main menu: ");
                cellId = scanner.nextLine();
                if (checkIfQuit(cellId))
                    return;
                try {
                    CellDTO cellInfo = engine.getCellInfo(cellId);
                    flag = false;
                    if (cellInfo != null) {
                        System.out.println("Cell ID: " + cellInfo.getId() + "\nOriginal Value: " + cellInfo.getOriginalValue() +
                                "\nEffective Value: " + cellInfo.getEffectiveValue() + "\nLast updated version: " + cellInfo.getVersion());
                        System.out.println("\nDependencies of this cell: ");
                        cellInfo.getDependsOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
                        System.out.println("\nInfluences of this cell: ");
                        cellInfo.getInfluencingOn().forEach(cellDTO -> System.out.print(cellDTO.getId() + ", "));
                    } else {
                        System.out.println("\nCell " + cellId + " has not been initialized yet.");
                    }

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }catch (IllegalStateException e){
            System.out.println("Error showing cell: " + e.getMessage());
        }
    }


    private void handleOption4() {
        try {
            boolean flag = true;
            String cellId = "";
            while (flag) {
                System.out.println("\nPlease select a cell to update or Q/q to go back to the main menu: ");
                cellId = scanner.nextLine();
                if (checkIfQuit(cellId))
                    return;
                try {
                    CellDTO cell = engine.getCellInfo(cellId);
                    if (cell != null) {
                        System.out.println("\nCell ID: " + cell.getId() + "\nOriginal Value: " + cell.getOriginalValue() +
                                "\nEffective Value: " + cell.getEffectiveValue().toString());
                    } else {
                        System.out.println("\nCell " + cellId + " has not been initialized yet.");
                    }
                    flag = false;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            flag = true;
            while (flag) {
                System.out.println("\nPlease enter the new cell's value or Q/q to go back to the main menu: ");
                String value = scanner.nextLine();
                if (checkIfQuit(value))
                    return;
                try {
                    engine.setCell(cellId, value);
                    flag = false;
                } catch (IllegalArgumentException e) {
                    System.out.println("Error updating cell: " + e.getMessage());
                    handleOption4();
                }
            }
            handleOption2();
        }catch (IllegalStateException e){
            System.out.println("Error updating cell: " + e.getMessage());
        }
    }

    private void handleOption5() {
        try {
            Map<Integer, Integer> versionTable = engine.showVersionTable();
            TablePrinter.printVersionToCellChangedTable(versionTable);
            System.out.println("Please choose a version to see its data or Q/q to go back to the main menu: ");
            String version = scanner.nextLine();
            if(checkIfQuit(version))
                return;
            SheetPrinter.printSheet(engine.showChosenVersion(Integer.parseInt(version)));
        }
        catch (IllegalStateException e) {
            System.out.println("Error showing version: " + e.getMessage());
        }
        catch (NumberFormatException e) {
            System.out.println("Error showing version: " + e.getMessage());
        }
    }

    private void handleExit() {
        System.out.println("Exiting...");
    }

    private void handleOption6() {

        System.out.println("Please enter the full path to where you want to save the file or q/Q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path))
            return;
        try{
            engine.writeEngineToFile(path);
            System.out.println("System saved successfully");
        }
        catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void handleOption7() {
        System.out.println("Please enter the full path to where your saved file is or q/Q to go back to the main menu: ");
        String path = scanner.nextLine();
        if(checkIfQuit(path))
            return;
        try{
            engine = engine.readEngineFromFile(path);
            System.out.println("System loaded successfully");
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private void handleOption8() {
        System.out.println("enter range:");
        String range = scanner.nextLine();
        System.out.println("enter columns for sort:");
        String columns = scanner.nextLine();
        SheetDTO sheetToprint = engine.sortSheet(range,columns);
        SheetPrinter.printSheet(sheetToprint);
    }


    private int getUserChoice() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next();
            }
        }

        return choice;
    }

    private boolean checkIfQuit(String input) {
        if (input.toUpperCase().equals("Q")) {
            return true;
        }
        return false;
    }

    private void createNewRange(){
        try {
            engine.addRange("1st range!", "C3..C5");
            System.out.println("Range created successfully");

        }
        catch (IllegalArgumentException e) {
            System.out.println("Error creating range: " + e.getMessage());
        }
    }

}
