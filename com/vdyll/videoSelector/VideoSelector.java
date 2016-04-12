package com.vdyll.videoSelector;

import java.util.Iterator;

import javax.swing.JOptionPane;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.Switch;

public class VideoSelector {

    // handle all arguments using the JSAP libraries
    public static JSAPResult handleArgs(JSAP jsap, String filter, String defaultPath,
                                        String[] args) throws JSAPException {

        final Switch opt1 = new Switch("help")
            .setShortFlag('h')
            .setLongFlag("help");
        opt1.setHelp("Displays this information");
        jsap.registerParameter(opt1);

        final Switch opt2 = new Switch("debug")
            .setShortFlag('d')
            .setLongFlag("debug");
        opt2.setHelp("Requests debug output.");
        jsap.registerParameter(opt2);

        final FlaggedOption opt3 = new FlaggedOption("videoPlayer")
            .setStringParser(JSAP.STRING_PARSER)
            .setDefault("vlc")
            .setRequired(true)
            .setShortFlag('v')
            .setLongFlag("video-player");
        opt3.setHelp("The command to launch videos with.");
        jsap.registerParameter(opt3);

        final QualifiedSwitch opt4 = (QualifiedSwitch) (new QualifiedSwitch("filter")
            .setStringParser(JSAP.STRING_PARSER)
            .setDefault(filter)
            .setShortFlag('f')
            .setLongFlag("filter")
            .setList(true)
            .setListSeparator(','));
        opt4.setHelp("Only find specified video formats");
        jsap.registerParameter(opt4);

        final FlaggedOption opt5 = new FlaggedOption("path")
            .setStringParser(JSAP.STRING_PARSER)
            .setDefault(defaultPath)
            .setRequired(true)
            .setShortFlag('p')
            .setLongFlag("path");
        opt5.setHelp("Specify the folder in which to search for videos");
        jsap.registerParameter(opt5);

        final JSAPResult config = jsap.parse(args);

        // if there was an error with parameters, show error message and exit
        if (!config.success()) {
            String errString = "\n";
            for (final Iterator<?> errs = config.getErrorMessageIterator(); errs.hasNext();) {
                errString += "Error: " + errs.next() + "\n";
            }
            errString += "\n " + jsap.getUsage() + "\n\n";
            errString += jsap.getHelp();
            help(errString, true);
        }

        // if help is selected, only show help dialog and exit
        if (config.getBoolean("help")) {
            help("\n " + jsap.getUsage() + "\n\n" + jsap.getHelp(), false);
        }

        return config;
    }

    // shows help or error message and exits
    public static void help(String errString, boolean error) {
        if (error) {
            System.err.println(errString);
        }
        JOptionPane.showMessageDialog(null, errString);
        System.exit(1);
    }

    // main method
    public static void main(String[] args) throws JSAPException, InterruptedException {

        // init objs
        final String title = "Video Selector";
        String[] filterArray = null;

        // path to choose videos from
        String path = null;

        // only pick files with these extensions
        final String filter = ".3gp,.amv,.asf,.avi,.divx,.flv,.gom,.h246,.mkv,.mmv,.mnv,.mov,"
                            + ".mp4,.mpeg,.mpeg4,.mpg,.mpg2,.ogm,.ogv,.ogx,.pmf,.qt,.swf,.tp,.ts,"
                            + ".vid,.wm,.wmv,.wmv,.wmx,.divx,.vob";

        // windows or linux? no macs here...
        String os = System.getProperty("os.name");
        final String slash = (os != null ? System.getProperty("file.separator") : "\\");

        // get home dir for defaulting local variable "path"
        final String home = System.getProperty("user.home");

        // use win vista or later, or linux for default
        String defaultPath = (home != null ? home + slash + "Videos" : "Videos");

        // set default path for specific OS's, no macs here...
        if (os != null) {
            os = os.toLowerCase();
            if (os.indexOf("windows") != -1) {
                // On Windows 98 and Windows Me - C:\My Documents\My Videos
                if (os.equals("windows 98") || os.equals("windows ME")) {
                    defaultPath = "C:\\My Documents\\My Videos";
                    // On Windows 2000 and Windows XP - %USERPROFILE%\My Documents\My Videos
                } else if (os.equals("windows xp") || os.equals("windows 2000")) {
                    defaultPath = (home != null ? home + slash + "My Documents" + slash
                        + "My Videos" : "My Videos");
                }
            }
        }

        boolean debug = false;

        // continue variable, used for cycling through videos
        final int cont = 0;

        // used if list empties; ask to try again
        int again = 1;

        // handle args
        final JSAP jsap = new JSAP();
        final JSAPResult config = handleArgs(jsap, filter, defaultPath, args);

        debug = config.getBoolean("debug");
        path = config.getString("path");
        if (config.getStringArray("filter").length > 0) {
            filterArray = config.getStringArray("filter");
        }

        // main
        do {
            // Pick a random video and ask to play it
            final VideoLauncher videoLauncher = new VideoLauncher(path, debug, filterArray, cont,
                config.getString("videoPlayer"), title);
            again = videoLauncher.launch();
            if (again == 2) {
                again = JOptionPane.showConfirmDialog(null, "Start Again?", title,
                    JOptionPane.YES_NO_OPTION);
            }
            // if list empties, ask to try again
        } while (again == 0);
    }
}
