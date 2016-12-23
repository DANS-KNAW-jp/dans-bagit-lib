package gov.loc.repository.bagit.conformance;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.domain.Version;

/**
 * Part of the BagIt conformance suite. 
 * This checker gives a warning if a bag is not using the latest bagit version
 */
public interface VersionChecker {
  Logger logger = LoggerFactory.getLogger(VersionChecker.class);
  Version LATEST_BAGIT_VERSION = new Version(0, 97);
  
  /*
   * Check that they are using the latest version
   */
  static void checkVersion(final Version version, final Set<BagitWarning> warnings, final Collection<BagitWarning> warningsToIgnore){
    if(!warningsToIgnore.contains(BagitWarning.OLD_BAGIT_VERSION) && LATEST_BAGIT_VERSION.compareTo(version) == 1){
      logger.warn("Found version [{}] of the bagit specification but the latest version is [{}].", version, LATEST_BAGIT_VERSION);
      warnings.add(BagitWarning.OLD_BAGIT_VERSION);
    }
  }
}
