package halot.nikitazolin.launcher.gui.tab;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatusTab {

  public JPanel makeTab() {
    JPanel statusPanel = new JPanel();
    statusPanel.setLayout(new FlowLayout());
    JLabel statusLabel = new JLabel("Бот не запущен");
    JButton startButton = new JButton("Запустить");
    JButton stopButton = new JButton("Остановить");
    JCheckBox startupCheckBox = new JCheckBox("Запускать при загрузке Windows");

    statusPanel.add(statusLabel);
    statusPanel.add(startButton);
    statusPanel.add(stopButton);
    statusPanel.add(startupCheckBox);

    return statusPanel;
  }
}
