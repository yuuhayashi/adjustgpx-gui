package osm.jp.gpx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

    /**
     * Zipファイルを展開します
     * @param aZipFile zipファイル
     * @param aOutDir  出力先ディレクトリ
     * @throws java.io.IOException
     */
    public static void decode(File aZipFile, String aOutDir) throws IOException {
        FileInputStream  fileIn  = null;
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        
        try {
            File outDir = new File(aOutDir);
            outDir.mkdirs();
            
            fileIn = new FileInputStream(aZipFile);
            zipIn = new ZipInputStream(fileIn);
            
            ZipEntry entry = null;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    String relativePath = entry.getName();
                    outDir = new File(outDir, relativePath);
                    outDir.mkdirs();
                }
                else {
                    String relativePath = entry.getName();
                    File outFile = new File( outDir, relativePath );
                    
                    File parentFile = outFile.getParentFile();
                    parentFile.mkdirs();
                    
                    fileOut = new FileOutputStream( outFile );
                    
                    byte[] buf = new byte[ 256 ];
                    int size = 0;
                    while ((size = zipIn.read(buf)) > 0){
                        fileOut.write(buf, 0, size);
                    }
                    fileOut.close();
                    fileOut = null;
                }
                zipIn.closeEntry();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fileIn != null) {
                try {
                    fileIn.close();
                }
                catch (IOException e) {}
            }
            if (fileOut != null) {
                try {
                    fileOut.close();
                }
                catch(IOException e) {}
            }
            zipIn.close();
        }
    }
}
