package com.faforever.client.topMonth;

public class UnreadTopMonthEvent {
  private final boolean hasUnreadTopMonth;

  public UnreadTopMonthEvent(boolean hasUnreadTopMonth) {
    this.hasUnreadTopMonth = hasUnreadTopMonth;
  }

  public boolean hasUnreadTopMonth() {
    return hasUnreadTopMonth;
  }
}
