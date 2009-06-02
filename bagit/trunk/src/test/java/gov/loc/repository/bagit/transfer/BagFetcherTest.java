package gov.loc.repository.bagit.transfer;

import static junit.framework.Assert.*;

import java.io.File;
import java.net.URI;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.ResourceHelper;

import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class BagFetcherTest
{
	private static File tempDir = new File("target/unittestdata/BagFetcherTest");
	private Mockery context = new Mockery();
	private BagFactory bagFactory = new BagFactory();
	private BagFetcher unit;
	
	@Before
	public void setUp() throws Exception 
	{
		if (tempDir.exists())
			FileUtils.forceDelete(tempDir);
		
		tempDir.mkdirs();
		
		this.unit = new BagFetcher(this.bagFactory);
		this.unit.setFetchFailStrategy(new ThrowExceptionFailStrategy());

		FileUtils.copyDirectory(ResourceHelper.getFile("bags/v0_96/holey-bag"), tempDir);
		FileUtils.deleteDirectory(new File(tempDir, "data"));	
	}

	@After
	public void tearDown() throws Exception
	{
		if (tempDir.exists())
			FileUtils.deleteQuietly(tempDir);
	}

	@Test
	public void testFetchSingleThread() throws Exception
	{
		this.unit.setNumberOfThreads(1);

		final FetchedFileDestinationFactory mockDestinationFactory = this.context.mock(FetchedFileDestinationFactory.class);		
		final FetchProtocol mockProtocol = this.context.mock(FetchProtocol.class);
		final FileFetcher mockFetcher = this.context.mock(FileFetcher.class);
		final States fetcherState = this.context.states("fetcher").startsAs("new");
		
		context.checking(new Expectations() {{
			// Destination
			expectDest(this, mockDestinationFactory, "data/dir1/test3.txt");
			expectDest(this, mockDestinationFactory, "data/dir2/dir3/test5.txt");
			expectDest(this, mockDestinationFactory, "data/dir2/test4.txt");
			expectDest(this, mockDestinationFactory, "data/test1.txt");
			expectDest(this, mockDestinationFactory, "data/test2.txt");
						
			// Protocol
			one(mockProtocol).createFetcher(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/dir1/test3.txt"), null);	will(returnValue(mockFetcher));
			
			// Fetcher
			one(mockFetcher).initialize(); when(fetcherState.is("new")); then(fetcherState.is("ready"));
			one(mockFetcher).fetchFile(with(equal(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/dir1/test3.txt"))), with(any(Long.class)), with(aNonNull(FetchedFileDestination.class))); when(fetcherState.is("ready"));
			one(mockFetcher).fetchFile(with(equal(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/dir2/dir3/test5.txt"))), with(any(Long.class)), with(aNonNull(FetchedFileDestination.class))); when(fetcherState.is("ready"));
			one(mockFetcher).fetchFile(with(equal(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/dir2/test4.txt"))), with(any(Long.class)), with(aNonNull(FetchedFileDestination.class))); when(fetcherState.is("ready"));
			one(mockFetcher).fetchFile(with(equal(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/test1.txt"))), with(any(Long.class)), with(aNonNull(FetchedFileDestination.class))); when(fetcherState.is("ready"));
			one(mockFetcher).fetchFile(with(equal(new URI("http://localhost:8989/bags/v0_96/holey-bag/data/test2.txt"))), with(any(Long.class)), with(aNonNull(FetchedFileDestination.class))); when(fetcherState.is("ready"));
			one(mockFetcher).close(); when(fetcherState.is("ready")); then(fetcherState.is("closed"));
		}});
		
		this.unit.registerProtocol("http", mockProtocol);
		
		Bag bag = this.bagFactory.createBag(tempDir);
		
		BagFetchResult result = this.unit.fetch(bag, mockDestinationFactory);
		
		assertTrue("Bag did not transfer successfully.", result.isSuccess());
	}
	
	private void expectDest(Expectations e, FetchedFileDestinationFactory destFactory, String path) throws Exception
	{
		FetchedFileDestination mockDestination = this.context.mock(FetchedFileDestination.class, "FetchedFileDestination:" + path);
		
		// Make the factory return a new destination.
		e.one(destFactory).createDestination(path, null);
		e.will(Expectations.returnValue(mockDestination));
		
		// That destination should:
		// 1) Always return the file path.
		e.allowing(mockDestination).getFilepath();
		e.will(Expectations.returnValue(path));
		
		// 2) Be opened once.
		// Except that the destination will never be opened because
		// it is being passed to a mock fetcher, which won't open it.  :-)
		// e.one(mockDestination).openOutputStream(false);
		// e.will(Expectations.returnValue(new NullOutputStream()));
		
		// 3) Be committed once.		
		e.one(mockDestination).commit();
	}
}
