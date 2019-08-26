package com.abledenthusiast.heracross.packager.commands;

/*  
*   example packager command from Shaka Packager
    packager input=/media/example.mp4,stream=audio,output=/media/audio.mp4 \
           input=/media/example.mp4,stream=video,output=/media/video.mp4 \
           --mpd_output /media/example.mpd
*/
public class PackageCommand extends Command {

    private StringBuilder stringBuilder;
    private String homeDir;


    public PackageCommand(String homeDir) {
        this.homeDir = homeDir;
    }

    public void buildProc() {
        ProcessBuilder builder = new ProcessBuilder();
    }

    public String toExec() {
        // return "packager input=" + fileDir + ",stream=audio,output=" + fileDir  + "/audio.mp4 \\"
        // + ""
        // + "";
        return "packager input=/media/example.mp4,stream=audio,output=/media/audio.mp4 \\"
         + "input=/media/example.mp4,stream=video,output=/media/video.mp4 \\"
        + "--mpd_output /media/example.mpd";
    }

    public static PackageCommandBuilder packageCommandBuilder() {
        return new PackageCommandBuilder();
    }

    private static class PackageCommandBuilder {
        private StringBuilder stringBuilder;
        private String inputFile;
        private String outputFile;

        // public PackageCommand build() {
        //     stringBuilder.append("packager input=");
        //     stringBuilder.append(this.inputFile);
        // }
    }

}