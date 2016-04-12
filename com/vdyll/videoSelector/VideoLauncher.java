package com.vdyll.videoSelector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import com.vdyll.utils.file.FileWalker;

public class VideoLauncher {

    private List<File>       fileList;
    private final String     path;
    private final boolean    debug;
    private final String[]   filterArray;
    private int              cont;
    private final String     videoPlayer;
    private final String     title;

    public VideoLauncher(String path, boolean debug, String[] filterArray, int cont,
                         String videoPlayer, String title) {
        this.fileList = new ArrayList<File>();
        this.path = path;
        this.debug = debug;
        this.filterArray = filterArray;
        this.cont = cont;
        this.videoPlayer = videoPlayer;
        this.title = title;
    }

    public int launch() throws InterruptedException {

        this.fileList = FileWalker.walk(this.path, this.debug, this.fileList, this.filterArray, true);

        final String[] options = { "Yes", "No", "Alternate Player" };
        final Random rand = new Random();

        do {
            if (this.fileList != null) {
                if (!this.fileList.isEmpty()) {
                    // select random movie
                    final File file = this.fileList.remove(rand.nextInt(this.fileList.size()));
                    if (file != null) {

                        if (this.debug) {
                            System.out.println(file.getAbsoluteFile());
                        }

                        final String message = "Watch this video?\n\n" + file.getName()
                            + "\n\nFound at:\n\n" + file.getParent() + "\n\n";

                        this.cont = JOptionPane.showConfirmDialog(null, message, this.title,
                            JOptionPane.YES_NO_OPTION);

                        // yes selected
                        if (this.cont == 0) {

                            // open video in specified player
                            final String[] cmd = { this.videoPlayer,
                                    file.getName() };

                            String altVideoPlayer = null;

                            do {
                                // trying a different video player
                                if (altVideoPlayer != null) {
                                    cmd[0] = altVideoPlayer;
                                    altVideoPlayer = null;
                                }

                                if (this.debug) {
                                    System.out.println(cmd[0] + " " + cmd[1]);
                                }

                                // try to open the new process and play the video
                                try {

                                    final Process process = Runtime.getRuntime().exec(cmd, null,
                                        file.getParentFile());
                                    process.waitFor();

                                } catch (final IOException e) {
                                    if (this.debug) {
                                        e.printStackTrace();
                                    }
                                    if (e.getMessage().contains("Cannot run program")) {
                                        JOptionPane.showMessageDialog(
                                            null,
                                            "Could not execute the player command.\nPlease ensure "
                                                + "the full path to the player has been input.\n"
                                                + "Example:\nC:\\VLC\\vlc.exe instead of just vlc",
                                            this.title,
                                            JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(
                                            null,
                                            "An unexpected error occured.\nThis is probably "
                                                + "because the file or player could not be found.\n"
                                                + "Ensure all paths are correct and try again.",
                                            this.title,
                                            JOptionPane.ERROR_MESSAGE);
                                    }
                                }

                                this.cont = JOptionPane.showOptionDialog(null,
                                    "Done watching movies?", this.title,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.YES_NO_CANCEL_OPTION,
                                    null, options, options[0]);

                                // Alt selected
                                if (this.cont == 2) {
                                    // allow an alternate video player to be chosen for same
                                    // video
                                    altVideoPlayer = JOptionPane.showInputDialog(null,
                                        "Input another video player to try", this.title,
                                        JOptionPane.QUESTION_MESSAGE);

                                    // cancel was selected or no played selected
                                    // default to the last player used
                                    if (altVideoPlayer == null) {
                                        altVideoPlayer = cmd[0];
                                    }

                                }
                            } while (altVideoPlayer != null);

                            // no selected
                        } else if (this.cont == 2) {
                            System.exit(0);
                        }
                    }
                } else {
                    return 2;
                }
            }

            // while no is selected from done watching movies dialog
        } while (this.cont == 1);

        return 1;
    }
}
