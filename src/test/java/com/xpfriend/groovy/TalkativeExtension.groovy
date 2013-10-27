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
package com.xpfriend.groovy

import static org.codehaus.groovy.tools.Utilities.*;
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.ConditionNotSatisfiedError
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.builtin.UnrollNameProvider
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * アサーションのエラー時にラベルの内容を付与するためのエクステンション。
 *
 * 参考:
 * <a href="http://ldaley.com/post/971946675/annotation-driven-extensions-with-spock">Annotation Driven Extensions With Spock</a>
 *
 */
class TalkativeExtension extends AbstractAnnotationDrivenExtension<Talkative> {

	@Override
	public void visitSpecAnnotation(Talkative annotation, SpecInfo spec) {
	}

	@Override
	public void visitSpec(final SpecInfo spec) {
		IterationInfo iteration;
		FeatureInfo feature;

		spec.addListener(new AbstractRunListener(){
			@Override
			public void beforeFeature(FeatureInfo info) {
				feature = info;
			}

			@Override
			public void beforeIteration(IterationInfo info) {
				iteration = info;
			}

			@Override
			public void afterIteration(IterationInfo info) {
				iteration = null;
			}

			@Override
			public void afterFeature(FeatureInfo info) {
				feature = null;
			}

			@Override
			public void error(ErrorInfo error) {
				if(error.exception instanceof ConditionNotSatisfiedError) {
					StringBuilder sb = new StringBuilder();
					error.method.feature.blocks*.texts*.each {sb.append(it)}
					String labels = sb.toString();
					if(labels.contains("#") && iteration != null) {
						UnrollNameProvider nameProvider = new UnrollNameProvider(feature, labels);
						labels = nameProvider.getName(iteration);
					}
					error.exception.condition.createRendering();
					error.exception.condition.rendering = labels + eol() + error.exception.condition.rendering;
				}
			}
		});
	}
}
