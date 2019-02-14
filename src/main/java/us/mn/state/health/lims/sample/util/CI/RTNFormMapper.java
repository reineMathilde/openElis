/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
*
* The Original Code is OpenELIS code.
*
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.sample.util.CI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import spring.mine.common.form.BaseForm;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.valueholder.ObservationData;
import us.mn.state.health.lims.test.valueholder.Test;

public class RTNFormMapper extends BaseProjectFormMapper implements IProjectFormMapper {

	private String projectCode = "LRTN";

	public RTNFormMapper(String projectFormId, BaseForm form) {
		super(projectFormId, form);
	}

	private List<Test> getTests(BaseForm form) {
		List<Test> testList = new ArrayList<>();

		if (projectData.getSerologyHIVTest()) {
			CollectionUtils.addIgnoreNull(testList, createTest("Vironostika", true));
			CollectionUtils.addIgnoreNull(testList, createTest("Murex", true));
			CollectionUtils.addIgnoreNull(testList, createTest("Integral", true));
		}

		return testList;
	}

	@Override
	public String getProjectCode() {
		return projectCode;
	}

	@Override
	public ArrayList<TypeOfSampleTests> getTypeOfSampleTests() {
		ArrayList<TypeOfSampleTests> sItemTests = new ArrayList<>();
		List<Test> testList = new ArrayList<>();

		// Check for Dry Tube Tests
		if (projectData.getSerologyHIVTest()) {
			if (projectData.getDryTubeTaken()) {
				testList = getTests(form);
				sItemTests.add(new TypeOfSampleTests(getTypeOfSample("Dry Tube"), testList));
			}
		}
		return sItemTests;
	}

	/**
	 * No lists of repeating answers in RTN
	 *
	 * @see us.mn.state.health.lims.sample.util.CI.BaseProjectFormMapper#readObservationHistoryLists(us.mn.state.health.lims.patient.valueholder.ObservationData)
	 */
	@Override
	public Map<String, List<ObservationHistory>> readObservationHistoryLists(ObservationData od) {
		Map<String, List<ObservationHistory>> historyLists = new HashMap<>();
		// do nothing
		return historyLists;
	}

	/**
	 * @see us.mn.state.health.lims.sample.util.CI.BaseProjectFormMapper#getSampleCenterCode()
	 */
	@Override
	public String getSampleCenterCode() {
		return ORGANIZATION_ID_NONE;
	}
}