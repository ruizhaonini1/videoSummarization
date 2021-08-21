package summarizer.utilities;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Collections;
import java.util.List;

public class SoundUtil {
    public static AudioInputStream readAudioInputStream(String sourceFileName) {
        InputStream waveStream = null;

        try {
            waveStream = new FileInputStream(sourceFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AudioInputStream audioInputStream = null;

        try {
            InputStream bufferedIn = new BufferedInputStream(waveStream);
            audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        return audioInputStream;
    }

    public static AudioInputStream trim(String sourceFileName, long startFemtoSecond, long femtoSecondsToCopy) {
        AudioInputStream inputStream;
        AudioInputStream shortenedStream = null;

        try {
            File file = new File(sourceFileName);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(file);
            int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();
            inputStream.skip(Math.round(startFemtoSecond / 1000000000000000.0) * bytesPerSecond);
            long framesOfAudioToCopy = Math.round(femtoSecondsToCopy / 1000000000000000.0) * (long) format.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shortenedStream;
    }

    public static AudioInputStream combine(List<AudioInputStream> audioInputStreamList) {
        long totalLen = 0;

        for (AudioInputStream audioInputStream : audioInputStreamList) {
            totalLen += audioInputStream.getFrameLength();
        }

        if (totalLen <= 0) {
            return null;
        }

        return new AudioInputStream(
                new SequenceInputStream(Collections.enumeration(audioInputStreamList)),
                    audioInputStreamList.get(0).getFormat(), totalLen);
    }
}