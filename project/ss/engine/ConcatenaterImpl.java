//Source file: C:\\project\\ss\\engine\\ConcatenaterImpl.java

package project.ss.engine;

import project.ss.misc.*;
import project.ss.exception.*;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.regex.*;

/**
 * This class provides the implementation of the Concatenater interface
 * The class owns a AudioInputStream targeting the Voice File which contains
 * audio data.The information related to the Voice File is stored in Voice Info
 * File
 * whose content is laoded in to the buffer for efficiency.
 * The class is basically designed to satisfy the need for concatenation of
 * various speech segments in Speech Synthesizing which uses the
 * Concatenation approach.
 * Partneme is the word used to denote the segment of speech.
 * Two separate files are used viz, Voice File and Voice Info File which works
 * in pair.
 * Voice File  :   For storing the segment audio or partneme.
 * This file can be any audio file whose format is supported
 * by Java Sound
 * Voice Info File : Since the audio file doesn't have labels , one must
 * provide a way to tell concatenater where a specific
 * sound segment can be found .Then only it can fetch
 * the  right segment from the Voice File. The Voice 
 * Info
 * File provides this information to concatenater.
 * It stores the partneme label , its position in the
 * voice
 * File and the lenght of the partneme
 * We can build the Voice File and Voice Info File pair using the  class
 * VoiceBuilder provided in the package ss.voice.See the description of
 * the VoiceBuilder class.
 */
public class ConcatenaterImpl implements Concatenater 
{
   
   /**
    *  The String in which the content of the voice file is laoded
    *  using buffer.Concatenter serches it to find the position of
    *  sound segment in Voice File
    */
   static String voiceInfoData = "";
   
   /**
    * It hold the no of bytes per sample in the Voice File
    */
   static int bytesPerSample = 2;
   
   /**
    * The audioInputStream responsible for holding audio data
    */
   static AudioInputStream currentAudioIS = null;
   
   /**
    * The RandomAccessFile for the audio file responsible for holding audio data
    */
   static RandomAccessFile currentAudioFile = null;
   
   /**
    * The File object representing th Voice File
    */
   static File voiceFile = null;
   
   /**
    * The File object representing th Voice Info File
    */
   static File voiceInfoFile = null;
   
   /**
    * Source Data Line where the segmented audio data is feed
    */
   static SourceDataLine SDLine = null;

   private static File dumpFile = null;
   
   /**
    * Constructs the object of this type with specified Voice and Voice Info File.
    * 
    * @param voiceFile
    * @param voiceInfoFile
    * @throws javax.sound.sampled.UnsupportedAudioFileException
    * @throws java.io.IOException
    * @roseuid 3AFF70F901D5
    */
   public ConcatenaterImpl(File voiceFile, File voiceInfoFile) throws UnsupportedAudioFileException, IOException 
   {
     try
     {
      setVoice (voiceFile , voiceInfoFile );
     }
    catch (UnsupportedAudioFileException e)
     {
      System.out.println("Unable to create ConcatenaterImpl() ");
      throw e;
     }
     catch (IOException e)
     {
      System.out.println("Unable to create ConcatenaterImpl() ");
      throw e;
     }    
   }
   
   /**
    * Called to wind up various resources in use
    * @roseuid 3AFF70F901C1
    */
   protected void finalized() 
   {
   voiceFile=null;
   voiceInfoFile=null;
   SDLine=null;
   voiceInfoData=null;
   try
    {
      currentAudioIS.close();
/*****************        */
      currentAudioFile.close();
    }
   catch (IOException e)
    {
     e.printStackTrace();
    }    
   }
   
   /**
    * This method sets the new Voice and Voice Info File. 
    * 
    * @param vcFile
    * @param vcInfoFile
    * @throws javax.sound.sampled.UnsupportedAudioFileException
    * @throws java.io.IOException
    * @roseuid 3AFF70F901EA
    */
   public void setVoice(File vcFile, File vcInfoFile) throws UnsupportedAudioFileException, IOException 
   {
    voiceFile = vcFile;
    voiceInfoFile = vcInfoFile;
    try
    {
     currentAudioIS  =  AudioSystem.getAudioInputStream(voiceFile);
/******************   inserted next line */
     currentAudioFile = new RandomAccessFile(voiceFile, "r");
     bytesPerSample = (currentAudioIS.getFormat().getSampleSizeInBits())/8;
     //voiceInfoData = FileLoad.getCharBufferRO(voiceInfoFile,"UTF-16BE").toString() ;
     System.out.println("new voice file selected");
     System.out.println("Frame length " +currentAudioIS.getFrameLength());
//   System.out.println("Voice Info Data \n"+voiceInfoData);
    }
    catch (UnsupportedAudioFileException e)
    {
     throw e;
    }
    catch (IOException e)
    {
     System.out.println("Unable to set new voice(files)");
     throw e;
    }    
   }
   
   /**
    * This method constructs a new SourceDataLine taking in account
    * the audio format of the Voice File in use
    * 
    * @param player
    * @throws javax.sound.sampled.LineUnavailableException
    * @roseuid 3B06A6620165
    */
   public void prepareSourceDataLine(Player player) throws LineUnavailableException 
   {
    try
     {
      DataLine.Info  dlInfo = new DataLine.Info(SourceDataLine.class,currentAudioIS.getFormat(),((int) currentAudioIS.getFrameLength() *  currentAudioIS.getFormat().getFrameSize()) );
      SDLine = (SourceDataLine) AudioSystem.getLine(dlInfo);
      SDLine.open(currentAudioIS.getFormat());
      player.setSourceDataLine(SDLine);
      player.play();
     }
      catch (LineUnavailableException e)
     {
        System.out.println("Unable to prepare SourceDataLine for output ");
        throw e;
     }    
   }
   
   /**
    * Finds the position of the partneme specified 
    * 
    * @param partneme
    * @return int
    * @throws project.ss.exception.GSSException
    * @roseuid 3B06A66201E7
    */
 /*  public int findPos(String partneme) throws GSSException 
   {
   try
    {
     System.out.println(" finding position of : " +partneme);
     Pattern pattern = Pattern.compile("(\\|" + partneme + "\\|);[0-9]*;[0-9]*;");
     //Matcher matcher = pattern.matcher(voiceInfoData);
     //matcher.find();
//     System.out.println(" start " + matcher.start());
//     System.out.println(" end " + matcher.end());
    // System.out.println(" Item selected " + matcher.group());
     //StringTokenizer st = new StringTokenizer (matcher.group(),";");
     //st.nextToken();
     //return   new Integer(st.nextToken()).intValue();
    }
    catch (Exception e )
    {
       System.out.println("Unable to find position of " + partneme);
       throw new GSSException (" Unable to find position " );
    }    
   }
   
   /**
    *  Finds the length of the partneme specified
    * 
    * @param partneme
    * @return int
    * @throws project.ss.exception.GSSException
    * @roseuid 3B06A6620255
    
   public int findLength(String partneme) throws GSSException 
   {
   try
    {
     System.out.println(" finding position of : " +partneme);
     Pattern pattern = Pattern.compile("(\\|" + partneme + "\\|);[0-9]*;[0-9]*;");
     Matcher matcher = pattern.matcher(voiceInfoData);
     matcher.find();
//     System.out.println(" start " + matcher.start());
//     System.out.println(" end " + matcher.end());
     System.out.println(" Item selected " + matcher.group());
     StringTokenizer st = new StringTokenizer (matcher.group(),";");
     st.nextToken();
     st.nextToken();
     return   new Integer(st.nextToken()).intValue();
    }
    catch (Exception e )
    {
       System.out.println("Unable to find length of " + partneme);
       throw new GSSException (" Unable to find length " );
    }    
   }*/
   
   /**
    *  Concatenates and supplies the partneme audio specified as partnem sequence
    *   to the Source Data Line under use.
    * 
    * WARNING: Make sure that call to this method  doesnt occur simeltaneously
    *                     currently the method is not thread safe.
    * 
    * @param partnemeString
    * @throws project.ss.exception.ImproperDataFeed
    * @roseuid 3B06A66202CE
    */
   public void concatenateAndFeed(String partnemeString) throws ImproperDataFeed 
   {
    /* boolean completeDataFeed=true;
     currentAudioIS.mark(((int) currentAudioIS.getFrameLength() *  currentAudioIS.getFormat().getFrameSize()));
     StringTokenizer st = new StringTokenizer (partnemeString,";") ;
     int toSkip; // position in voicefile at which the partneme starts
     int toRead; // length of perticular partneme
     int numBytesRead;
     byte[] partnemeData = null;
     ByteBuffer byteBuffer = ByteBuffer.allocate(100000); 
     for( int i=0 ; st.hasMoreTokens() ; i++) // put yor condi
     {
        String partneme = st.nextToken();
        System.out.println ( i + "------------------------------------- " + partneme );
        try
        {
//       System.out.println ( i + " avilable  " + currentAudioIS.available() );
           currentAudioIS.reset();
           try
           {
             toSkip = findPos(partneme)*bytesPerSample;
             currentAudioIS.skip(toSkip);
           }
           catch (GSSException e )
           {
             e.printStackTrace();
             toSkip = 0;
             completeDataFeed=false;
           }
           System.out.println ( i + " skipping  " + toSkip );
           try
           {
             toRead = findLength(partneme)*bytesPerSample;
           }
           catch (GSSException e )
           {
             e.printStackTrace();
             toRead = 0;
             completeDataFeed=false;                          
           }
           System.out.println ( i + " reading  " + toRead );
           partnemeData = new byte [toRead];
           numBytesRead =  currentAudioIS.read(partnemeData,0,toRead);
           System.out.println ( i + " writing  " + numBytesRead );
           SDLine.write(partnemeData, 0, numBytesRead);
           currentAudioIS.reset();
           byteBuffer.put (partnemeData) ;
        }
        catch (BufferOverflowException e )
         {
          e.printStackTrace();
          continue;
         }
        catch (IOException e )
        {
         e.printStackTrace();
         completeDataFeed=false;
         continue;
        }
     }
     if ( !completeDataFeed)
      {
        System.out.println ( "Problem occured in  feeding sound data " );
        throw new ImproperDataFeed(" Data was not feeded thoroughly");
      }
     if ( dumpFile != null )
     {
       try
        {
         System.out.println ( "Trying to write dumping file " );
         byte [] dumpBytes = new byte [byteBuffer.position()];
         System.out.println ( " ----  " + dumpBytes.length);
         byteBuffer.position(0);
         byteBuffer.get(dumpBytes);
         System.out.println ( "  " + dumpBytes.length);
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream ( dumpBytes);
         AudioFormat audioFormat =  currentAudioIS.getFormat();
         System.out.println ( "  " + audioFormat);
         AudioInputStream OutputAIStream = new AudioInputStream(byteArrayInputStream, audioFormat,dumpBytes.length);
         AudioSystem.write(OutputAIStream,AudioFileFormat.Type.WAVE,dumpFile);
         System.out.println ( "Completing to write dumping file " );
        }
        catch (IOException e )
        {
         e.printStackTrace();
        }
     }   */
  //       put System.gc() method here for efficiency    
   }
	public ConcatenaterImpl(int a[],int b[])
	{
             boolean completeDataFeed=true;
             //currentAudioIS  =  AudioSystem.getAudioInputStream(voiceFile);
             int previousAvailable = 0;
             try {	
	                previousAvailable = (int)currentAudioIS.available();
             } catch (IOException ioe) {
                ioe.printStackTrace();
             }
//             currentAudioIS.mark(((int) currentAudioIS.getFrameLength() *  currentAudioIS.getFormat().getFrameSize()));
/*********** commented previous two lines and introduced next two line   */
             int resetPosition = 0;
             try {
                resetPosition = previousAvailable - (int)currentAudioIS.available();
                currentAudioFile.seek(resetPosition);
             } catch (IOException ioe) {
                ioe.printStackTrace();
             }	
             int toSkip; // position in voicefile at which the partneme starts
             int toRead; // length of perticular partneme
             int numBytesRead;
             byte[] partnemeData = null;
             ByteBuffer byteBuffer = ByteBuffer.allocate(10000000); 
            for( int i=0 ; i<a.length ; i++) // put yor condi
            {
           
            try
            {
    //       System.out.println ( i + " avilable  " + currentAudioIS.available() );
/*************** commented next line */
               //currentAudioIS.reset();
               try
               {
                 toSkip = a[i]*bytesPerSample;
//                 currentAudioIS.skip(toSkip);
/*********************** commented previous ilne and introduced next line */
                 currentAudioFile.seek(toSkip);
               }
               catch (Throwable e )
               {
                 e.printStackTrace();
                 toSkip = 0;
                 completeDataFeed=false;
               }
               System.out.println ( i + " skipping  " + toSkip );
               try
               {
                 toRead =b[i]*bytesPerSample;
               }
               catch (Throwable e )
               {
                 e.printStackTrace();
                 toRead = 0;
                 completeDataFeed=false;                          
               }
               System.out.println ( i + " reading  " + toRead );
               partnemeData = new byte [toRead];
//               numBytesRead =  currentAudioIS.read(partnemeData,0,toRead);
/*********************** commented previous ilne and introduced next line */
                numBytesRead =  currentAudioFile.read(partnemeData,0,toRead);
               System.out.println ( i + " writing  " + numBytesRead );
               SDLine.write(partnemeData, 0, numBytesRead);
//               currentAudioIS.reset();
/*********************** commented previous ilne and introduced next line */
                currentAudioFile.seek(resetPosition);
               byteBuffer.put (partnemeData);
                }
                catch (BufferOverflowException e)
                {
                    e.printStackTrace();
                    continue;
                }
                catch (IOException e )
                {
                    e.printStackTrace();
                    completeDataFeed=false;
                    continue;
                }
                
                if ( !completeDataFeed)
                {
                    System.out.println ( "Problem occured in  feeding sound data " );
                   // throw new ImproperDataFeed(" Data was not feeded thoroughly");
                }
                 if ( dumpFile != null )
                {
                    try
                    {
                         System.out.println ( "Trying to write dumping file " );
                         byte [] dumpBytes = new byte [byteBuffer.position()];
                         System.out.println ( " ----  " + dumpBytes.length);
                         byteBuffer.position(0);
                         byteBuffer.get(dumpBytes);
                         System.out.println ( "  " + dumpBytes.length);
                         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream ( dumpBytes);
                         AudioFormat audioFormat =  currentAudioIS.getFormat();
                         System.out.println ( "  " + audioFormat);
                         AudioInputStream OutputAIStream = new AudioInputStream(byteArrayInputStream, audioFormat,dumpBytes.length);
                         AudioSystem.write(OutputAIStream,AudioFileFormat.Type.WAVE,dumpFile);
                         System.out.println ( "Completing to write dumping file " );
                    }
                    catch (IOException e )
                    {
                        e.printStackTrace();
                    }
                }   
            System.out.println("End....................................");
	}
        }

/*
	public ConcatenaterImpl(int a[],int b[])
	{
             boolean completeDataFeed=true;
             //currentAudioIS  =  AudioSystem.getAudioInputStream(voiceFile);
             
             currentAudioIS.mark(((int) currentAudioIS.getFrameLength() *  currentAudioIS.getFormat().getFrameSize()));
            
             int toSkip; // position in voicefile at which the partneme starts
             int toRead; // length of perticular partneme
             int numBytesRead;
             byte[] partnemeData = null;
             ByteBuffer byteBuffer = ByteBuffer.allocate(10000000); 
            for( int i=0 ; i<a.length ; i++) // put yor condi
            {
           
            try
            {
    //       System.out.println ( i + " avilable  " + currentAudioIS.available() );
               //currentAudioIS.reset();
               if ( currentAudioIS.markSupported() ) {
                   currentAudioIS.reset();
                   System.out.println("reset() supported in audio input stream...");
               } else {
                   System.out.println("reset() not supported in audio input stream...");
               } 
               try
               {
                 toSkip = a[i]*bytesPerSample;
                 currentAudioIS.skip(toSkip);
               }
               catch (Throwable e )
               {
                 e.printStackTrace();
                 toSkip = 0;
                 completeDataFeed=false;
               }
               System.out.println ( i + " skipping  " + toSkip );
               try
               {
                 toRead =b[i]*bytesPerSample;
               }
               catch (Throwable e )
               {
                 e.printStackTrace();
                 toRead = 0;
                 completeDataFeed=false;                          
               }
               System.out.println ( i + " reading  " + toRead );
               partnemeData = new byte [toRead];
               numBytesRead =  currentAudioIS.read(partnemeData,0,toRead);
               System.out.println ( i + " writing  " + numBytesRead );
               SDLine.write(partnemeData, 0, numBytesRead);
               currentAudioIS.reset();
               byteBuffer.put (partnemeData) ;
                }
                catch (BufferOverflowException e )
                {
                    e.printStackTrace();
                    continue;
                }
                catch (IOException e )
                {
                    e.printStackTrace();
                    completeDataFeed=false;
                    continue;
                }
                
                if ( !completeDataFeed)
                {
                    System.out.println ( "Problem occured in  feeding sound data " );
                   // throw new ImproperDataFeed(" Data was not feeded thoroughly");
                }
                 if ( dumpFile != null )
                {
                    try
                    {
                         System.out.println ( "Trying to write dumping file " );
                         byte [] dumpBytes = new byte [byteBuffer.position()];
                         System.out.println ( " ----  " + dumpBytes.length);
                         byteBuffer.position(0);
                         byteBuffer.get(dumpBytes);
                         System.out.println ( "  " + dumpBytes.length);
                         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream ( dumpBytes);
                         AudioFormat audioFormat =  currentAudioIS.getFormat();
                         System.out.println ( "  " + audioFormat);
                         AudioInputStream OutputAIStream = new AudioInputStream(byteArrayInputStream, audioFormat,dumpBytes.length);
                         AudioSystem.write(OutputAIStream,AudioFileFormat.Type.WAVE,dumpFile);
                         System.out.println ( "Completing to write dumping file " );
                    }
                    catch (IOException e )
                    {
                        e.printStackTrace();
                    }
                }   
            System.out.println("End....................................");
	}
        }

*/


  public void setDumpFile ( File dmpFile )
   {
     dumpFile = dmpFile ;
   }
}
/**
 * 
 * void ConcatenaterImpl.main(String[]){
 * try
 * {
 * File dir = new File ("C:/project/voice");
 * File voiceFile = new File ( dir , "partVoice" + ".wav" );   // "sargam"
 * File voiceInfoFile = new File ( dir , "partVoice" + ".vf" );
 * Concatenater c  = new  ConcatenaterImpl (voiceFile ,voiceInfoFile);
 * Player player = new PlayerImpl();
 * c.prepareSourceDataLine (player);
 * player.stop();
 * player.play();
 * //  c.concatenateAndFeed("re;re;re;re;re;re;re;re;");
 * c.concatenateAndFeed("ni;dha;ma;re;sa;ni;pa;ni;ma;ga;ma;ma;re;sa;");
 * c.concatenateAndFeed("ni;ni;ni;ni;ni;ni;ni;ni;");
 * c.concatenateAndFeed("re;re;re;re;re;re;re;re;");
 * c.concatenateAndFeed("sa;sa;sa;sa;sa;sa;sasdd;sa;" );
 * //    c.concatenateAndFeed("\u0259;");
 * //  c.concatenateAndFeed("sa;sa;sa;sa;re;dha;ni;pa;sa;ni;ma;ga;pa;sa;ga;ga;ma;re;dha;ni;pa;sa;ni;ma;ga;pa;");
 * }
 * catch (Exception e)
 * {
 * e.printStackTrace();
 * }
 * }
 * 
 */
