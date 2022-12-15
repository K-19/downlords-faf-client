package com.faforever.client.tournament;


import com.faforever.client.config.ClientProperties;
import com.faforever.client.config.ClientProperties.Website;
import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.main.event.NavigateEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TournamentsController extends AbstractViewController<Node> {
//  private static final ClassPathResource TOURNAMENT_DETAIL_HTML_RESOURCE = new ClassPathResource("/theme/tournaments/tournament_detail.html");
//
//  private final TimeService timeService;
//  private final I18n i18n;
//  private final TournamentService tournamentService;
//  private final UiService uiService;
//  private final WebViewConfigurer webViewConfigurer;
//  private final NotificationService notificationService;
//
//  public Pane tournamentRoot;
//  public WebView tournamentDetailWebView;
//  public Pane loadingIndicator;
//  public Node contentPane;
//  public ListView<TournamentBean> tournamentListView;
//
//  @Override
//  public Node getRoot() {
//    return tournamentRoot;
//  }
//
//  @Override
//  public void initialize() {
//    contentPane.managedProperty().bind(contentPane.visibleProperty());
//    contentPane.setVisible(false);
//
//    tournamentListView.setCellFactory(param -> new TournamentItemListCell(uiService));
//    tournamentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> displayTournamentItem(newValue));
//  }
//
//  private void onLoadingStart() {
//    JavaFxUtil.runLater(() -> loadingIndicator.setVisible(true));
//  }
//
//  private void onLoadingStop() {
//    JavaFxUtil.runLater(() -> loadingIndicator.setVisible(false));
//  }
//
//  @Override
//  protected void onDisplay(NavigateEvent navigateEvent) {
//    eventBus.post(new UnreadNewsEvent(false));
//    onLoadingStart();
//    loadNews();
//
////    if (contentPane.isVisible()) {
////      return;
////    }
////    onLoadingStart();
////
////    tournamentDetailWebView.setContextMenuEnabled(false);
////    webViewConfigurer.configureWebView(tournamentDetailWebView);
////
////    tournamentService.getAllTournaments()
////        .thenAccept(tournaments -> JavaFxUtil.runLater(() -> {
////          tournaments.sort(
////              Comparator.<TournamentBean, Integer>comparing(o -> o.getStatus().getSortOrderPriority())
////                  .thenComparing(TournamentBean::getCreatedAt)
////                  .reversed()
////          );
////          tournamentListView.getItems().setAll(tournaments);
////          tournamentListView.getSelectionModel().selectFirst();
////          onLoadingStop();
////        })).exceptionally(throwable -> {
////      log.error("Tournaments could not be loaded", throwable);
////      return null;
////    });
//  }
//
//  private void displayTournamentItem(TournamentBean tournamentBean) {
//    String startingDate = i18n.get("tournament.noStartingDate");
//    if (tournamentBean.getStartingAt() != null) {
//      startingDate = MessageFormat.format(i18n.get("dateWithTime"), timeService.asDate(tournamentBean.getStartingAt()), timeService.asShortTime(tournamentBean.getStartingAt()));
//    }
//
//    String completedDate = i18n.get("tournament.noCompletionDate");
//    if (tournamentBean.getCompletedAt() != null) {
//      completedDate = MessageFormat.format(i18n.get("dateWithTime"), timeService.asDate(tournamentBean.getCompletedAt()), timeService.asShortTime(tournamentBean.getCompletedAt()));
//    }
//
//    try (Reader reader = new InputStreamReader(TOURNAMENT_DETAIL_HTML_RESOURCE.getInputStream())) {
//      String html = CharStreams.toString(reader).replace("{name}", tournamentBean.getName())
//          .replace("{challonge-url}", tournamentBean.getChallongeUrl())
//          .replace("{tournament-type}", tournamentBean.getTournamentType())
//          .replace("{starting-date}", startingDate)
//          .replace("{completed-date}", completedDate)
//          .replace("{description}", tournamentBean.getDescription())
//          .replace("{tournament-image}", tournamentBean.getLiveImageUrl())
//          .replace("{open-on-challonge-label}", i18n.get("tournament.openOnChallonge"))
//          .replace("{game-type-label}", i18n.get("tournament.gameType"))
//          .replace("{starting-at-label}", i18n.get("tournament.startingAt"))
//          .replace("{completed-at-label}", i18n.get("tournament.completedAt"))
//          .replace("{loading-label}", i18n.get("loading"));
//
//      tournamentDetailWebView.getEngine().loadContent(html);
//    } catch (IOException e) {
//      throw new AssetLoadException("Tournament view could not be loaded", e, "tournament.viewNotLoaded");
//    }
//  }
//}

  private final EventBus eventBus;
  private final WebViewConfigurer webViewConfigurer;
  private final ClientProperties clientProperties;
  public Pane tournamentsRoot;
  public WebView tournamentsWebView;
  public Control loadingIndicator;
  private final ChangeListener<Boolean> loadingIndicatorListener;

  public TournamentsController(EventBus eventBus, WebViewConfigurer webViewConfigurer, ClientProperties clientProperties) {
    this.eventBus = eventBus;
    this.webViewConfigurer = webViewConfigurer;
    this.clientProperties = clientProperties;

    loadingIndicatorListener = (observable, oldValue, newValue)
        -> loadingIndicator.getParent().getChildrenUnmodifiable().stream()
        .filter(node -> node != loadingIndicator)
        .forEach(node -> node.setVisible(!newValue));
    eventBus.register(this);
  }

  @Override
  public void initialize() {
    tournamentsWebView.setContextMenuEnabled(false);
    webViewConfigurer.configureWebView(tournamentsWebView);

    loadingIndicator.managedProperty().bind(loadingIndicator.visibleProperty());
    loadingIndicator.visibleProperty().addListener(loadingIndicatorListener);
    loadingIndicatorListener.changed(loadingIndicator.visibleProperty(), null, true);

    loadingIndicator.getParent().getChildrenUnmodifiable()
        .forEach(node -> node.managedProperty().bind(node.visibleProperty()));
    tournamentsWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        onLoadingStop();
      }
    });
  }

  private void onLoadingStart() {
    JavaFxUtil.runLater(() -> loadingIndicator.setVisible(true));
  }

  private void onLoadingStop() {
    JavaFxUtil.runLater(() -> loadingIndicator.setVisible(false));
  }

  @Override
  protected void onDisplay(NavigateEvent navigateEvent) {
    eventBus.post(new UnreadTournamentsEvent(false));
    onLoadingStart();
    loadTournaments();
  }

  @Subscribe
  public void onUnreadTournamentsEvent(UnreadTournamentsEvent unreadTournamentsEvent) {
    if (unreadTournamentsEvent.hasUnreadTournaments()) {
      onLoadingStart();
      loadTournaments();
    }
  }


  private void loadTournaments() {
    JavaFxUtil.runLater(() -> {
      Website website = clientProperties.getWebsite();
      tournamentsWebView.getEngine().load(website.getTournamentsHubUrl());
    });
  }

  public Node getRoot() {
    return tournamentsRoot;
  }

}