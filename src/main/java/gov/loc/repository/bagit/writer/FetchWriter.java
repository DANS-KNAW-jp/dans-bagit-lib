package gov.loc.repository.bagit.writer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.domain.FetchItem;

public final class FetchWriter {
  private static final Logger logger = LoggerFactory.getLogger(FetchWriter.class);

  private FetchWriter(){
    //intentionally left empty
  }
  
  /**
   * Write the fetch.txt file to the outputDir with the specified encoding (charsetName)
   * 
   * @param itemsToFetch the list of {@link FetchItem}s to write into the fetch.txt
   * @param outputDir the root of the bag
   * @param charsetName the name of the encoding for the file
   * 
   * @throws IOException if there was a problem writing a file
   */
  public static void writeFetchFile(final List<FetchItem> itemsToFetch, final Path outputDir, final Charset charsetName) throws IOException{
    logger.debug("Writing fetch.txt to [{}]", outputDir);
    final Path fetchFilePath = outputDir.resolve("fetch.txt");
    
    for(final FetchItem item : itemsToFetch){
      final String line = item.toString() + System.lineSeparator();
      logger.debug("Writing [{}] to [{}]", line, fetchFilePath);
      Files.write(fetchFilePath, line.getBytes(charsetName), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
  }
}
