package com.scnsoft.eldermark.entity.assessment;

public enum AssessmentEventsPreferences {

    NO_EVENTS(false, false, false),
    EVENTS_SHORT_WITH_NOTES(true, true, false),
    EVENTS_WITH_DETAILED_ANSWERS_WITHOUT_NOTES(true, false, true);

    boolean sendEvents;
    boolean sendNotesOnNegativeUpdates;
    boolean includeDetailedAnswers;

    AssessmentEventsPreferences(boolean sendEvents, boolean sendNotesOnNegativeUpdates, boolean includeDetailedAnswers) {
        this.sendEvents = sendEvents;
        this.sendNotesOnNegativeUpdates = sendNotesOnNegativeUpdates;
        this.includeDetailedAnswers = includeDetailedAnswers;
    }

    public boolean getSendEvents() {
        return sendEvents;
    }

    public boolean getSendNotesOnNegativeUpdates() {
        return sendNotesOnNegativeUpdates;
    }

    public boolean getIncludeDetailedAnswers() {
        return includeDetailedAnswers;
    }
}
