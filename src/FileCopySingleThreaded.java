//package de.htw.ds.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import de.sb.java.TypeMetadata;


/**
 * Demonstrates copying a file using a single thread. Note that this class is declared final because
 * it provides an application entry point, and therefore not supposed to be extended.
 */
@TypeMetadata(copyright = "2008-2015 Sascha Baumeister, all rights reserved", version = "0.3.0", authors = "Sascha Baumeister")
public final class FileCopySingleThreaded {

	/**
	 * Copies a file. The first argument is expected to be a qualified source file name, the second
	 * a qualified target file name.
	 * @param args the VM arguments
	 * @throws IOException if there's an I/O related problem
	 */
	static public void main (final String[] args) throws IOException {
		final Path sourcePath = Paths.get(args[0]);
		if (!Files.isReadable(sourcePath)) throw new IllegalArgumentException(sourcePath.toString());

		final Path sinkPath = Paths.get(args[1]);
		if (sinkPath.getParent() != null && !Files.isDirectory(sinkPath.getParent())) throw new IllegalArgumentException(sinkPath.toString());

		// Files.copy(sourcePath, sinkPath, StandardCopyOption.REPLACE_EXISTING);

//n�chste Zeile: try with resources genannt, resource in runden Klammern, virtueller finally Block, in dem die ge�ffnete Res geschlossen wird, egal wie der try block verlassen wurde
//Wichtig: Hilfreich f�r das verlangte Schlie�en der Res.
// bei multi threading komplex, weil in einem thread ge�ffnet und in anderem geschlossen
		try (InputStream fis = Files.newInputStream(sourcePath)) {
			try (OutputStream fos = Files.newOutputStream(sinkPath)) {
				final byte[] buffer = new byte[0x10000];
				for (int bytesRead = fis.read(buffer); bytesRead != -1; bytesRead = fis.read(buffer)) {
					fos.write(buffer, 0, bytesRead);
// nur soviel schreiben wie man gelesen hat, sicher: es wurde min 1 gelesen, aber der buffer muss nicht komplett gef�llt sein
				}
			}
		}
		
		System.out.println("done.");
	}
}