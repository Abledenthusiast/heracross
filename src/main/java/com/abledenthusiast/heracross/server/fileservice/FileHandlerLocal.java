package com.abledenthusiast.heracross.server.fileservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;
import javax.print.attribute.standard.Media;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.abledenthusiast.heracross.server.fileservice.dto.MediaDTO;
import com.abledenthusiast.heracross.server.media.library.Library;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;
import com.sun.net.httpserver.Authenticator.Result;

import org.springframework.web.multipart.MultipartFile;

public class FileHandlerLocal implements FileHandler {
  private Path defaultDir;
  DbConnector db;

  public FileHandlerLocal(Path projectRoot) throws SQLException {
    this.defaultDir = projectRoot;
    // create a database connection

    db = DbConnector.ofDefaultConnection();
  }

  @Override
  public boolean writeFile(InputStream in, Path targetLocation) {
    Objects.requireNonNull(in);
    Objects.requireNonNull(targetLocation);

    boolean result = false;
    createDirectory(targetLocation);
    try {
      Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
      result = true;
    } catch (Exception e) {
      System.out.printf("error when writing file %s", e);
      result = false;
    }

    return result;
  }

  /*
   * checks if the directory already exists if it does, the library needs to be
   * initialized
   */
  public void initDirectory(Path dir) {
    try {
      Files.createDirectories(dir);
    } catch (Exception e) {
      System.out.printf("error when creating directory %s", e);
    }
  }

  /*
   * persist the map to a log file. The file wil be opened and then closed via
   * this method. This should be fine temporarily, since uploading a video should
   * be a fairly rare task anyway Should upload become a pain point, this method
   * should be checked.
   */
  // public void persist() throws IOException {
  // try(BufferedWriter writer = Files.newBufferedWriter(persistFile,
  // StandardOpenOption.WRITE,
  // StandardOpenOption.APPEND)) {
  // hashMap.entrySet().stream().filter(isValid).forEach(entry -> {
  // try {
  // writer.append(entry.getKey() +":"+entry.getValue());
  // } catch(IOException e) {

  // }
  // });
  // }
  // }

  @Override
  public boolean createDirectory(Path directory) {
    if (Files.isDirectory(directory)) {
      return true;
    }
    try {
      Files.createDirectories(directory);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean isDirectory(Path directory) {
    return Files.isDirectory(directory);
  }

  @Override
  public File getFile(Path directory) {
    return directory.toFile();
  }

  public File[] getFiles(Path directory) {
    // @see Files.newDirectoryStream
    return directory.toFile().listFiles();
  }

  @Override
  public List<MediaDTO> loadMedia() throws Exception {
    return db.selectAll();
  }
  
  public void loadMedia(Consumer<List<MediaDTO>> loader) throws Exception {
    List<MediaDTO> loadedMedia = db.selectAll();
    loader.accept(loadedMedia);
  }

  @Override
  public void writeLog(MediaDTO dto) throws Exception {
    db.insert.setString(LibraryCol.FileName.columnIndex(), dto.fileName());
    db.insert.setString(LibraryCol.MediaFileType.columnIndex(), dto.mediaFileType().toString());
    db.insert.setString(LibraryCol.FilePath.columnIndex(), dto.filePath().toString());
    db.insert.setString(LibraryCol.SeriesName.columnIndex(), dto.seriesName().toString());
    db.insert.setString(LibraryCol.ContentType.columnIndex(), dto.contentType().toString());

    db.insert.execute();
  }

  private static class DbConnector {
    // private Connection connection;
    PreparedStatement insert = null;
    PreparedStatement select = null;
    PreparedStatement selectAll = null;
    PreparedStatement update = null;
    PreparedStatement delete = null;
    Connection connection = null;

    public DbConnector(Connection connection) {
      this.connection = connection;
    }

    public ResultSet execute(PreparedStatement statement) throws SQLException {
      // updateSales.setInt(1, e.getValue().intValue());
      // updateSales.setString(2, e.getKey());
      return statement.executeQuery();
    }

    public Statement createStatement() throws SQLException {
      return connection.createStatement();
    }

    public static DbConnector ofDefaultConnection() throws SQLException {
      DbConnector connector = new DbConnector(connect()).createTable().prepareStatements();
      return connector;
    }

    private static Connection connect() throws SQLException {
      String protocol = "jdbc:derby:";
      return DriverManager.getConnection(protocol + "/Users/abledenthusiast/Development/heracross/derbyDB;create=true");
    }

    public List<MediaDTO> selectAll() throws SQLException, OperationNotSupportedException {
      List<MediaDTO> media = new ArrayList<>();
      ResultSet results = selectAll.executeQuery();
      while (results.next()) {
        String fileName = results.getString("FILE_NAME");
        String mediaFileType = results.getString("MEDIA_FILE_TYPE");
        String contentType = results.getString("CONTENT_TYPE");
        String filePath = results.getString("FILE_PATH");
        String seriesName = results.getString("SERIES_NAME");

        MediaDTO dto = MediaDTO.newMediaDTOBuilder().filePath(Path.of(filePath)).fileName(fileName)
            .mediaFileType(MediaFileType.valueOf(mediaFileType))
            .contentType(contentType)
            .seriesName(seriesName)
            .build();
        media.add(dto);
      }
      return media;
    }

    public void close() throws SQLException {
      connection.close();
    }

    private DbConnector prepareStatements() throws SQLException {
      insert = connection.prepareStatement("INSERT INTO MEDIA_LIBRARY VALUES (?, ?, ?, ?, ?)");
      select = connection.prepareStatement("SELECT * FROM MEDIA_LIBRARY WHERE FILE_NAME = ?");
      selectAll = connection.prepareStatement("SELECT * FROM MEDIA_LIBRARY");
      // update = connection.prepareStatement("UPDATE EMPLOYEES SET SALARY = ? WHERE
      // ID = ?");
      // delete = connection.prepareStatement("UPDATE EMPLOYEES SET SALARY = ? WHERE
      // ID = ?");
      return this;
    }

    private DbConnector createTable() throws SQLException {
      try {
        Statement stmt = connection.createStatement();
        String query = "CREATE TABLE MEDIA_LIBRARY( "
          + "FILE_NAME VARCHAR(255) NOT NULL, "
          + "MEDIA_FILE_TYPE VARCHAR(255), "
          + "FILE_PATH VARCHAR(255), "
          + "SERIES_NAME VARCHAR(255), " 
          + "CONTENT_TYPE VARCHAR(255))";
          
        stmt.execute(query);
      } catch(SQLException sqlExc) {
        // if exception is table already exists, swallow it.
        if(sqlExc.getSQLState().equals("X0Y32")) {
        } else {
          throw new SQLException(sqlExc);
        }
      }
      return this;
    }
  }

  private enum LibraryCol {
    FileName(1),
    MediaFileType(2),
    FilePath(3),
    SeriesName(4),
    ContentType(5);

    private final int value;

    LibraryCol(int value) {
      this.value = value;
    }

    public int columnIndex() {
      return value;
    }

    
  }

}