package com.techbank.account.cmd.domain;

import com.techbank.cqrs.core.events.BaseEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author by Zifa Mathebula
 */
public abstract class AggregateRoot {
    private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());
    private final List<BaseEvent> changes = new ArrayList<>();
    protected String id;
    private int version = -1;

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public List<BaseEvent> getUncommittedChanges() {
        return changes;
    }

    public void markChangeAsCommitted() {
        this.changes.clear();
    }

    public void applyChange(BaseEvent event, Boolean isNewEvent) {
        try {
            var method = getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (NoSuchMethodException e) {
            logger.log(Level.WARNING, MessageFormat.format("The apply method was not found in the aggregate for {0}", event.getClass().getName()));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error applying event for aggregate");
        } finally {
            if (isNewEvent) {
                changes.add(event);
            }
        }
    }

    public void raiseEvent(BaseEvent event) {
        applyChange(event, true);
    }

    public void replayEvents(Iterable<BaseEvent> events) {
        events.forEach(event -> applyChange(event, false));
    }
}
