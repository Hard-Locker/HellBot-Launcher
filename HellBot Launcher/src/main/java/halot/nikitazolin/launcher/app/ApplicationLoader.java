package halot.nikitazolin.launcher.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationLoader {

  private final OkHttpClient client = new OkHttpClient();
  private final Path botDirectory = Paths.get("apps");

  public void loadBotJar(boolean download) {
    try {
      if (download) {
        downloadJarFromGithub();
      } else {
        selectLocalJarFile();
      }
    } catch (Exception e) {
      log.error("Error while loading bot jar file", e);
      JOptionPane.showMessageDialog(null, "Failed to load the bot jar file: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void downloadJarFromGithub() {
    log.info("Fetching latest release version from GitHub...");
    Optional<String> latestVersion = getNumberLatestVersion();

    if (latestVersion.isEmpty()) {
      log.error("Failed to retrieve the latest version from GitHub.");
      return;
    }

    String appName = "hell-bot-" + latestVersion.get() + ".jar";
    String urlLatestVersion = latestVersion.get() + "/" + appName;
    String downloadUrl = "https://github.com/Hard-Locker/HellBot/releases/download/" + urlLatestVersion;
    log.info("Jar file URL: {}", downloadUrl);

    // Создание окна с прогресс баром
    JFrame frame = new JFrame("Downloading Bot");
    JProgressBar progressBar = new JProgressBar(0, 100);
    progressBar.setValue(0);
    progressBar.setStringPainted(true);
    frame.add(progressBar);
    frame.setSize(300, 75);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);

    try {
      URI uri = new URI(downloadUrl);
      URL url = uri.toURL();

      log.info("Creating directories...");
      Files.createDirectories(botDirectory);

      Path destinationPath = botDirectory.resolve(appName);
      File outputFile = destinationPath.toFile();

      log.info("Destination path: {}", destinationPath.toString());
      log.info("Downloading bot jar file from GitHub...");

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int fileLength = connection.getContentLength();

      try (InputStream inputStream = connection.getInputStream();
          FileOutputStream outputStream = new FileOutputStream(outputFile)) {

        byte[] buffer = new byte[4096];
        int bytesRead;
        int totalBytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
          totalBytesRead += bytesRead;

          // Обновление прогресс бара
          if (fileLength > 0) {
            int progress = (int) (((double) totalBytesRead / fileLength) * 100);
            progressBar.setValue(progress);
          }
        }

        log.info("File saved to: {}", outputFile.getAbsolutePath());
        log.info("Downloaded bot jar file successfully.");
        JOptionPane.showMessageDialog(null, "Bot jar file downloaded successfully.", "Success",
            JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception e) {
      log.error("Error occurred while downloading bot jar file", e);
      JOptionPane.showMessageDialog(null, "Failed to download the bot jar file: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    } finally {
      frame.dispose(); // Закрываем окно прогресс бара после завершения загрузки
    }
  }

  private void selectLocalJarFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Bot Jar File");

    // Ограничиваем выбор только файлами с расширением .jar
    FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR Files", "jar");
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      Path selectedFile = fileChooser.getSelectedFile().toPath();

      // Проверяем, действительно ли выбранный файл имеет расширение .jar
      if (!selectedFile.toString().endsWith(".jar")) {
        log.error("Selected file is not a JAR file.");
        JOptionPane.showMessageDialog(null, "Please select a valid JAR file.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      log.info("Selected local bot jar file: {}", selectedFile.toString());

      try {
        Files.createDirectories(botDirectory);
        Files.copy(selectedFile, botDirectory.resolve(selectedFile.getFileName()),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        log.info("Copied bot jar file to: {}", botDirectory.resolve(selectedFile.getFileName()).toString());
        JOptionPane.showMessageDialog(null, "Bot jar file selected successfully.", "Success",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        log.error("Error occurred while copying bot jar file", e);
        JOptionPane.showMessageDialog(null, "Failed to copy the bot jar file: " + e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public Optional<String> getNumberLatestVersion() {
    String url = "https://api.github.com/repos/Hard-Locker/HellBot/releases/latest";
    Request request = new Request.Builder().url(url).get().build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Failed to fetch latest version: HTTP {}", response.code());
        return Optional.empty();
      }

      try (ResponseBody body = response.body()) {
        if (body != null) {
          try (Reader reader = body.charStream()) {
            JSONObject object = new JSONObject(new JSONTokener(reader));
            return Optional.ofNullable(object.getString("tag_name"));
          }
        } else {
          log.error("Response body is null");
          return Optional.empty();
        }
      }
    } catch (IOException | JSONException ex) {
      log.error("Exception occurred while fetching latest version", ex);
      return Optional.empty();
    }
  }
}
