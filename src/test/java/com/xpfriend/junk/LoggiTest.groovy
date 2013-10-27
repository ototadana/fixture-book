/*
 * Copyright 2013 XPFriend Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xpfriend.junk

import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.StreamHandler;
import java.util.logging.Formatter;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class LoggiTest extends Specification {
	
	private static class TestFormatter extends Formatter {
		private Throwable thrown;
		
		@Override
		public String format(LogRecord record) {
			this.thrown = record.thrown;
			StringBuilder sb = new StringBuilder();
			sb.append(record.level).append(" ").append(record.message);
			return sb.toString();
		}
	}
	
	private static class LogTest {
		private ByteArrayOutputStream output;
		private Logger logger
		private StreamHandler handler;
		private TestFormatter formatter;
		
		public LogTest() {
			output = new ByteArrayOutputStream();
			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			formatter = new TestFormatter();
			handler = new StreamHandler(output, formatter);
			logger.addHandler(handler);	
		}

		public void assertThrown(Throwable thrown) {
			assert thrown == formatter.thrown;
		}
		
		public void assertMessage(String expected) {
			handler.flush();
			assert output.toString() == expected;
		}
		
		public void dispose() {
			logger.removeHandler(handler);
		}
	}
	
	private LogTest logTest;
	
	def setup() {
		Loggi.setDebugEnabled(false)
		logTest = new LogTest()
	}
	
	def cleanup() {
		logTest.dispose();
		Loggi.setDebugEnabled(false);
	}

	def "info（String）はINFOレベルでログ出力を行う"() {
		when:
		Loggi.info("aaa")
		
		then:
		logTest.assertMessage("INFO aaa")
	}
	
	def "info（Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.info(exception)
		
		then:
		logTest.assertMessage("INFO java.lang.Exception")
		logTest.assertThrown(exception)
	}
	
	def "info（String,Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.info("aaa", exception)
		
		then:
		logTest.assertMessage("INFO aaa")
		logTest.assertThrown(exception)
	}
	
	def "warn（String）はINFOレベルでログ出力を行う"() {
		when:
		Loggi.warn("aaa")
		
		then:
		logTest.assertMessage("WARNING aaa")
	}
	
	def "warn（Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.warn(exception)
		
		then:
		logTest.assertMessage("WARNING java.lang.Exception")
		logTest.assertThrown(exception)
	}
	
	def "warn（String,Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.warn("aaa", exception)
		
		then:
		logTest.assertMessage("WARNING aaa")
		logTest.assertThrown(exception)
	}
	
	def "error（String）はINFOレベルでログ出力を行う"() {
		when:
		Loggi.error("aaa")
		
		then:
		logTest.assertMessage("SEVERE aaa")
	}
	
	def "error（Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.error(exception)
		
		then:
		logTest.assertMessage("SEVERE java.lang.Exception")
		logTest.assertThrown(exception)
	}
	
	def "error（String,Throwable）はINFOレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.error("aaa", exception)
		
		then:
		logTest.assertMessage("SEVERE aaa")
		logTest.assertThrown(exception)
	}

	def "debug（String）はデバッグレベルでログ出力を行う"() {
		when:
		Loggi.setDebugEnabled(true)
		Loggi.debug("aaa")
		
		then:
		logTest.assertMessage("CONFIG aaa")
	}
	
	def "debug（Throwable）はデバッグレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.setDebugEnabled(true)
		Loggi.debug(exception)
		
		then:
		logTest.assertMessage("CONFIG java.lang.Exception")
		logTest.assertThrown(exception)
	}

	def "debug（String,Throwable）はデバッグレベルでログ出力を行う"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.setDebugEnabled(true)
		Loggi.debug("aaa", exception)
		
		then:
		logTest.assertMessage("CONFIG aaa")
		logTest.assertThrown(exception)
	}
	
	def "isDebugEnabledはsetDebugEnabledでtrueをセットするとtrueを返す"(boolean enabled) {
		when:
		Loggi.setDebugEnabled(enabled)
		
		then:
		Loggi.isDebugEnabled() == enabled
		
		where:
		enabled << [true, false]
	}

	def "debug（String）はsetDebugEnabled（false）のときにログ出力しない"() {
		when:
		Loggi.setDebugEnabled(false)
		Loggi.debug("aaa")
		
		then:
		logTest.assertMessage("")
	}
	
	def "debug（Throwable）はsetDebugEnabled（false）のときにログ出力しない"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.setDebugEnabled(false)
		Loggi.debug(exception)
		
		then:
		logTest.assertMessage("")
		logTest.assertThrown(null)
	}

	def "debug（String,Throwable）setDebugEnabled（false）のときにログ出力しない"() {
		setup:
		Throwable exception = new Exception()
		
		when:
		Loggi.setDebugEnabled(false)
		Loggi.debug("aaa", exception)
		
		then:
		logTest.assertMessage("")
		logTest.assertThrown(null)
	}
	
}
