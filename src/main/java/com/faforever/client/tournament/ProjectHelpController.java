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
public class ProjectHelpController extends AbstractViewController<Node> {

  private final EventBus eventBus;
  private final WebViewConfigurer webViewConfigurer;
  private final ClientProperties clientProperties;
  public Pane projectHelpRoot;
  public WebView projectHelpWebView;
  public Control loadingIndicator;
  private final ChangeListener<Boolean> loadingIndicatorListener;

  public ProjectHelpController(EventBus eventBus, WebViewConfigurer webViewConfigurer, ClientProperties clientProperties) {
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
    projectHelpWebView.setContextMenuEnabled(false);
    webViewConfigurer.configureWebView(projectHelpWebView);

    loadingIndicator.managedProperty().bind(loadingIndicator.visibleProperty());
    loadingIndicator.visibleProperty().addListener(loadingIndicatorListener);
    loadingIndicatorListener.changed(loadingIndicator.visibleProperty(), null, true);

    loadingIndicator.getParent().getChildrenUnmodifiable()
        .forEach(node -> node.managedProperty().bind(node.visibleProperty()));
    projectHelpWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
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
    eventBus.post(new UnreadHelpEvent(false));
    onLoadingStart();
    loadTournaments();
  }

  @Subscribe
  public void onUnreadTournamentsEvent(UnreadHelpEvent unreadTournamentsEvent) {
    if (unreadTournamentsEvent.hasUnread()) {
      onLoadingStart();
      loadTournaments();
    }
  }


  private void loadTournaments() {
    JavaFxUtil.runLater(() -> {
      Website website = clientProperties.getWebsite();
      projectHelpWebView.getEngine().load(website.getProjectHelpHubUrl());
    });
  }

  public Node getRoot() {
    return projectHelpRoot;
  }
}
