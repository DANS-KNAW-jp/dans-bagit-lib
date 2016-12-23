package gov.loc.repository.bagit.conformance;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Part of the BagIt conformance suite. 
 * This checker gives a warning if a file is not using UTF-8 encoding which is standard on most filesystems today.
 */
public interface EncodingChecker {
  Logger logger = LoggerFactory.getLogger(EncodingChecker.class);
  
  /*
   * It is now normal for all files to be UTF-8
   */
  static void checkEncoding(final Charset encoding, final Set<BagitWarning> warnings, final Collection<BagitWarning> warningsToIgnore){
    if(!warningsToIgnore.contains(BagitWarning.TAG_FILES_ENCODING) && !StandardCharsets.UTF_8.equals(encoding)){
      logger.warn("Tag files are encoded with [{}]. We recommend always using UTF-8 instead.", encoding);
      warnings.add(BagitWarning.TAG_FILES_ENCODING);
    }
  }
}
