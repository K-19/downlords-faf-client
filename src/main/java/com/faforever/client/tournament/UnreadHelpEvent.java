package com.faforever.client.tournament;

public class UnreadHelpEvent {
  private final boolean hasUnread;

  public UnreadHelpEvent(boolean hasUnread) {
    this.hasUnread = hasUnread;
  }

  public boolean hasUnread() {
    return hasUnread;
  }
}
