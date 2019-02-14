<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
			us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
		    us.mn.state.health.lims.common.util.DateUtil,
			us.mn.state.health.lims.common.util.StringUtil,
			us.mn.state.health.lims.common.util.Versioning,
			org.owasp.encoder.Encode" %>

<%@ page isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="app" uri="/tags/labdev-view" %>
<%@ taglib prefix="ajax" uri="/tags/ajaxtags" %>

<%!
	IAccessionNumberValidator accessionValidator;
	String basePath = "";
 %>

<%
	accessionValidator = new AccessionNumberValidatorFactory().getValidator();
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
%>

<!-- Creates updated UI. Removing for current release 
<link rel="stylesheet" media="screen" type="text/css" href="<%=basePath%>css/bootstrap.min.css?ver=<%= Versioning.getBuildNumber() %>" />
<link rel="stylesheet" media="screen" type="text/css" href="<%=basePath%>css/openElisCore.css?ver=<%= Versioning.getBuildNumber() %>" />
-->

<script type="text/javascript" src="scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript">


function datePeriodUpdated(selectionElement){
	var selectedValue = selectionElement.options[selectionElement.selectedIndex].value;

    if( selectedValue == "custom"){
    	$("lowerMonth").disabled = false;
    	$("lowerYear").disabled = false;
    	$("upperMonth").disabled = false;
    	$("upperYear").disabled = false;
    }else{
    	clearDate( "lowerMonth");
    	clearDate( "lowerYear");
    	clearDate( "upperMonth");
    	clearDate( "upperYear");
    	
    	$("dateWarning").style.visibility = "hidden";
    }
}

function clearAllDates(){
		setDateWarning( false, "" );
}

function clearDate( id ){
	var element = $(id);
	element.value="";
	element.disabled = true;
	element.style.borderColor = "";	
}
function /*boolean*/ formCorrect(){
	var datePeriod = $("datePeriod");
	
	if( datePeriod ){
		var selectedValue = datePeriod.options[datePeriod.selectedIndex].value;
		if( selectedValue == "custom"){
			var hasMissingValue = false;
						
			if( missingValue("lowerYear")){ hasMissingValue = true;}
			if( missingValue("lowerMonth")){ hasMissingValue = true;}
			if( missingValue("upperYear")){ hasMissingValue = true;}
			if( missingValue("upperMonth")){ hasMissingValue = true;}
		
			if( hasMissingValue){ 
				$("dateWarning").innerHTML = "Dates are required for custom range";
				return false;
			}
			
			var lowerYearValue = $("lowerYear").value;
			var lowerMonthValue = $("lowerMonth").value;
			var upperYearValue = $("upperYear").value;
			var upperMonthValue = $("upperMonth").value;
			
			var low = getCompoundDate( lowerYearValue, lowerMonthValue);
			var upper = getCompoundDate(upperYearValue, upperMonthValue);
			
			if( low >= upper){
				setDateWarning( true, "Start date must be before end date" );
				return false;	
			}
			
			var yearDiff = parseInt(upperYearValue - lowerYearValue );
			
			if( yearDiff > 1 ){
			 	setDateWarning( true, "Maximum date span is 12 months" );
				return false;
			}else if( yearDiff == 1 ){
				var monthDiff = parseInt(upperMonthValue - lowerMonthValue );
				
				if( monthDiff >= 0){
					setDateWarning( true, "Maximum date span is 12 months"  );
					return false;
				}
			}
		}
	}

	return true;
}

function setDateWarning( on, warning ){
				$("lowerYear").style.borderColor = on ? "red" : "";	
				$("lowerMonth").style.borderColor = on ? "red" : "";	
				$("upperYear").style.borderColor = on ? "red" : "";	
				$("upperMonth").style.borderColor = on ? "red" : "";
				$("dateWarning").innerHTML = warning;
}

function /*int*/ getCompoundDate( year, month){
	month = month.length == 1 ? "0" + month : month;
	return parseInt( year + month); 

}

function /*boolean*/ missingValue( id ){
	if( $(id).value == ""){
		$(id).style.borderColor = "red";
		return true;
	}

	return false;
}

function onCancel(){
	var form = window.document.forms[0];
	form.action = "CancelReport.do";
	form.submit();
	return true;
}

function onPrint(){
	if( formCorrect()){
		var form = window.document.forms[0];
		form.action = "ReportPrint.do";
		form.target = "_blank";
		form.method = 'get';
		form.submit();
		return false;
	}
	
	return true;
}

</script>

<h2><c:out value="${form.reportName}"/></h2>

<div class="oe-report">

<form:hidden path="report" id="reportRequest"/>
<form:hidden path="type" id="reportType"/>

<c:if test="${not form.noRequestSpecifications}">

  <c:if test="${form.useAccessionDirect}">
	  <div><strong><%= StringUtil.getContextualMessageForKey("report.enter.labNumber.headline") %></strong></div>
  </c:if>
  
  <div>

	  <c:if test="${form.useAccessionDirect}">
		<span style="padding-left: 10px">
		<c:if test="${form.useAccessionDirect}">
			<%= StringUtil.getContextualMessageForKey("report.from") %>
		</c:if>
		</span>
		<form:input path="accessionDirect"
				   cssClass="input-medium" 
				   maxlength='<%= Integer.toString(accessionValidator.getMaxAccessionLength())%>'
				   />
	  </c:if>
	  <c:if test="${form.useHighAccessionDirect}">
		<span style="padding-left: 10px"><%= StringUtil.getContextualMessageForKey("report.to") %></span>
			<form:input path="highAccessionDirect"
			           cssClass="input-medium"
			           maxlength='<%= Integer.toString(accessionValidator.getMaxAccessionLength())%>'/>
	  </c:if>
	  <c:if test="${form.useAccessionDirect}">
	  
	  	<spring:message code="sample.search.scanner.instructions"/>
	  </c:if>
	  <c:if test="${not form.useAccessionDirect}">
	  	<c:if test="${form.useAccessionDirect}">
	  		<spring:message code="sample.search.scanner.instructions"/>
	  	</c:if>
	  </c:if>
  </div>
  <c:if test="${form.useHighAccessionDirect}">
    <div><span style="padding-left: 10px"><%= StringUtil.getContextualMessageForKey("report.enter.labNumber.detail") %></span></div>
  </c:if>
  <c:if test="${form.usePatientNumberDirect}">
  
	<div><strong><%= StringUtil.getContextualMessageForKey("report.enter.subjectNumber") %></strong></div>
  </c:if>
  <div>

	  <c:if test="${form.usePatientNumberDirect}">
		<div style="padding: 5px 0 0 10px"><form:input path="patientNumberDirect" cssClass="input-medium"/></div>
	  </c:if>

	  <c:if test="${form.useUpperPatientNumberDirect}">
	   <span style="padding-left: 10px"><%= StringUtil.getContextualMessageForKey("report.to") %></span>
		<form:input path="patientUpperNumberDirect" cssClass="input-medium"/>
	  </c:if>
  </div>
  
  <div>
	  <c:if test="${form.useLowerDateRange}">
	  	<span style="padding-left: 10px"><spring:message code="report.date.start"/>&nbsp;<%=DateUtil.getDateUserPrompt()%></span>
		<form:input path="lowerDateRange" cssClass="input-medium" onkeyup="addDateSlashes(this, event);" maxlength="10"/>
	  </c:if>
	  <c:if test="${form.useUpperDateRange}">
	  	<span style="padding-left: 10px"><spring:message code="report.date.end"/>&nbsp;<%=DateUtil.getDateUserPrompt()%></span>
	  	<form:input path="upperDateRange" cssClass="input-medium" maxlength="10" onkeyup="addDateSlashes(this, event);"/>
	  </c:if>
  </div>
 
  <c:if test="${form.useLocationCode}">
  	<div>
   	  <span style="padding-left: 10px"><spring:message code="report.select.service.location"/></span>
      <form:select path="locationCode" cssClass="text" >
		<option value=""></option><form:options items="${form.locationCodeList}" itemLabel="organizationName" itemValue="id" />
	  </form:select>
	</div>
  </c:if>
 
  <c:if test="${form.useProjectCode}">
   	<div>
	  <spring:message code="report.select.project"/>
	  <form:select path="projectCode" cssClass="text" >
		<option value=""></option><form:options  items="${form.projectCodeList}" itemLabel="localizedName" itemValue="id" />
	  </form:select>
	</div>
  </c:if>
  
  <c:if test="${form.usePredefinedDateRanges}">
   	<div>
	   <spring:message code="report.select.date.period"/>
	     <form:select path="datePeriod" cssClass="text" onchange="datePeriodUpdated(this)" id="datePeriod">
	     	<option value='year'><%= StringUtil.getMessageForKey("report.select.date.period.year") %></option>
	     	<option value='months3'><%= StringUtil.getMessageForKey("report.select.date.period.months.3") %></option>
	     	<option value='months6'><%= StringUtil.getMessageForKey("report.select.date.period.months.6") %></option>
	     	<option value='months12'><%= StringUtil.getMessageForKey("report.select.date.period.months.12") %></option>
	     	<option value='custom'><%= StringUtil.getMessageForKey("report.selecte.date.period.custom") %></option>
		 </form:select>
	</div>
	<div>
  	    <spring:message code="report.date.start"/>
			<form:select path="lowerMonth" 
						 cssClass="text" 
						 disabled="true" 
						 id="lowerMonth"
						 onchange="clearAllDates();" >
				<option value=""></option><form:options items="${form.monthList}" itemLabel="value" itemValue="id" />
			</form:select>
			<form:select path="lowerYear" 
						 cssClass="text"  
						 disabled="true" 
						 id="lowerYear" 
						 onchange="clearAllDates();">
				<option value=""></option><form:options items="${form.yearList}" itemLabel="value" itemValue="id" />
			</form:select>
	    <spring:message code="report.date.end"/>
			<form:select path="upperMonth" 
			             cssClass="text"   
			             disabled="true" 
			             id="upperMonth"
			             onchange="clearAllDates();">
				<option value=""></option><form:options items="${form.monthList}" itemLabel="value" itemValue="id" />
			</form:select>
			<form:select path="upperYear" 
			             cssClass="text"   
			             disabled="true" 
			             id="upperYear"
			             onchange="clearAllDates();">
				<option value=""></option><form:options items="${form.yearList}" itemLabel="value" itemValue="id" />
			</form:select>
  			<div id="dateWarning" ></div>
	</div>
  </c:if>
    <c:if test="${not empty form.selectList}">
   	<div>
       <c:set var="selectList" value="${form.selectList}" />
       <span style="padding-left: 10px"><label for="selectList">
       <c:out value="${form.selectList.label}"/></label>
	   <form:select path="selectList.selection" cssClass="text" id="selectList">
		   <option value=""></option><form:options items="${form.selectList.list}" itemLabel="value" itemValue="id" />
       </form:select></span>
	</div>
    </c:if>
</c:if>

<br/>
<div align="left" style="width:80%" >
<c:if test="${not empty form.instructions}">
<c:out value="${form.instructions}"/>
</c:if>
</div>
<div style="margin-left: 50px">
	<input type="button" class="btn" name="printNew" onclick="onPrint();" value="<%=StringUtil.getMessageForKey("label.button.print.new.window") %>">
</div>

</div>