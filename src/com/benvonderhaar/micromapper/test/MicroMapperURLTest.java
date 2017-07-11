package com.benvonderhaar.micromapper.test;

import org.junit.Before;
import org.junit.Test;

import com.benvonderhaar.micromapper.servlet.MicroMapper;

public class MicroMapperURLTest {

	MMTestHarness harness;
	
	@Before
	public void before() {
		harness = new MMTestHarness(MicroMapper.class);
	}
	
	@Test
	public void testAssertMapping() {
		harness.assertMapping("/test/1?param1=4", "test");
	}
}
