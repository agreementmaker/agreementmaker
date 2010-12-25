package am.userInterface;

import java.io.File; 
import java.io.IOException; 
import javax.sound.sampled.AudioFormat; 
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.DataLine; 
import javax.sound.sampled.FloatControl; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.SourceDataLine; 
import javax.sound.sampled.UnsupportedAudioFileException; 

import am.app.Core;
 
public class SoundNotification extends Thread { 
 
    private String filename;
 
    private Position curPosition = Position.NORMAL;
 
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb 
 
    enum Position { 
        LEFT, RIGHT, NORMAL
    };
 
    public SoundNotification(String wavfile) { 
        filename = wavfile;
        curPosition = Position.NORMAL;
    } 
 
    public SoundNotification(String wavfile, Position p) { 
        filename = wavfile;
        curPosition = p;
    } 
 
    public void run() { 
 
        File soundFile = new File(filename);
        if (!soundFile.exists()) { 
            if(Core.DEBUG) System.err.println("Wave file not found: " + filename);
            return;
        } 
 
        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) { 
            e1.printStackTrace();
            return;
        } catch (IOException e1) { 
            e1.printStackTrace();
            return;
        } 
 
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) { 
            e.printStackTrace();
            return;
        } catch (Exception e) { 
            e.printStackTrace();
            return;
        } 
 
        if (auline.isControlSupported(FloatControl.Type.PAN)) { 
            FloatControl pan = (FloatControl) auline
                    .getControl(FloatControl.Type.PAN);
/*            if (curPosition == Position.RIGHT) 
                pan.setValue(1.0f);
            else if (curPosition == Position.LEFT) 
                pan.setValue(-1.0f);*/
            pan.setValue(0.0f);
        } 
        if( auline.isControlSupported(FloatControl.Type.BALANCE)) {
        	FloatControl bal = (FloatControl) auline.getControl(FloatControl.Type.BALANCE);
        	bal.setValue(0.0f);
        }
        if( auline.isControlSupported(FloatControl.Type.REVERB_SEND)) {
        	FloatControl bal = (FloatControl) auline.getControl(FloatControl.Type.REVERB_SEND);
        	bal.setValue(0.0f);
        }
        
        if( auline.isControlSupported(FloatControl.Type.REVERB_RETURN)) {
        	FloatControl bal = (FloatControl) auline.getControl(FloatControl.Type.REVERB_RETURN);
        	bal.setValue(0.0f);
        }
 
        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
        try { 
            while (nBytesRead != -1) { 
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
            e.printStackTrace();
            return;
        } finally { 
            auline.drain();
            auline.close();
        } 
        
        
    } 
} 
