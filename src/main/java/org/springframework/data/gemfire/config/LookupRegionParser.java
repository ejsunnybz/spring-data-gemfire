/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gemfire.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.data.gemfire.LookupRegionFactoryBean;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.gemstone.gemfire.cache.asyncqueue.AsyncEventQueue;
import com.gemstone.gemfire.cache.wan.GatewaySender;

/**
 * Parser for GFE &lt;lookup-region&gt; bean definitions.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see org.springframework.data.gemfire.LookupRegionFactoryBean
 * @see org.springframework.data.gemfire.config.AbstractRegionParser
 */
class LookupRegionParser extends AbstractRegionParser {

	@Override
	protected Class<?> getRegionFactoryClass() {
		return LookupRegionFactoryBean.class;
	}

	@Override
	protected void doParseRegion(Element element, ParserContext parserContext, BeanDefinitionBuilder builder,
			boolean subRegion) {

		super.doParse(element, builder);

		String resolvedCacheRef = ParsingUtils.resolveCacheReference(element.getAttribute("cache-ref"));

		builder.addPropertyReference("cache", resolvedCacheRef);

		ParsingUtils.setPropertyValue(element, builder, "cloning-enabled");
		ParsingUtils.setPropertyValue(element, builder, "eviction-maximum");
		ParsingUtils.setPropertyValue(element, builder, "name");
		ParsingUtils.parseExpiration(parserContext, element, builder);

		parseCollectionOfCustomSubElements(element, parserContext, builder, AsyncEventQueue.class.getName(),
			"async-event-queue", "asyncEventQueues");

		parseCollectionOfCustomSubElements(element, parserContext, builder, GatewaySender.class.getName(),
			"gateway-sender", "gatewaySenders");

		Element cacheListenerElement = DomUtils.getChildElementByTagName(element, "cache-listener");

		if (cacheListenerElement != null) {
			builder.addPropertyValue("cacheListeners", ParsingUtils.parseRefOrNestedBeanDeclaration(parserContext,
				cacheListenerElement, builder));
		}

		Element cacheLoaderElement = DomUtils.getChildElementByTagName(element, "cache-loader");

		if (cacheLoaderElement != null) {
			builder.addPropertyValue("cacheLoader", ParsingUtils.parseRefOrSingleNestedBeanDeclaration(
				parserContext, cacheLoaderElement, builder));
		}

		Element cacheWriterElement = DomUtils.getChildElementByTagName(element, "cache-writer");

		if (cacheWriterElement != null) {
			builder.addPropertyValue("cacheWriter", ParsingUtils.parseRefOrSingleNestedBeanDeclaration(
				parserContext, cacheWriterElement, builder));
		}

		if (!subRegion) {
			parseSubRegions(element, parserContext, resolvedCacheRef);
		}
	}

}
