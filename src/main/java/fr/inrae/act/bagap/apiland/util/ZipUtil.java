package fr.inrae.act.bagap.apiland.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    
    public static void zip(String zipFile, String inputFolder) {
    	
    	List<String> fileList = new ArrayList<String>();
    	
    	generateFileList(new File(inputFolder), inputFolder, fileList);
    	
    	zipIt(zipFile, inputFolder, fileList);
    }

    private static void zipIt(String zipFile, String inputFolder, List<String> fileList) {
    	
        byte[] buffer = new byte[1024];
        String source = new File(inputFolder).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            //System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (String file: fileList) {
                //System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(inputFolder + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();
            //System.out.println("Folder successfully compressed");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void generateFileList(File node, String inputFolder, List<String> fileList) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString(), inputFolder));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(new File(node, filename), inputFolder, fileList);
            }
        }
    }

    private static String generateZipEntry(String file, String inputFolder) {
        return file.substring(inputFolder.length(), file.length());
    }
}
