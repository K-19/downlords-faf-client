package com.faforever.client.tutorial;

public class UnreadTutorialEvent {
  private final boolean hasUnreadTutorial;

  public UnreadTutorialEvent(boolean hasUnreadTutorial) {
    this.hasUnreadTutorial = hasUnreadTutorial;
  }

  public boolean hasUnreadTutorial() {
    return hasUnreadTutorial;
  }
}
