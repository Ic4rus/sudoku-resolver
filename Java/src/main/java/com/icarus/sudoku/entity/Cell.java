package com.icarus.sudoku.entity;

import java.util.HashSet;
import java.util.Set;

public class Cell {

    private int id;

    private int row;

    private int column;

    private int zone;

    private int value;

    private boolean checked;

    private HashSet<Integer> noteValues;

    private HashSet<Integer> removalNoteValues;

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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public HashSet<Integer> getNoteValues() {
        return noteValues;
    }

    public void setNoteValues(HashSet<Integer> noteValues) {
        this.noteValues = noteValues;
    }

    public HashSet<Integer> getRemovalNoteValues() {
        return removalNoteValues;
    }

    public void setRemovalNoteValues(HashSet<Integer> removalNoteValues) {
        this.removalNoteValues = removalNoteValues;
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

    public void filterByRelevantCell(Cell relevantCell) {
        if (this.value == 0 && relevantCell.getValue() > 0) {
            this.noteValues.remove(relevantCell.getValue());
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

    public boolean hasSameNoteValuesWith(Cell targetCell) {
        return this.noteValues != null
                && this.noteValues.equals(targetCell.getNoteValues());
    }

    public void removeNoteValues(Set<Integer> removalNoteValues) {
        if (this.noteValues != null && !this.noteValues.isEmpty()) {
            this.noteValues.removeAll(removalNoteValues);
        }
    }

    public void addRemovalNoteValues(HashSet<Integer> removalNoteValues) {
        if (this.removalNoteValues == null) {
            this.removalNoteValues = new HashSet<>();
        }
        this.removalNoteValues.addAll(removalNoteValues);
    }

    public void removeNoteValues() {
        if (this.removalNoteValues != null && !this.removalNoteValues.isEmpty()) {
            removeNoteValues(removalNoteValues);
        }
        this.removalNoteValues = null;
    }

    public boolean inRelationship(Cell targetCell, Relation... relations) {

        boolean result = false;
        for (Relation relation : relations) {
            switch (relation) {
                case VALUE:
                    result = targetCell.getValue() > 0;
                    break;
                case SAME_ROW:
                    result = this.row == targetCell.getRow();
                    break;
                case SAME_COLUMN:
                    result = this.column == targetCell.getColumn();
                    break;
                case SAME_ZONE:
                    result = this.zone == targetCell.getZone();
                    break;
                case RELEVANT:
                    result = this.row == targetCell.getRow() || this.column == targetCell.getColumn() || this.zone == targetCell.getZone();
                    break;
                case NOT_ME:
                    result = this.id != targetCell.getId();
                    break;
                case NONE_VALUE:
                    result = targetCell.getValue() == 0;
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
