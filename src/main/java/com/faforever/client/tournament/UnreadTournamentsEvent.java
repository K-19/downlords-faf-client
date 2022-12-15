package com.faforever.client.tournament;

public class UnreadTournamentsEvent {
  private final boolean hasUnreadTournaments;

  public UnreadTournamentsEvent(boolean hasUnreadTournaments) {
    this.hasUnreadTournaments = hasUnreadTournaments;
  }

  public boolean hasUnreadTournaments() {
    return hasUnreadTournaments;
  }
}
