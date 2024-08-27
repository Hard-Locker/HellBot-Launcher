package halot.nikitazolin.launcher.gui.tab;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.app.ApplicationLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationTab {

  private final ApplicationLoader applicationLoader;

  public JPanel makeTab() {
    JPanel appPanel = new JPanel();
    appPanel.setLayout(new FlowLayout());
    JLabel currentPathLabel = new JLabel("Текущее расположение: <путь к файлу>");
    JButton downloadButton = new JButton("Скачать");
    JButton updateButton = new JButton("Обновить");
    JButton selectPathButton = new JButton("Выбрать путь");

    appPanel.add(currentPathLabel);
    appPanel.add(downloadButton);
    appPanel.add(updateButton);
    appPanel.add(selectPathButton);

    return appPanel;
  }
}
