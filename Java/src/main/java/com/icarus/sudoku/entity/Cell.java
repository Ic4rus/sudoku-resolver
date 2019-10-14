package com.icarus.sudoku.entity;

import java.util.HashSet;
import java.util.Set;

public class Cell {

    private int id;

    private int row;

    private int column;

    private int zone;

    private int value;

    private HashSet<Integer> noteValues;

    public Cell(int id, int row, int column, int zone, int value, HashSet<Integer> noteValues) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.zone = zone;
        this.value = value;
        this.noteValues = noteValues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public HashSet<Integer> getNoteValues() {
        return noteValues;
    }

    public void setNoteValues(HashSet<Integer> noteValues) {
        this.noteValues = noteValues;
    }

    public void show() {
        System.out.println(String.format("___cell: zone = %d, row = %d, column = %d", this.zone, this.row, this.column));
    }

    public boolean equal(Cell targetCell) {
        return this.id == targetCell.getId();
    }

    public boolean isNoneValue() {
        return this.value == 0;
    }

    public boolean relateToCell(Cell targetCell) {
        return this.id != targetCell.getId()
                && (this.row == targetCell.getRow()
                || this.column == targetCell.getColumn()
                || this.zone == targetCell.getZone());
    }

    public void filterByRelatedCell(Cell relatedCell) {
        if (this.value == 0 && relatedCell.getValue() > 0) {
            this.noteValues.remove(relatedCell.getValue());
        }
    }

    public boolean selfFilling() {
        if (noteValues != null && noteValues.size() == 1) {
            value = (int) noteValues.toArray()[0];
            noteValues = null;
            return true;
        } else {
            return false;
        }
    }

    public boolean containValueInNote(int value) {
        return this.noteValues != null && this.noteValues.contains(value);
    }

    public void fillValue(int value) {
        if (this.value == 0 && value > 0) {
            this.value = value;
            this.noteValues = null;
        }
    }

    public boolean hasNumberValuesInNote(int n) {
        return this.noteValues != null && this.noteValues.size() == n;
    }

    public boolean hasSameNoteValuesWith(Cell targetCell) {
        return this.noteValues != null
                && this.noteValues.equals(targetCell.getNoteValues());
    }

    public boolean inSameRow(Cell targetCell) {
        return this.id != targetCell.getId() && this.row == targetCell.getRow();
    }

    public boolean inSameColumn(Cell targetCell) {
        return this.id != targetCell.getId() && this.column == targetCell.getColumn();
    }

    public boolean inSameZone(Cell targetCell) {
        return this.id != targetCell.getId() && this.zone == targetCell.getZone();
    }

    public void filterByUnrelatedCell(Cell targetCell) {
        if (this.value == 0 && targetCell.getValue() == 0) {
            this.noteValues.removeAll(targetCell.getNoteValues());
        }
    }

    public void removeNoteValues(Set<Integer> removalNoteValues) {
        if (this.noteValues != null && !this.noteValues.isEmpty()) {
            this.noteValues.removeAll(removalNoteValues);
        }
    }

    public boolean inRelationship(Cell targetCell, Relation... relations) {

        boolean result = false;
        for (Relation relation : relations) {
            switch (relation) {
                case SAME_ROW:
                    result = this.row == targetCell.getRow();
                    break;
                case SAME_COLUMN:
                    result = this.column == targetCell.getColumn();
                    break;
                case SAME_ZONE:
                    result = this.zone == targetCell.getZone();
                    break;
                case NOT_ME:
                    result = this.id != targetCell.getId();
                    break;
                case OTHER_ROW:
                    result = this.row != targetCell.getRow();
                    break;
                case OTHER_COLUMN:
                    result = this.column != targetCell.getColumn();
                    break;
                case OTHER_ZONE:
                    result = this.zone != targetCell.getZone();
                    break;
                default:
                    result = false;
            }
            if (!result) {
                break;
            }
        }
        return result;
    }

}
