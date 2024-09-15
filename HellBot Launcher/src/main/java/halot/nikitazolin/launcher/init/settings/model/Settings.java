package halot.nikitazolin.launcher.init.settings.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope("singleton")
@Data
public class Settings {

  private String customAppName = "HellBot";
  private boolean showCustomAppName = true;
  private boolean showInTray = true;
  private boolean hideToTrayOnClose = true;
  private boolean autostartLauncher = true;
  private boolean autostartApp = true;
}
