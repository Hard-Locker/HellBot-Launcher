package halot.nikitazolin.launcher.gui;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.app.manager.AppStatusObserver;
import halot.nikitazolin.launcher.localization.gui.tray.TrayProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SystemTrayManager implements AppStatusObserver {

  private static final String appName = ApplicationRunnerImpl.APP_NAME;
  private static final String iconPath = ApplicationRunnerImpl.APP_ICON_PATH;
  private static final String iconTrayOnPath = ApplicationRunnerImpl.TRAY_ON_ICON_PATH;
  private static final String iconTrayOffPath = ApplicationRunnerImpl.TRAY_OFF_ICON_PATH;

  private final AppService appService;
  private final TrayProvider trayProvider;

  private TrayIcon trayIcon;
  private MenuItem statusItem;

  @PostConstruct
  public void init() {
    appService.addObserver(this);
  }

  @PreDestroy
  public void destroy() {
    appService.removeObserver(this);
  }

  @Override
  public void onAppStatusChanged() {
    updateStatus();
  }

  public void makeTray(JFrame frame) {
    checkSupported();

    PopupMenu popupMenu = createPopupMenu(frame);

    trayIcon = initializeTrayImage();
    trayIcon.setPopupMenu(popupMenu);
    trayIcon.setImageAutoSize(true);

    try {
      SystemTray.getSystemTray().add(trayIcon);
      log.debug("Application icon {} added to system tray", appName);
    } catch (AWTException e) {
      log.error("Failed to add icon to system tray", e);
    }

    trayIcon.addActionListener(e -> openMainWindow(frame));

    updateStatus();
  }

  private TrayIcon initializeTrayImage() {
    URL iconUrl = getClass().getResource(iconPath);

    if (iconUrl != null) {
      log.error("Set base tray icon");
      return new TrayIcon(Toolkit.getDefaultToolkit().getImage(iconUrl), appName);
    } else {
      log.error("Tray icon not found");
      return new TrayIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB), appName);
    }
  }

  private PopupMenu createPopupMenu(JFrame frame) {
    PopupMenu popupMenu = new PopupMenu();

    String openText = trayProvider.getText("tray.open");
    String startText = trayProvider.getText("tray.start");
    String stopText = trayProvider.getText("tray.stop");
    String shutdownText = trayProvider.getText("tray.shutdown");

    MenuItem openItem = new MenuItem(openText);
    statusItem = new MenuItem();
    MenuItem startItem = new MenuItem(startText);
    MenuItem stopItem = new MenuItem(stopText);
    MenuItem shutdownItem = new MenuItem(shutdownText);

    statusItem.setEnabled(false);

    openItem.addActionListener(e -> openMainWindow(frame));
    startItem.addActionListener(e -> startApp());
    stopItem.addActionListener(e -> stopApp());
    shutdownItem.addActionListener(e -> shutdownApp());

    popupMenu.add(openItem);
    popupMenu.addSeparator();
    popupMenu.add(statusItem);
    popupMenu.add(startItem);
    popupMenu.add(stopItem);
    popupMenu.addSeparator();
    popupMenu.add(shutdownItem);

    return popupMenu;
  }

  private void openMainWindow(JFrame frame) {
    frame.setVisible(true);
    frame.setState(Frame.NORMAL);
    log.debug("Window {} opened from tray", appName);
  }

  private void startApp() {
    appService.start();
    log.debug("Application started from tray");
    updateStatus();
  }

  private void stopApp() {
    appService.stop();
    log.debug("Application stopped from tray");
    updateStatus();
  }

  private void shutdownApp() {
    stopApp();
    log.info("Shutting down the application {}", appName);
    System.exit(0);
  }

  private void updateStatus() {
    updateStatusText();
    updateStatusIcon();
  }

  private void updateStatusText() {
    String statusText = trayProvider.getText("tray.status");
    String runningText = trayProvider.getText("tray.status_on");
    String stoppedText = trayProvider.getText("tray.status_off");

    if (appService.isRunning()) {
      statusItem.setLabel(statusText + ": " + runningText);
    } else {
      statusItem.setLabel(statusText + ": " + stoppedText);
    }
  }

  private void updateStatusIcon() {
    URL iconOnUrl = getClass().getResource(iconTrayOnPath);
    URL iconOffUrl = getClass().getResource(iconTrayOffPath);

    if (appService.isRunning()) {
      updateIcon(iconOnUrl, "ON");
    } else {
      updateIcon(iconOffUrl, "OFF");
    }
  }

  private void updateIcon(URL iconUrl, String status) {
    if (iconUrl != null) {
      trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
    } else {
      log.error("Tray icon {} not found", status);
    }
  }

  private void checkSupported() {
    if (!SystemTray.isSupported()) {
      log.error("SystemTray not supported on this platform");
      return;
    }
  }
}
