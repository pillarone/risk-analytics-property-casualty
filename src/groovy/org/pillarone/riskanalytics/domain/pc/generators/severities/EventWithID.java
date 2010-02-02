package org.pillarone.riskanalytics.domain.pc.generators.severities;

/**
 * Two objects are considered the same if their id corresponds. The date property is not checked.
 */
public class EventWithID extends Event {

    public EventWithID() {
    }

    private int id;

    @Override
    public String toString() {
        return new StringBuilder().append(super.toString()).append(", id: ").append(id).toString();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventWithID) {
            return id == ((EventWithID) obj).getId();
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}