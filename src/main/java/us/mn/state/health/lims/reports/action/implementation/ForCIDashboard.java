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
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.reports.action.implementation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.jfree.util.Log;
import spring.mine.common.form.BaseForm;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.validator.GenericValidator.isBlankOrNull;

/**
 * @author Paul A. Hill (pahill@uw.edu)
 * @since Jan 26, 2011
 */
public class ForCIDashboard extends CSVSampleExportReport implements IReportParameterSetter, IReportCreator {
	protected final ProjectDAO projectDAO = new ProjectDAOImpl();
	private String projectStr;
	private Project project;
	private String indicStr;
	protected static final SimpleDateFormat postgresDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	//private String indicLabel;
	
	@Override
	protected String reportFileName(){
		//return indicLabel;
		return "ForCIDashboard";
	}

	public void setRequestParameters(BaseForm form) {
		try {
			PropertyUtils.setProperty(form, "reportName", getReportNameForParameterPage());
			PropertyUtils.setProperty(form, "useLowerDateRange", Boolean.TRUE);
			PropertyUtils.setProperty(form, "useUpperDateRange", Boolean.TRUE);
			//PropertyUtils.setProperty(form, "useProjectCode", Boolean.TRUE);
			PropertyUtils.setProperty(form, "useDashboard", Boolean.TRUE);
			PropertyUtils.setProperty(form, "projectCodeList", getProjectList());
		} catch (Exception e) {
			Log.error("Error in CIDashboard.setRequestParemeters: ", e);
		}
	}

	protected String getReportNameForParameterPage() {
		return StringUtil.getMessageForKey("reports.label.project.export") + " "
				+ "Date d'impression du rapport";//StringUtil.getContextualMessageForKey("sample.collectionDate");
	}

	protected void createReportParameters() {
		super.createReportParameters();
		reportParameters.put("studyName", (project == null) ? null : project.getLocalizedName());
	}

	public void initializeReport(BaseForm form) {
		super.initializeReport();
		errorFound = false;
		
		indicStr = form.getString("projectCode");

		lowDateStr = form.getString("lowerDateRange");
		highDateStr = form.getString("upperDateRange");
		projectStr = form.getString("projectCode");
		dateRange = new DateRange(lowDateStr, highDateStr);
		String[] splitline = form.getString("projectCode").split(":");
		
		projectStr = splitline[0];
		//indicLabel = splitline[1];
		createReportParameters();
		
		errorFound = !validateSubmitParameters();
		if (errorFound) {
			return;
		}
		
		createReportItems();
	}

	/**
	 * check everything
	 */
	private boolean validateSubmitParameters() {
		return dateRange.validateHighLowDate("report.error.message.date.received.missing") && validateProject();
	}

	/**
	 * @return true, if location is not blank or "0" is is found in the DB;
	 *         false otherwise
	 */
	private boolean validateProject() {
		if (isBlankOrNull(projectStr) || "0".equals(Integer.getInteger(projectStr))) {
			add1LineErrorMessage("report.error.message.project.missing");
			return false;
		}
		ProjectDAO dao = new ProjectDAOImpl();
		project = dao.getProjectById(projectStr);
		if (project == null) {
			add1LineErrorMessage("report.error.message.project.missing");
			return false;
		}
		return true;
	}

	/**
	 * creating the list for generation to the report
	 */
	private void createReportItems() {
		try {
			csvColumnBuilder = getColumnBuilder(projectStr);
			csvColumnBuilder.buildDataSource();
		} catch (Exception e) {
			Log.error("Error in " + this.getClass().getSimpleName() + ".createReportItems: ", e);
			add1LineErrorMessage("report.error.message.general.error");
		}
	}

	protected void writeResultsToBuffer(ByteArrayOutputStream buffer) throws Exception, IOException, UnsupportedEncodingException {
	
        String currentAccessionNumber = null;
        String[] splitBase = null;
        while (csvColumnBuilder.next()) {
            String line = csvColumnBuilder.nextLine();
            String[] splitLine = line.split(",");

            if (splitLine[0].equals(currentAccessionNumber)) {
                merge(splitBase, splitLine);
            } else {
                if (currentAccessionNumber != null && writeAble(splitBase[16].trim())) {
                    
                                   
                    writeConsolidatedBaseToBuffer(buffer, splitBase);
                }
                splitBase = splitLine;
                currentAccessionNumber = splitBase[0];
                }
        }
        if (writeAble(splitBase[16].trim()))
            writeConsolidatedBaseToBuffer(buffer, splitBase);
           }

	
	
	private boolean writeAble(String result) throws ParseException {
		
		       
        String workingResult = result.split("\\(")[0].trim();
       // System.out.println("result=" + result + " / workingResult= " + workingResult);
        String[] splitLine = indicStr.split(":");
        String indic = splitLine[1];
        if (indic.equals("Unsuppressed VL"))
            return workingResult.contains("Log7")|| !workingResult.contains("L") && !workingResult.contains("X") && !workingResult.contains("<") && workingResult.length()>0 
                    && Double.parseDouble(workingResult) >= 1000;// workingResult.length()>=4 &&
                                                                    // !workingResult.contains("L") &&
                                                                    // !workingResult.contains("X") ;
        else if (indic.equals("Suppressed VL"))
            return workingResult.contains("L") || workingResult.contains("<") 
                    || (workingResult.length()>0 && !workingResult.contains("X") && Double.parseDouble(workingResult) < 1000);

        return false;
    }
	
	

	private void merge(String[] base, String[] line) {
		for( int i = 0; i < base.length; ++i){
			if( GenericValidator.isBlankOrNull(base[i])){
				base[i] = line[i];
			}
		}
	}

	protected void writeConsolidatedBaseToBuffer(ByteArrayOutputStream buffer, String[] splitBase) throws IOException,
			UnsupportedEncodingException {

		if (splitBase != null) {
			StringBuffer consolidatedLine = new StringBuffer();
			for (String value : splitBase) {
				consolidatedLine.append(value);
				consolidatedLine.append(",");
			}

			consolidatedLine.deleteCharAt(consolidatedLine.lastIndexOf(","));
			buffer.write(consolidatedLine.toString().getBytes("windows-1252"));
		}
	}

	private CSVColumnBuilder getColumnBuilder(String projectId) {
		//String projectTag = CIColumnBuilder.translateProjectId(projectId);
		return new ForCIDashboardColumnBuilder(dateRange, projectStr);
		
	}
		
		
		/*
		if (projectTag.equals("ARVB")) {
			return new ARVInitialColumnBuilder(dateRange, projectStr);
		} else if (projectTag.equals("ARVS")) {
			return new ARVFollowupColumnBuilder(dateRange, projectStr);
		} else if (projectTag.equalsIgnoreCase("DBS")) {
			return new EIDColumnBuilder(dateRange, projectStr);
		} else if (projectTag.equalsIgnoreCase("VLS")) {
			return new VLColumnBuilder(dateRange, projectStr);
		} else if (projectTag.equalsIgnoreCase("RTN")) {
			return new RTNColumnBuilder(dateRange, projectStr);
		} else if (projectTag.equalsIgnoreCase("IND")) {
			return new RTNColumnBuilder(dateRange, projectStr);
		}
		throw new IllegalArgumentException();
	}

	
	
	
	
	
	/**
	 * @return a list of the correct projects for display
	 */
	protected List<Project> getProjectList() {
		List<Project> projects = new ArrayList<Project>();
		Project project = new Project();
		/*
		project.setProjectName("Antiretroviral Study");
		projects.add(projectDAO.getProjectByName(project, false, false));
		project.setProjectName("Antiretroviral Followup Study");
		projects.add(projectDAO.getProjectByName(project, false, false));
		project.setProjectName("Routine HIV Testing");
		projects.add(projectDAO.getProjectByName(project, false, false));
		project.setProjectName("Early Infant Diagnosis for HIV Study");
		projects.add(projectDAO.getProjectByName(project, false, false));
		project.setProjectName("Viral Load Results");
		projects.add(projectDAO.getProjectByName(project, false, false));
		project.setProjectName("Indeterminate Results");
		projects.add(projectDAO.getProjectByName(project, false, false));
		*/
		
		project.setId("28:Unsuppressed VL");
		project.setProjectName("Unsuppressed VL");
		projects.add(project);
		project = new Project();
		project.setId("28:Suppressed VL");
		project.setProjectName("Suppressed VL");
		projects.add(project);
		
		
		
		return projects;
	}
}