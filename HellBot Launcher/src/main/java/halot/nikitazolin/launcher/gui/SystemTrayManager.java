package halot.nikitazolin.launcher.gui;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.ApplicationRunnerImpl;
import halot.nikitazolin.launcher.app.AppService;
import halot.nikitazolin.launcher.localization.gui.tray.TrayProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SystemTrayManager {

  private final AppService appService;
  private final TrayProvider trayProvider;

  private final String appName = ApplicationRunnerImpl.APP_NAME;
  private final String icoPath = ApplicationRunnerImpl.APP_ICON_PATH;

  public void makeTray(JFrame frame) {
    checkSupported();

    PopupMenu popupMenu = new PopupMenu();

    String openText = trayProvider.getText("tray.open");
    String startText = trayProvider.getText("tray.start");
    String stopText = trayProvider.getText("tray.stop");
    String shutdownText = trayProvider.getText("tray.shutdown");

    MenuItem openItem = new MenuItem(openText);
    MenuItem statusItem = new MenuItem();
    MenuItem startItem = new MenuItem(startText);
    MenuItem stopItem = new MenuItem(stopText);
    MenuItem shutdownItem = new MenuItem(shutdownText);

    statusItem.setEnabled(false);
    updateStatusItem(statusItem);

    popupMenu.add(openItem);
    popupMenu.addSeparator();
    popupMenu.add(statusItem);
    popupMenu.add(startItem);
    popupMenu.add(stopItem);
    popupMenu.addSeparator();
    popupMenu.add(shutdownItem);

    openItem.addActionListener(e -> {
      frame.setVisible(true);
      frame.setState(Frame.NORMAL);
      log.debug("Window {} opened from tray", appName);
    });

    startItem.addActionListener(e -> {
      appService.start();
      log.debug("Application started from tray");
      updateStatusItem(statusItem);
    });

    stopItem.addActionListener(e -> {
      appService.stop();
      log.debug("Application stopped from tray");
      updateStatusItem(statusItem);
    });

    shutdownItem.addActionListener(e -> {
      log.info("Shutting down the application {}", appName);
      System.exit(0);
    });

    Image image = Toolkit.getDefaultToolkit().getImage(icoPath);
    TrayIcon trayIcon = new TrayIcon(image, appName);
    trayIcon.setImageAutoSize(true);
    trayIcon.setPopupMenu(popupMenu);

    try {
      SystemTray.getSystemTray().add(trayIcon);
      log.debug("Application icon {} added to system tray", appName);
    } catch (AWTException e) {
      log.error("Failed to add icon to system tray", e);
    }

    trayIcon.addActionListener(e -> {
      frame.setVisible(true);
      frame.setState(Frame.NORMAL);
      log.debug("Window {} opened from tray", appName);
    });
  }

  public void hideWindow(JFrame frame) {
    frame.setVisible(false);
    log.debug("Window {} hide to tray", appName);
  }

  private void updateStatusItem(MenuItem statusItem) {
    String statusText = trayProvider.getText("tray.status");
    String runningText = trayProvider.getText("tray.status_on");
    String stoppedText = trayProvider.getText("tray.status_off");

    if (appService.isRunning()) {
      statusItem.setLabel(statusText + ": " + runningText);
    } else {
      statusItem.setLabel(statusText + ": " + stoppedText);
    }
  }

  private void checkSupported() {
    if (!SystemTray.isSupported()) {
      log.error("SystemTray not supported on this platform");
      return;
    }
  }
}
