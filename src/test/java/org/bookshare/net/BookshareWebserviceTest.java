package org.bookshare.net;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class BookshareWebserviceTest {
    private BookshareWebservice service;
    
    @Before
    public void setup() {
        service = new BookshareWebservice();
    }
    
    @Test
    public void testMd5sum() throws Exception {
        // setup
        final String input = "test-string-for-md5";
        
        // run the test
        final String result = service.md5sum(input);
        
        // verify
        final String expectedOutput = "DC5627ED84AB50D93A30DE90B88DE94E";
        assertEquals(expectedOutput, result);
    }
}
