
//package de.htw.ds.sync;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import de.sb.java.TypeMetadata;

/**
 * Demonstrates copying a file using a single thread. Note that this class is
 * declared final because it provides an application entry point, and therefore
 * not supposed to be extended.
 */
@TypeMetadata(copyright = "2008-2015 Sascha Baumeister, all rights reserved", version = "0.3.0", authors = "Sascha Baumeister")
public final class FileCopyMultiThreaded {

	/**
	 * Copies a file. The first argument is expected to be a qualified source
	 * file name, the second a qualified target file name.
	 * 
	 * @param args
	 *            the VM arguments
	 * @throws IOException
	 *             if there's an I/O related problem
	 */
	static public void main(final String[] args) throws IOException {
		final Path sourcePath = Paths.get(args[0]);
		if (!Files.isReadable(sourcePath))
			throw new IllegalArgumentException(sourcePath.toString());

		final Path sinkPath = Paths.get(args[1]);
		if (sinkPath.getParent() != null && !Files.isDirectory(sinkPath.getParent()))
			throw new IllegalArgumentException(sinkPath.toString());

		// Files.copy(sourcePath, sinkPath,
		// StandardCopyOption.REPLACE_EXISTING);
		// nächste Zeile: try with resources genannt, resource in runden
		// Klammern, virtueller finally Block, in dem die geöffnete Res
		// geschlossen wird, egal wie der try block verlassen wurde
		// Wichtig: Hilfreich für das verlangte Schließen der Res.
		// bei multi threading komplex, weil in einem thread geöffnet und in
		// anderem geschlossen
		// nur soviel schreiben wie man gelesen hat, sicher: es wurde min 1
		// gelesen, aber der buffer muss nicht komplett gefüllt sein

		InputStream fis = Files.newInputStream(sourcePath);
		OutputStream fos = Files.newOutputStream(sinkPath);

		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream();
		pis.connect(pos);

		Transporter t1 = new Transporter(fis, pos);
		Transporter t2 = new Transporter(pis, fos);

		Thread dt1 = new Thread(t1);
		Thread dt2 = new Thread(t2);

		dt1.start();
		dt2.start();

		System.out.println("done.");
	}

	static class Transporter implements Runnable {
		InputStream inputStream;
		OutputStream outputStream;

		Transporter(InputStream iS, OutputStream oS) {
			inputStream = iS;
			outputStream = oS;
		}

		@Override
		public void run() {
			final byte[] buffer = new byte[0x10000];
			try {
				for (int bytesRead = inputStream.read(buffer); bytesRead != -1; bytesRead = inputStream.read(buffer)) {
					outputStream.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
}