package com.icarus.sudoku.entity;

import java.util.HashSet;

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

    public boolean has2ValuesInNote() {
        return this.noteValues != null && this.noteValues.size() == 2;
    }

}
