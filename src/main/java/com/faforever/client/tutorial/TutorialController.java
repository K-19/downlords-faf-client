package com.faforever.client.tutorial;

import com.faforever.client.config.ClientProperties;
import com.faforever.client.config.ClientProperties.Website;
import com.faforever.client.domain.TutorialBean;
import com.faforever.client.domain.TutorialCategoryBean;
import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.fx.WebViewConfigurer;
import com.faforever.client.main.event.NavigateEvent;
import com.faforever.client.news.UnreadNewsEvent;
import com.faforever.client.theme.UiService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TutorialController extends AbstractViewController<Node> {
  private final EventBus eventBus;
  private final WebViewConfigurer webViewConfigurer;
  private final ClientProperties clientProperties;
  public Pane tutorialsRoot;
  public WebView tutorialsWebView;
  public Control loadingIndicator;
  private final ChangeListener<Boolean> loadingIndicatorListener;

  public TutorialController(EventBus eventBus, WebViewConfigurer webViewConfigurer, ClientProperties clientProperties) {
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
    tutorialsWebView.setContextMenuEnabled(false);
    webViewConfigurer.configureWebView(tutorialsWebView);

    loadingIndicator.managedProperty().bind(loadingIndicator.visibleProperty());
    loadingIndicator.visibleProperty().addListener(loadingIndicatorListener);
    loadingIndicatorListener.changed(loadingIndicator.visibleProperty(), null, true);

    loadingIndicator.getParent().getChildrenUnmodifiable()
        .forEach(node -> node.managedProperty().bind(node.visibleProperty()));
    tutorialsWebView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
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
    eventBus.post(new UnreadNewsEvent(false));
    onLoadingStart();
    loadNews();
  }

  @Subscribe
  public void onUnreadNewsEvent(UnreadNewsEvent unreadNewsEvent) {
    if (unreadNewsEvent.hasUnreadNews()) {
      onLoadingStart();
      loadNews();
    }
  }


  private void loadNews() {
    JavaFxUtil.runLater(() -> {
      Website website = clientProperties.getWebsite();
      tutorialsWebView.getEngine().load(website.getTopHubUrl());
    });
  }

  public Node getRoot() {
    return tutorialsRoot;
  }

}
