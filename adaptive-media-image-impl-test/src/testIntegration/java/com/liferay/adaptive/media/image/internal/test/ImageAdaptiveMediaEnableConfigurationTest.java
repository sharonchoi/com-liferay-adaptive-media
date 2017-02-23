/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.adaptive.media.image.internal.test;

import com.liferay.adaptive.media.image.configuration.ImageAdaptiveMediaConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.ImageAdaptiveMediaConfigurationHelper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Filter;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
@Sync
public class ImageAdaptiveMediaEnableConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		Registry registry = RegistryUtil.getRegistry();

		Filter filter = registry.getFilter(
			"(objectClass=" +
				ImageAdaptiveMediaConfigurationHelper.class.getName() + ")");

		_serviceTracker = registry.trackServices(filter);

		_serviceTracker.open();
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceTracker.close();
	}

	@Before
	public void setUp() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Collection<ImageAdaptiveMediaConfigurationEntry> configurationEntries =
			configurationHelper.getImageAdaptiveMediaConfigurationEntries(
				TestPropsValues.getCompanyId(), configurationEntry -> true);

		for (ImageAdaptiveMediaConfigurationEntry configurationEntry :
				configurationEntries) {

			configurationHelper.forceDeleteImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), configurationEntry.getUUID());
		}
	}

	@Test
	public void testEnableAllConfigurationEntries() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		properties = new HashMap<>();

		properties.put("max-height", "200");
		properties.put("max-width", "200");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "2", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		Optional<ImageAdaptiveMediaConfigurationEntry>
			firstConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertDisabled(firstConfigurationEntryOptional);

		Optional<ImageAdaptiveMediaConfigurationEntry>
			secondConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "2");

		_assertDisabled(secondConfigurationEntryOptional);

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");
		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		firstConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		_assertEnabled(firstConfigurationEntryOptional);

		secondConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		_assertEnabled(secondConfigurationEntryOptional);
	}

	@Test
	public void testEnableConfigurationEntryWithExistingDisabledConfiguration()
		throws Exception {

		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		properties = new HashMap<>();

		properties.put("max-height", "200");
		properties.put("max-width", "200");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "2", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		Optional<ImageAdaptiveMediaConfigurationEntry>
			configurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertEnabled(configurationEntryOptional);

		configurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		_assertDisabled(configurationEntryOptional);
	}

	@Test
	public void testEnableEnabledConfigurationEntry() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		Optional<ImageAdaptiveMediaConfigurationEntry>
			configurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertEnabled(configurationEntryOptional);

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		configurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		_assertEnabled(configurationEntryOptional);
	}

	@Test
	public void testEnableFirstConfigurationEntry() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		properties = new HashMap<>();

		properties.put("max-height", "200");
		properties.put("max-width", "200");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "2", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		Optional<ImageAdaptiveMediaConfigurationEntry>
			firstConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertDisabled(firstConfigurationEntryOptional);

		Optional<ImageAdaptiveMediaConfigurationEntry>
			secondConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "2");

		_assertDisabled(secondConfigurationEntryOptional);

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		firstConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		_assertEnabled(firstConfigurationEntryOptional);

		secondConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		_assertDisabled(secondConfigurationEntryOptional);
	}

	@Test
	public void testEnableSecondConfigurationEntry() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		properties = new HashMap<>();

		properties.put("max-height", "200");
		properties.put("max-width", "200");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "two", "2", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		Optional<ImageAdaptiveMediaConfigurationEntry>
			firstConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertDisabled(firstConfigurationEntryOptional);

		Optional<ImageAdaptiveMediaConfigurationEntry>
			secondConfigurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "2");

		_assertDisabled(secondConfigurationEntryOptional);

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "2");

		firstConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		_assertDisabled(firstConfigurationEntryOptional);

		secondConfigurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "2");

		_assertEnabled(secondConfigurationEntryOptional);
	}

	@Test
	public void testEnableUniqueConfigurationEntry() throws Exception {
		ImageAdaptiveMediaConfigurationHelper configurationHelper =
			_serviceTracker.getService();

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "one", "1", properties);

		configurationHelper.disableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		Optional<ImageAdaptiveMediaConfigurationEntry>
			configurationEntryOptional =
				configurationHelper.getImageAdaptiveMediaConfigurationEntry(
					TestPropsValues.getCompanyId(), "1");

		_assertDisabled(configurationEntryOptional);

		configurationHelper.enableImageAdaptiveMediaConfigurationEntry(
			TestPropsValues.getCompanyId(), "1");

		configurationEntryOptional =
			configurationHelper.getImageAdaptiveMediaConfigurationEntry(
				TestPropsValues.getCompanyId(), "1");

		_assertEnabled(configurationEntryOptional);
	}

	private void _assertDisabled(
		Optional<ImageAdaptiveMediaConfigurationEntry>
			configurationEntryOptional) {

		Assert.assertTrue(configurationEntryOptional.isPresent());

		ImageAdaptiveMediaConfigurationEntry configurationEntry =
			configurationEntryOptional.get();

		Assert.assertFalse(configurationEntry.isEnabled());
	}

	private void _assertEnabled(
		Optional<ImageAdaptiveMediaConfigurationEntry>
			configurationEntryOptional) {

		Assert.assertTrue(configurationEntryOptional.isPresent());

		ImageAdaptiveMediaConfigurationEntry configurationEntry =
			configurationEntryOptional.get();

		Assert.assertTrue(configurationEntry.isEnabled());
	}

	private static ServiceTracker
		<ImageAdaptiveMediaConfigurationHelper,
			ImageAdaptiveMediaConfigurationHelper> _serviceTracker;

}