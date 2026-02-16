package be.cypherke.mua;

import be.cypherke.mua.db.TeleportsDb;
import be.cypherke.mua.db.UsersDb;
import be.cypherke.mua.io.IrcListener;
import be.cypherke.mua.io.LocalFileManager;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Scanner;
import org.apache.commons.io.input.Tailer;

/** Main application that wraps a Minecraft server and adds chat commands and IRC bridging. */
public class Mua {

  private UsersDb usersDb;
  private Output output;
  private TeleportsDb teleportsDb;
  private Scheduler scheduler;

  private PrintWriter ircwriter;
  private boolean isIrcActive = false;
  private boolean ircBridgeActive = false;

  private Mua() throws IOException {
    Config config = new Config("mua", "xml");
    usersDb = new UsersDb(new LocalFileManager(config.getString("mua_usersfile")));
    teleportsDb = new TeleportsDb(new LocalFileManager(config.getString("mua_teleportsfile")));
    scheduler = new Scheduler(this);

    File workDir = new File(config.getString("server_workdir"));
    if (!workDir.isDirectory()) {
      System.err.println("server_workdir does not exist: " + workDir.getAbsolutePath());
      System.exit(1);
    }

    File serverJar = new File(workDir, config.getString("server_jar"));
    if (!serverJar.isFile()) {
      System.err.println("server_jar not found: " + serverJar.getAbsolutePath());
      System.exit(1);
    }

    ProcessBuilder builder = new ProcessBuilder("java", "-Xmx4G", "-Xms4G",
        "-XX:+UseG1GC", "-XX:ParallelGCThreads=2", "-jar",
        config.getString("server_jar"), "nogui");
    builder.directory(workDir);

    Process process = builder.start();

    Scanner scan = new Scanner(System.in);

    OutputStream stdout = process.getOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdout));

    output = new Output(writer);

    if (!config.getString("irc_channel_file").equals("none")) {
      isIrcActive = true;

      // Open writer to (ii irc bot) pipe
      ircwriter = new PrintWriter(new BufferedOutputStream(
          new FileOutputStream(config.getString("irc_channel_file") + "/in", true)));

      // Open reader from (ii irc bot) output file
      IrcListener ircListener = new IrcListener();
      ircListener.setMua(this);
      File ircOutput = new File(config.getString("irc_channel_file") + "/out");
      Tailer ircTailer = Tailer.builder()
          .setFile(ircOutput)
          .setTailerListener(ircListener)
          .setDelayDuration(Duration.ofMillis(500))
          .setTailFromEnd(true)
          .get();

      Thread ircReaderThread = new Thread(ircTailer);
      ircReaderThread.start();
    }

    Thread inputThread = new Thread(() -> {
      while (true) {
        String input = scan.nextLine();
        input += "\n";
        try {
          writer.write(input);
          writer.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (input.equalsIgnoreCase("stop\n")) {
          usersDb.save();
          teleportsDb.save();
          try {
            process.waitFor();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          System.exit(0);
        }
      }
    });
    inputThread.start();

    InputStream stdin = process.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));

    InputStream stderr = process.getErrorStream();
    BufferedReader error = new BufferedReader(new InputStreamReader(stderr));

    Thread errorThread = new Thread(() -> {
      String line = null;
      try {
        line = error.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
      while (line != null) {
        System.out.println("Error: " + line);
        try {
          line = reader.readLine();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    errorThread.start();

    String line = reader.readLine();
    MessageHandler messageHandler = new MessageHandler(this);
    while (line != null && !line.trim().equals("stop")) {
      System.out.println(line);
      messageHandler.dispatch(line);
      line = reader.readLine();
    }
  }

  /**
   * Main entry point to program.
   *
   * @param args the arguments for the program
   */
  public static void main(final String[] args) {
    try {
      new Mua();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public UsersDb getUsersDb() {
    return this.usersDb;
  }

  public TeleportsDb getTeleportsDb() {
    return this.teleportsDb;
  }

  public Output getOutput() {
    return output;
  }

  /**
   * Sends a message to the IRC channel.
   *
   * @param msg the message to send
   */
  public void printToIrc(String msg) {
    if (!isIrcActive) {
      return;
    }

    ircwriter.println("[MineCraft Server] " + msg);
    ircwriter.flush();
  }

  public boolean isIrcBridgeActive() {
    return this.ircBridgeActive;
  }

  public void setIrcBridgeActive(boolean active) {
    this.ircBridgeActive = active;
  }
}
