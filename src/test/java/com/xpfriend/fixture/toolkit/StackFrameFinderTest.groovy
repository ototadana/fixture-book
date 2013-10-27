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
package com.xpfriend.fixture.toolkit

import org.spockframework.runtime.model.SpecInfo;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class StackFrameFinderTest extends Specification {

	def "findは指定されたクラスのStackTraceElementを取得する"() {
		expect:
		new CallerCaller().call1();
	}
	
	private class CallerCaller {
		public void call1() {
			new FinderCaller().call2();
		}
	}
	
	private class FinderCaller {
		public void call2() {
			SpecInfo specInfo = new org.spockframework.runtime.SpecInfoBuilder(StackFrameFinderTest).build();
			List<StackTraceElement> frames = StackFrameFinder.find(StackFrameFinderTest.class.getName());
			for(StackTraceElement frame : frames) {
				
				println("called : " + frame);
				String featureName = specInfo.toFeatureName(frame.getMethodName());
				println featureName
				assert featureName == "findは指定されたクラスのStackTraceElementを取得する"
			}
		}
	}
}
