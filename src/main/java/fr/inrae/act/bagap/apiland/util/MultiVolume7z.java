package fr.inrae.act.bagap.apiland.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiVolume7z {

	public static void create(String sourceDir, String output7Z, String volumeSize) throws IOException, InterruptedException {
		
		create(new File(sourceDir), new File(output7Z), volumeSize);
	}
	
    /**
     * Compresse un dossier en 7Z multi-volume avec 7-Zip
     *
     * @param sourceDir dossier source à compresser
     * @param output7Z nom de base de l’archive de sortie (ex: "backup.zip")
     * @param volumeSize taille de chaque volume (ex: "2000m" = 2000 Mo, "2g" = 2 Go)
     */
    public static void create(File sourceDir, File output7Z, String volumeSize) throws IOException, InterruptedException {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IllegalArgumentException("Le chemin source doit être un dossier valide.");
        }

        // Commande 7z : 7z a -t7z -v2g archive.7z dossier/
        List<String> command = new ArrayList<>();
        command.add("c://Program Files/7-Zip/7z.exe");
        command.add("a");              // ajouter à l’archive
        command.add("-t7z");          // format ZIP
        command.add("-v" + volumeSize); // taille max par volume
        command.add(output7Z.getAbsolutePath());
        command.add(sourceDir.getAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.inheritIO(); // redirige la sortie dans la console Java

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Échec de la compression (code " + exitCode + ")");
        }
    }

    public static void main(String[] args) {
        try {
            File source = new File("C:/Data/projet/grain_bocager/data/GRAIN-BOCAGER_1-0__TIFF_LAMB93_D012_2022-01-01/"); 	// Dossier à compresser
            File output = new File("C:/Data/projet/grain_bocager/archive2/GRAIN-BOCAGER_1-0__TIFF_LAMB93_D012_2022-01-01"); // Fichier 7Z multi-volume
            String volumeSize = "2g";                      // 2000 Mo (~2 Go) par volume

            create(source, output, volumeSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}