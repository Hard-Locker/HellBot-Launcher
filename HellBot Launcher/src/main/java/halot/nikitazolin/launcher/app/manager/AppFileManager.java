package halot.nikitazolin.launcher.app.manager;

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
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import halot.nikitazolin.launcher.localization.app.manager.ManagerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppFileManager {

  private final ManagerProvider managerProvider;
  private final OkHttpClient client = new OkHttpClient();

  /**
   * Loads the application JAR file. If the download flag is true, the method
   * downloads the latest version from GitHub. Otherwise, it prompts the user to
   * select a local JAR file.
   *
   * @param download     boolean flag indicating whether to download or select a
   *                     local file
   * @param appDirectory Path to the directory where the JAR file will be loaded
   */
  public void loadAppJar(boolean download, Path appDirectory) {
    try {
      if (download) {
        downloadJarFromGithub(appDirectory);
      } else {
        selectLocalJarFile(appDirectory);
      }
    } catch (Exception e) {
      log.error("Error while loading app jar file", e);
      String messageText = managerProvider.getText("file_manager.load_message_fail");
      JOptionPane.showMessageDialog(null, messageText + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Searches for a JAR file in the specified directory whose name contains the
   * given file name and ends with ".jar".
   *
   * @param fileName      String representing the main part of the file name to
   *                      search for
   * @param directoryPath Path to the directory where the search should be
   *                      conducted
   * @return Optional<String> containing the absolute path of the found file, or
   *         empty if no file was found
   */
  public Optional<String> findJarFileAbsolutePath(String fileName, Path directoryPath) {
    try (Stream<Path> paths = Files.walk(directoryPath)) {
      return paths.filter(Files::isRegularFile)
          .map(Path::toFile)
          .filter(file -> file.getName().contains(fileName) && file.getName().endsWith(".jar"))
          .map(File::getAbsolutePath)
          .findFirst();
    } catch (IOException e) {
      log.error("Error searching for jar files in directory {}: {}", directoryPath, e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Deletes the specified JAR file if it exists and is a valid JAR file.
   *
   * @param jarFilePath Absolute path to the JAR file to delete
   * @return true if the file was successfully deleted, false otherwise
   */
  public boolean deleteAppJarFile(Path jarFilePath) {
    try {
      if (Files.exists(jarFilePath) && jarFilePath.toString().toLowerCase().endsWith(".jar")) {
        Files.delete(jarFilePath);
        log.info("Deleted app jar file: {}", jarFilePath.toString());
        return true;
      } else {
        log.warn("File does not exist or is not a JAR file: {}", jarFilePath.toString());
        return false;
      }
    } catch (IOException e) {
      log.error("Error occurred while deleting app jar file", e);
      return false;
    }
  }

  /**
   * Downloads the latest release of the app JAR file from the GitHub repository.
   * The downloaded file is saved to the specified app directory.
   *
   * @param appDirectory Path to the directory where the JAR file will be saved
   */
  public void downloadJarFromGithub(Path appDirectory) {
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

    String name = managerProvider.getText("file_manager.download_name_frame");
    JFrame frame = new JFrame(name);
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
      Files.createDirectories(appDirectory);

      Path destinationPath = appDirectory.resolve(appName);
      File outputFile = destinationPath.toFile();

      log.info("Destination path: {}", destinationPath.toString());
      log.info("Downloading app jar file from GitHub...");

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

          if (fileLength > 0) {
            int progress = (int) (((double) totalBytesRead / fileLength) * 100);
            progressBar.setValue(progress);
          }
        }

        log.info("File saved to: {}", outputFile.getAbsolutePath());
        log.info("Downloaded app jar file successfully.");
        String messageOkText = managerProvider.getText("file_manager.download_message_ok");
        JOptionPane.showMessageDialog(null, messageOkText, "Success", JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception e) {
      log.error("Error occurred while downloading app jar file", e);
      String messageFailText = managerProvider.getText("file_manager.download_message_fail");
      JOptionPane.showMessageDialog(null, messageFailText + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
      frame.dispose();
    }
  }

  /**
   * Prompts the user to select a local JAR file and copies it to the specified
   * app directory.
   *
   * @param appDirectory Path to the directory where the selected JAR file will be
   *                     copied
   */
  public void selectLocalJarFile(Path appDirectory) {
    JFileChooser fileChooser = new JFileChooser();
    String name = managerProvider.getText("file_manager.select_name_frame");
    fileChooser.setDialogTitle(name);

    FileNameExtensionFilter filter = new FileNameExtensionFilter("JAR Files", "jar");
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showOpenDialog(null);
    if (result == JFileChooser.APPROVE_OPTION) {
      Path selectedFile = fileChooser.getSelectedFile().toPath();

      if (!selectedFile.toString().endsWith(".jar")) {
        log.error("Selected file is not a JAR file.");
        String messageText = managerProvider.getText("file_manager.select_message");
        JOptionPane.showMessageDialog(null, messageText, "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      log.info("Selected local app jar file: {}", selectedFile.toString());

      try {
        Files.createDirectories(appDirectory);
        Files.copy(selectedFile, appDirectory.resolve(selectedFile.getFileName()),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        log.info("Copied app jar file to: {}", appDirectory.resolve(selectedFile.getFileName()).toString());
        String messageOkText = managerProvider.getText("file_manager.select_message_ok");
        JOptionPane.showMessageDialog(null, messageOkText, "Success", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        log.error("Error occurred while copying app jar file", e);
        String messageFailText = managerProvider.getText("file_manager.select_message_fail");
        JOptionPane.showMessageDialog(null, messageFailText + ": " + e.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Fetches the latest version number of the app from the GitHub API.
   *
   * @return Optional<String> containing the latest version number, or empty if
   *         the version could not be retrieved
   */
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
