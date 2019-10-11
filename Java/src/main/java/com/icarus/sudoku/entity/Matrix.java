package com.icarus.sudoku.entity;

import java.util.*;

public class Matrix {

    private int matrixSize;

    private int blockSize;

    private HashSet<Integer> allValues;

    private Cell[][] cells;

    private List<Cell> noneValueCellList;

    public Matrix(int[][] values) {
        matrixSize = 9;
        blockSize = (int) Math.sqrt(matrixSize);
        allValues = new HashSet<>();
        for (int i = 0; i < matrixSize; i++) {
            allValues.add(i + 1);
        }
        cells = new Cell[matrixSize][matrixSize];
        noneValueCellList = new ArrayList<>();
        Cell cell;
        int id = 0, row, column, zone, value;
        for (row = 0; row < matrixSize; row++) {
            for (column = 0; column < matrixSize; column++) {
                zone = (row / blockSize) * blockSize + (column / blockSize);
                value = values[row][column];
                if (value == 0) {
                    cell = new Cell(id++, row, column, zone, values[row][column], new HashSet<>(allValues));
                    noneValueCellList.add(cell);
                } else {
                    cell = new Cell(id++, row, column, zone, values[row][column], null);
                }
                cells[row][column] = cell;
            }
        }
    }

    public void show() {
        StringBuilder border = new StringBuilder();
        StringBuilder divider = new StringBuilder();
        for (int i = 0; i < (matrixSize * 3 + blockSize + 1); i++) {
            border.append("-");
            divider.append(i % (blockSize * 3 + 1) == 0 ? "|" : "-");
        }
        System.out.println(border);
        for (int i = 0; i < matrixSize; i++) {
            System.out.print("|");
            for (int j = 0; j < matrixSize; j++) {
                System.out.print(" " + cells[i][j].getValue() + " ");
                if ((j % blockSize) == (blockSize - 1)) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (((i % blockSize) == (blockSize - 1)) && ((i / blockSize) < (blockSize - 1))) {
                System.out.println(divider);
            }
        }
        System.out.println(border);
        for (Cell cell : noneValueCellList) {
            System.out.print(cell.getRow() + "-" + cell.getColumn() + ": ");
            for (int v : cell.getNoteValues()) {
                System.out.print(v + ", ");
            }
            System.out.println();
        }
    }

    public List<Cell> findRelatedCell(Cell sourceCell) {
        List<Cell> relatedCellList = new ArrayList<>();
        Cell targetCell;
        for (int row = 0; row < matrixSize; row++) {
            for (int column = 0; column < matrixSize; column++) {
                targetCell = cells[row][column];
                if (sourceCell.relateToCell(targetCell)) {
                    relatedCellList.add(targetCell);
                }
            }
        }
        return relatedCellList;
    }

    public void note() {
        List<Cell> relatedCellList;
        for (Cell noneValueCell : noneValueCellList) {
            relatedCellList = findRelatedCell(noneValueCell);
            for (Cell relatedCell : relatedCellList) {
                noneValueCell.filterByRelatedCell(relatedCell);
            }
        }
    }

    public void step1() {

        Iterator<Cell> noneValueCellIterator = noneValueCellList.iterator();
        Cell noneValueCell;
        boolean selfFillingSuccess;
        List<Cell> relatedCellList;
        while (noneValueCellIterator.hasNext()) {
            noneValueCell = noneValueCellIterator.next();
            noneValueCell.show();
            // Auto fill if there is only one note value
            selfFillingSuccess = noneValueCell.selfFilling();
            if (selfFillingSuccess) {
                relatedCellList = findRelatedCell(noneValueCell);
                for (Cell relatedCell : relatedCellList) {
                    relatedCell.filterByRelatedCell(noneValueCell);
                }
                noneValueCellIterator.remove();
            } // The cell have many note values
            else {
                relatedCellList = findRelatedCell(noneValueCell);
                HashSet<Integer> noteValues = noneValueCell.getNoteValues();
                boolean isExists;
                for (Integer noteValue : noteValues) {
                    isExists = false;
                    for (Cell relatedCell : relatedCellList) {
                        if (relatedCell.containValueInNote(noteValue)) {
                            isExists = true;
                            break;
                        }
                    }
                    if (!isExists) {
                        noneValueCell.fillValue(noteValue);
                        for (Cell relatedCell : relatedCellList) {
                            relatedCell.filterByRelatedCell(noneValueCell);
                        }
                        noneValueCellIterator.remove();
                        break;
                    }
                }
            }
        }
    }

//    /**
//     * The only cell have this note value in row/column/zone
//     *
//     * @return
//     */
//    public boolean findUniqueNoteValueOfCell(Cell sourceCell) {
//
//        HashSet<Integer> noteValues = sourceCell.getNoteValues();
//        // The list of cell in zone
//        List<Cell> cellsInZone = findNoneValueCells(sourceCell, true, false, false, true, false);
//        List<Cell> cellsInRow = findNoneValueCells(sourceCell, false, true, false, true, false);
//        List<Cell> cellsInColumn = findNoneValueCells(sourceCell, false, false, true, true, false);
//        for (Integer noteValue : noteValues) {
//            for (Cell cell : cellsInZone) {
//
//            }
//            for (Cell cell : cellsInRow) {
//
//            }
//            for (Cell cell : cellsInColumn) {
//
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Find none value cells by condition
//     *
//     * @param sourceCell        the cell to start find
//     * @param inZone            <code>true</code> the cell in zone
//     *                          <code>false</code> the cell out zone
//     * @param inRow             <code>true</code> the cell on the row
//     *                          <code>false</code> the cell on the other row
//     * @param inColumn          <code>true</code> the cell on the column
//     *                          <code>false</code> the cell on the other column
//     * @param isRelated         <code>true</code> inZone || inRow || inColumn
//     *                          <code>false</code> inZone && inRow && inColumn
//     * @param includeSourceCell
//     * @return
//     */
//    public List<Cell> findNoneValueCells(Cell sourceCell, boolean inZone, boolean inRow, boolean inColumn,
//                                         boolean isRelated, boolean includeSourceCell) {
//        List<Cell> cellList = new ArrayList<>();
//        if (noneValueCellList == null || noneValueCellList.isEmpty()) {
//            return cellList;
//        }
//        int zone = sourceCell.getZone();
//        int row = sourceCell.getRow();
//        int column = sourceCell.getColumn();
//        boolean satisfyZone, satisfyRow, satisfyColumn;
//        for (Cell cell : noneValueCellList) {
//            satisfyZone = (cell.getZone() == zone) == inZone;
//            satisfyRow = (cell.getRow() == row) == inRow;
//            satisfyColumn = (cell.getColumn() == column) == inColumn;
//            if () {
//                cellList.add(cell);
//            }
//        }
//        return cellList;
//    }

    public void step2() {
        int size = noneValueCellList.size();
        Cell cell1, cell2;
        for (int i = 0; i < size; i++) {
            cell1 = noneValueCellList.get(i);
            if (cell1.hasNumberValuesInNote(2)) {
                for (int j = i + 1; j < size; j++) {
                    cell2 = noneValueCellList.get(j);
                    if (cell1.hasSameNoteValuesWith(cell2) && cell1.relateToCell(cell2)) {
                        updateNoteValuesInStep2(cell1, cell2);
                    }
                }
            }
        }
    }

    public void updateNoteValuesInStep2(Cell cell1, Cell cell2) {
        for (Cell cell : noneValueCellList) {
            if (cell.inSameRow(cell1) && cell.inSameRow(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameColumn(cell1) && cell.inSameColumn(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameZone(cell1) && cell.inSameZone(cell2)) {
                cell.filterByUnrelatedCell(cell1);
            }
        }
    }

    public void step3() {
        int size = noneValueCellList.size();
        Cell cell1, cell2, cell3;
        for (int i = 0; i < size; i++) {
            cell1 = noneValueCellList.get(i);
            if (cell1.hasNumberValuesInNote(3)) {
                for (int j = i + 1; j < size; j++) {
                    cell2 = noneValueCellList.get(j);
                    if (cell1.hasSameNoteValuesWith(cell2) && cell1.relateToCell(cell2)) {
                        for (int k = j + 1; k < size; k++) {
                            cell3 = noneValueCellList.get(k);
                            if (cell2.hasSameNoteValuesWith(cell3) && cell2.relateToCell(cell3)) {
                                updateNoteValuesInStep3(cell1, cell2, cell3);
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateNoteValuesInStep3(Cell cell1, Cell cell2, Cell cell3) {
        for (Cell cell : noneValueCellList) {
            if (cell.inSameRow(cell1) && cell.inSameRow(cell2) && cell.inSameRow(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameColumn(cell1) && cell.inSameColumn(cell2) && cell.inSameRow(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
            if (cell.inSameZone(cell1) && cell.inSameZone(cell2) && cell.inSameZone(cell3)) {
                cell.filterByUnrelatedCell(cell1);
            }
        }
    }

    /**
     * Find none value cell
     *
     * @param zone    the index of zone
     * @param inZone  true - in zone, false - out zone
     * @param isRow   true - row, false - column
     * @param index   the index of row/column
     * @param isEqual the cell on the row/column
     * @return
     */
    public List<Cell> findNoneValueCells(int zone, boolean inZone, boolean isRow, int index, boolean isEqual) {
        List<Cell> cellList = new ArrayList<>();
        int cellIndex;
        for (Cell cell : noneValueCellList) {
            cellIndex = isRow ? cell.getRow() : cell.getColumn();
            if ((cell.getZone() == zone) == inZone && (cellIndex == index) == isEqual) {
                cellList.add(cell);
            }
        }
        return cellList;
    }

    /**
     *
     * @param zone
     * @param isRow
     * @param index
     */
    public void updateNoteValuesInStep4(int zone, boolean isRow, int index) {

        Set<Integer> noteValuesIsNotDuplicate = new HashSet<>();
        List<Cell> cellsInZone = findNoneValueCells(zone, true, isRow, index, true);
        List<Cell> cellsOutZone = findNoneValueCells(zone, false, isRow, index, true);
        if (cellsInZone == null || cellsInZone.isEmpty() || cellsOutZone == null || cellsOutZone.isEmpty()) {
            return;
        }
        Set<Integer> noteValuesInZone = new HashSet<>();
        Set<Integer> noteValuesOutZone = new HashSet<>();
        HashSet<Integer> noteValues;
        for (Cell cell : cellsInZone) {
            noteValues = cell.getNoteValues();
            if (noteValues != null && !noteValues.isEmpty()) {
                noteValuesInZone.addAll(noteValues);
            }
        }
        for (Cell cell : cellsOutZone) {
            noteValues = cell.getNoteValues();
            if (noteValues != null && !noteValues.isEmpty()) {
                noteValuesOutZone.addAll(noteValues);
            }
        }
        for (Integer noteValue : noteValuesInZone) {
            if (!noteValuesOutZone.contains(noteValue)) {
                noteValuesIsNotDuplicate.add(noteValue);
            }
        }
        if (!noteValuesIsNotDuplicate.isEmpty()) {
            List<Cell> otherCellsInZone = findNoneValueCells(zone, true, isRow, index, false);
            if (otherCellsInZone != null && !otherCellsInZone.isEmpty()) {
                for (Cell cell : otherCellsInZone) {
                    cell.removeNoteValues(noteValuesIsNotDuplicate);
                }
            }
        }
    }

    public int checkTotalNoteValues() {
        int total = 0;
        for (Cell cell : noneValueCellList) {
            total += cell.getNoteValues().size();
        }
        return total;
    }

    /**
     * Remove note values of other cells in zone if it have cells on the same row or column that have note values
     * that other cells do not have on the row or column
     */
    public void step4() {
        int startRow, startColumn;
        List<Cell> cellsInZone, cellsOutZone;
        Set<Integer> noteValuesIsNotDuplicate;
        // Loop each zone
        for (int zone = 0; zone < matrixSize; zone++) {
            // Loop each row in zone
            startRow = (zone / blockSize) * blockSize;
            for (int row = startRow; row < startRow + blockSize; row++) {
                updateNoteValuesInStep4(zone, true, row);
            }
            // Loop each column in zone
            startColumn = (zone % blockSize) * blockSize;
            for (int column = startColumn; column < startColumn + blockSize; column++) {
                updateNoteValuesInStep4(zone, false, column);
            }
        }
        show();
    }

    public void resolve() {
        note();
        show();
//        checkTotalNoteValues();
//        step4();
        step1();


//        int previousSize = noneValueCellList.size();
//        while (noneValueCellList.size() > 0) {
//            step1();
//            step2();
//            show();
//            if (noneValueCellList.size() == previousSize) {
//                step3();
//            } else {
//                previousSize = noneValueCellList.size();
//            }
//        }
    }

    private static int[][] matrixValue = {
            {0, 0, 0, 8, 7, 1, 0, 2, 0},
            {0, 0, 0, 3, 0, 9, 8, 0, 0},
            {0, 9, 8, 2, 0, 0, 0, 0, 0},
            {7, 8, 0, 9, 0, 0, 3, 0, 0},
            {0, 0, 0, 0, 0, 7, 0, 0, 0},
            {0, 5, 0, 0, 0, 3, 0, 0, 0},
            {9, 7, 4, 1, 3, 2, 6, 8, 5},
            {0, 0, 0, 0, 0, 0, 0, 0, 9},
            {2, 0, 0, 0, 9, 0, 0, 0, 4},
    };

    public static void main(String[] args) {
        Matrix matrix = new Matrix(matrixValue);
        matrix.resolve();
    }

}
