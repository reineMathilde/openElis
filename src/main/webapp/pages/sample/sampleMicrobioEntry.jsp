<%@page import="org.openelisglobal.common.util.SystemConfiguration"%>
<%@page import="org.openelisglobal.common.action.IActionConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="org.openelisglobal.common.formfields.FormFields,
	org.openelisglobal.sample.util.AccessionNumberUtil,
	        org.openelisglobal.common.formfields.FormFields.Field,
	        org.openelisglobal.common.util.ConfigurationProperties,
	        org.openelisglobal.common.util.IdValuePair,
	        org.openelisglobal.common.util.ConfigurationProperties.Property,
	        org.openelisglobal.common.util.DateUtil,
	        org.openelisglobal.internationalization.MessageUtil,
	        org.openelisglobal.common.util.Versioning"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib prefix="ajax" uri="/tags/ajaxtags"%>

<c:set var="formName" value="${form.formName}" />
<%@page import="org.openelisglobal.common.action.IActionConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="org.openelisglobal.common.formfields.FormFields,
	org.openelisglobal.sample.util.AccessionNumberUtil,
	        org.openelisglobal.common.formfields.FormFields.Field,
	        org.openelisglobal.common.util.ConfigurationProperties,
	        org.openelisglobal.common.util.IdValuePair,
	        org.openelisglobal.common.util.ConfigurationProperties.Property,
	        org.openelisglobal.common.util.DateUtil,
	        org.openelisglobal.internationalization.MessageUtil,
	        org.openelisglobal.common.util.Versioning"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ taglib prefix="ajax" uri="/tags/ajaxtags"%>



<c:set var="formName" value="${form.formName}" />
<c:set var="entryDate" value="${form.currentDate}" />


<%
boolean useCollectionDate = FormFields.getInstance().useField(Field.CollectionDate);
boolean useInitialSampleCondition = FormFields.getInstance().useField(Field.InitialSampleCondition);
boolean useSampleNature = FormFields.getInstance().useField(Field.SampleNature);
boolean useCollector = FormFields.getInstance().useField(Field.SampleEntrySampleCollector);
boolean autofillCollectionDate = ConfigurationProperties.getInstance()
		.isPropertyValueEqual(Property.AUTOFILL_COLLECTION_DATE, "true");
%>

<script type="text/javascript" src="scripts/utilities.js?"></script>
<script type="text/javascript" src="scripts/tbUtilities.js"></script>
<script type="text/javascript" src="scripts/additional_utilities.js"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?"></script>
<script type="text/javascript" src="scripts/laborder.js?"></script>

<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?" />
<script type="text/javascript" src="select2/js/select2.min.js"></script>
<link rel="stylesheet" type="text/css" href="select2/css/select2.min.css">
<script type="text/javascript" src="scripts/jquery_ui/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="scripts/jquery_ui/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="scripts/jquery_ui/jquery-ui.theme.min.css"/>


<script type="text/javascript">



fieldValidator = new FieldValidator();
fieldValidator.setRequiredFields(
		new Array('labNo','requestDate','receivedDate','referringSiteCode',
				'lastNameID','dateOfBirthID','genderID',
				'tbSpecimenNature','tbOrderReasons','tbDiagnosticMethods'));
		
function /*void*/setSaveButton() {
	var validToSave = fieldValidator.isAllValid();
	$("saveButtonId").disabled = !validToSave;

}
	function showHideSection(button, targetId) {
		targetId = targetId + button.name
		if (button.value == "+") {
			showSection(button, targetId);
		} else {
			hideSection(button, targetId);
		}
	}

	function showSection(button, targetId) {
		jQuery("#" + targetId).show();
		button.value = "-";
	}

	function hideSection(button, targetId) {
		jQuery("#" + targetId).hide();
		button.value = "+";
	}

	function toggleField(toShow, targetId) {
		if (toShow) {
			jQuery("#" + targetId).show();
			fieldValidator.addRequiredField(targetId.replace('Row', ''));
		} else {
			jQuery("#" + targetId.replace('Row', '')).val(null).trigger(
					'change');
			fieldValidator.removeRequiredField(targetId.replace('Row', ''));
			jQuery("#" + targetId).hide();
		}
	}

	function toggleOrderReasons() {
		var elm = jQuery("#tbOrderReasons");
		toggleField(elm[0].selectedIndex === 1, "tbDiagnosticReasonsRow");
		toggleField(elm[0].selectedIndex === 2, "tbFollowupReasonsRow");
		toggleField(elm[0].selectedIndex === 2, "tbSubjectNumberRow");
		setOrderModified();
	}
	
	function toggleTBFollowupPeriodLine() {
		var elm = jQuery("#tbFollowupReasons");
		toggleField(elm[0].selectedIndex === 1, "tbFollowupPeriodLine1Row");
		toggleField(elm[0].selectedIndex === 2, "tbFollowupPeriodLine2Row");
		setOrderModified();
	}
	
	function toggleTBSampleAspects() {
		var elm = jQuery("#tbDiagnosticMethodsRow");
		var selectedIndices = jQuery("#tbDiagnosticMethodsRow :selected").map((_, e) => e.index).get();
		toggleField(selectedIndices.includes(2), "tbAspectsRow");
		setOrderModified();
	}
	
	//
	//
	//laborder
	//
	function checkAccessionNumber(accessionNumber) {
        //check if empty
        if (!fieldIsEmptyById("labNo")) {
            validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
        }
        else {
             selectFieldErrorDisplay(false, $("labNo"));
        }

        setCorrectSave();
    }
	
	function validateSubjectNumber(field){
		 const tbNumberFormat = /^[0-9]{2}-[0-9]{5}$/;
		 if(field.value){
			  if(tbNumberFormat.test(field.value)){
				  field.classList.remove("error");
				  setCorrectSave();
			  }
			  else{
				  field.classList.add("error");
			  }
		 }
	}


    function processAccessionSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var success = false;

        if (message.firstChild.nodeValue == "valid") {
            success = true;
        }
        var labElement = formField.firstChild.nodeValue;
        selectFieldErrorDisplay(success, $(labElement));

        if (!success) {
            alert(message.firstChild.nodeValue);
        }

        setCorrectSave();
        
    }

    function setCorrectSave(){
        if( window.setSave){
            setSave();
        }else if(window.setSaveButton){
            setSaveButton();
        }
    }

    function getNextAccessionNumber() {
        generateNextScanNumber(processScanSuccess);
    }

    function processScanSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var returnedData = formField.firstChild.nodeValue;

        var message = xhr.responseXML.getElementsByTagName("message").item(0);

        var success = message.firstChild.nodeValue == "valid";

        if (success) {
            $("labNo").value = returnedData;

        } else {
            alert("<%= MessageUtil.getMessage("error.accession.no.next") %>");
            $("labNo").value = "";
        }

        selectFieldErrorDisplay(success, $("labNo"));
        setValidIndicaterOnField(success, "labNo");

        setCorrectSave();
    }

    function processCodeSuccess(xhr) {
        //alert(xhr.responseText);
        var code = xhr.responseXML.getElementsByTagName("code").item(0);
        var success = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue == "valid";

        if (success) {
            jQuery("#requesterCodeId").val(code.getAttribute("value"));
        }
    }
    function setOrderModified(){
        jQuery("#orderModified").val("true");
        orderChanged = true;
        if( window.makeDirty ){ makeDirty(); }

        setCorrectSave();
    }
    
    function  /*void*/ processValidateEntryDateSuccess(xhr){

        //alert(xhr.responseText);
        
        var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

        var isValid = message == "<%=IActionConstants.VALID%>";

        //utilites.js
        selectFieldErrorDisplay( isValid, $(formField));
        setSampleFieldValidity( isValid, formField );
        setSaveButton();

        if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
            alert( '<spring:message code="error.date.inFuture"/>' );
        }else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
            alert( '<spring:message code="error.date.inPast"/>' );
        }
    }
    
    function checkValidEntryDate(date, dateRange, blankAllowed)
    {   
        if((!date.value || date.value == "") && !blankAllowed){
            setSaveButton();
            return;
        } else if ((!date.value || date.value == "") && blankAllowed) {
            setSampleFieldValid(date.id);
            setValidIndicaterOnField(true, date.id);
            return;
        }
        if( !dateRange || dateRange == ""){
            dateRange = 'past';
        }
        isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
    }
    
	function savePage__(action) {
		window.onbeforeunload = null; 
		var form = document.getElementById("mainForm");
		if (action == null) {
			action = "MicrobiologyClassic"
		}
		form.action = action;
		form.submit();
	}
</script>







<c:set var="entryDate" value="${form.currentDate}" />

<script type="text/javascript" src="scripts/utilities.js?"></script>
<script type="text/javascript" src="scripts/tbUtilities.js"></script>
<script type="text/javascript" src="scripts/additional_utilities.js"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?"></script>
<script type="text/javascript" src="scripts/ajaxCalls.js?"></script>
<script type="text/javascript" src="scripts/laborder.js?"></script>

<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?" />
<script type="text/javascript" src="select2/js/select2.min.js"></script>
<link rel="stylesheet" type="text/css" href="select2/css/select2.min.css">
<script type="text/javascript" src="scripts/jquery_ui/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css" href="scripts/jquery_ui/jquery-ui.min.css"/>
<link rel="stylesheet" type="text/css" href="scripts/jquery_ui/jquery-ui.theme.min.css"/>







<h1>${subtitle}</h1>




<script type="text/javascript">
fieldValidator = new FieldValidator();
fieldValidator.setRequiredFields(
		new Array('labNo','requestDate','receivedDate','referringSiteCode',
				'lastNameID','dateOfBirthID','genderID',
				'tbSpecimenNature','tbOrderReasons','tbDiagnosticMethods'));
		
function /*void*/setSaveButton() {
	var validToSave = fieldValidator.isAllValid();
	$("saveButtonId").disabled = !validToSave;

}
	function showHideSection(button, targetId) {
		targetId = targetId + button.name
		if (button.value == "+") {
			showSection(button, targetId);
		} else {
			hideSection(button, targetId);
		}
	}

	function showSection(button, targetId) {
		jQuery("#" + targetId).show();
		button.value = "-";
	}

	function hideSection(button, targetId) {
		jQuery("#" + targetId).hide();
		button.value = "+";
	}

	function toggleField(toShow, targetId) {
		if (toShow) {
			jQuery("#" + targetId).show();
			fieldValidator.addRequiredField(targetId.replace('Row', ''));
		} else {
			jQuery("#" + targetId.replace('Row', '')).val(null).trigger(
					'change');
			fieldValidator.removeRequiredField(targetId.replace('Row', ''));
			jQuery("#" + targetId).hide();
		}
	}

	function toggleOrderReasons() {
		var elm = jQuery("#tbOrderReasons");
		toggleField(elm[0].selectedIndex === 1, "tbDiagnosticReasonsRow");
		toggleField(elm[0].selectedIndex === 2, "tbFollowupReasonsRow");
		toggleField(elm[0].selectedIndex === 2, "tbSubjectNumberRow");
		setOrderModified();
	}
	
	function toggleTBFollowupPeriodLine() {
		var elm = jQuery("#tbFollowupReasons");
		toggleField(elm[0].selectedIndex === 1, "tbFollowupPeriodLine1Row");
		toggleField(elm[0].selectedIndex === 2, "tbFollowupPeriodLine2Row");
		setOrderModified();
	}
	
	function toggleTBSampleAspects() {
		var elm = jQuery("#tbDiagnosticMethodsRow");
		var selectedIndices = jQuery("#tbDiagnosticMethodsRow :selected").map((_, e) => e.index).get();
		toggleField(selectedIndices.includes(2), "tbAspectsRow");
		setOrderModified();
	}
	
	//
	//
	//laborder
	//
	function checkAccessionNumber(accessionNumber) {
        //check if empty
        if (!fieldIsEmptyById("labNo")) {
            validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
        }
        else {
             selectFieldErrorDisplay(false, $("labNo"));
        }

        setCorrectSave();
    }
	
	function validateSubjectNumber(field){
		 const tbNumberFormat = /^[0-9]{2}-[0-9]{5}$/;
		 if(field.value){
			  if(tbNumberFormat.test(field.value)){
				  field.classList.remove("error");
				  setCorrectSave();
			  }
			  else{
				  field.classList.add("error");
			  }
		 }
	}
	
	

    function processAccessionSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var success = false;

        if (message.firstChild.nodeValue == "valid") {
            success = true;
        }
        var labElement = formField.firstChild.nodeValue;
        selectFieldErrorDisplay(success, $(labElement));

        if (!success) {
            alert(message.firstChild.nodeValue);
        }

        setCorrectSave();>
        
    }

    function setCorrectSave(){
        if( window.setSave){
            setSave();
        }else if(window.setSaveButton){
            setSaveButton();
        }
    }

    function getNextAccessionNumber() {
        generateNextScanNumber(processScanSuccess);
    }

    function processScanSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var returnedData = formField.firstChild.nodeValue;

        var message = xhr.responseXML.getElementsByTagName("message").item(0);

        var success = message.firstChild.nodeValue == "valid";

        if (success) {
            $("labNo").value = returnedData;

        } else {
            alert("<%= MessageUtil.getMessage("error.accession.no.next") %>");
            $("labNo").value = "";
        }

        selectFieldErrorDisplay(success, $("labNo"));
        setValidIndicaterOnField(success, "labNo");

        setCorrectSave();
    }

    function processCodeSuccess(xhr) {
        //alert(xhr.responseText);
        var code = xhr.responseXML.getElementsByTagName("code").item(0);
        var success = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue == "valid";

        if (success) {
            jQuery("#requesterCodeId").val(code.getAttribute("value"));
        }
    }
    function setOrderModified(){
        jQuery("#orderModified").val("true");
        orderChanged = true;
        if( window.makeDirty ){ makeDirty(); }

        setCorrectSave();
    }
    
    function  /*void*/ processValidateEntryDateSuccess(xhr){

        //alert(xhr.responseText);
        
        var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

        var isValid = message == "<%=IActionConstants.VALID%>";

        //utilites.js
        selectFieldErrorDisplay( isValid, $(formField));
        setSampleFieldValidity( isValid, formField );
        setSaveButton();

        if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
            alert( '<spring:message code="error.date.inFuture"/>' );
        }else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
            alert( '<spring:message code="error.date.inPast"/>' );
        }
    }
    
    function checkValidEntryDate(date, dateRange, blankAllowed)
    {   
        if((!date.value || date.value == "") && !blankAllowed){
            setSaveButton();
            return;
        } else if ((!date.value || date.value == "") && blankAllowed) {
            setSampleFieldValid(date.id);
            setValidIndicaterOnField(true, date.id);
            return;
        }
        if( !dateRange || dateRange == ""){
            dateRange = 'past';
        }
        isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
    }
    
	function savePage__(action) {
		window.onbeforeunload = null; 
		var form = document.getElementById("mainForm");
		if (action == null) {
			action = "MicrobiologyClassic"
		}
		form.action = action;
		form.submit();requestDate
	}
</script>




    
    <!--  -->
 


<div id="tb_container">
	<%=MessageUtil.getContextualMessage("referring.order.number")%>:
	<form:input id="externalOrderNumber" path="externalOrderNumber"
		onchange="checkOrderReferral();makeDirty();" />
	<input type="button" name="searchExternalButton"
		value='<%=MessageUtil.getMessage("label.button.search")%>'
		onclick="checkOrderReferral();makeDirty();">
	<%=MessageUtil.getContextualMessage("referring.order.not.found")%>
	<hr style="width: 100%; height: 1px" />
	<br />
	<form:hidden path="modified" id="orderModified"/>
    <form:hidden path="sampleId" id="sampleId"/>
    
    <!--  -->
    <div id=orderSearchSection>
		<input type="button" name="showHide" value='-'
			onclick="showHideSection(this, 'orderSearch');" id="orderSearchId">
		<%=MessageUtil.getContextualMessage("sample.entry.search.label") %>
		<table id="orderSearchshowHide" style="display:none">
			<tr>
				<td style="width: 35%"><%=MessageUtil.getContextualMessage("quick.entry.accession.number")%>:</td>
				<td style="width: 65%"><form:input path="labnoForSearch"
						maxlength='<%=Integer.toString(AccessionNumberUtil.getMaxAccessionLength())%>'
						onchange="" cssClass="text" id="searchByLabNo" />
					<input type="button" name="searchButton" class="patientSearch" value="<%= MessageUtil.getMessage("label.patient.search")%>"
           			id="searchButton" onclick="searchOrder()">
				</td>
			</tr>
		</table>
	</div>
    
    <hr style="width: 100%; height: 1px" />
	<br />
    
   
	<div id=orderEntrySection>
		<input type="button" name="showHide" value='-'
			onclick="showHideSection(this, 'orderDisplay');" id="orderSectionId">
		<%=MessageUtil.getContextualMessage("sample.entry.order.label")%>
		<span class="requiredlabel">*</span>
		<table id="orderDisplayshowHide">
			<tr>
				<td style="width: 35%"><%=MessageUtil.getContextualMessage("quick.entry.accession.number")%>
					:<span	class="requiredlabel">*</span></td>
				<td style="width: 65%"><form:input path="labNo"
						maxlength='<%=Integer.toString(AccessionNumberUtil.getMaxAccessionLength())%>'
						onchange="checkAccessionNumber(this);" cssClass="text" id="labNo" />

					<spring:message code="sample.entry.scanner.instructions"
						htmlEscape="false" /> <input type="button"
					id="generateAccessionButton"
					value='<%=MessageUtil.getMessage("sample.entry.scanner.generate")%>'
					onclick="setOrderModified();getNextAccessionNumber(); "
					class="textButton"></td>
			</tr>

			<tr>
				<td><spring:message code="sample.entry.requestDate" />: <span
					class="requiredlabel">*</span><span style="font-size: xx-small;"><%=DateUtil.getDateUserPrompt()%></span></td>
				<td><form:input path="requestDate" id="requestDate"
						cssClass="required"
						onchange="setOrderModified();checkValidEntryDate(this, 'past')"
						onkeyup="addDateSlashes(this, event);" maxlength="10" />
			</tr>
			<tr>
				<td><%=MessageUtil.getContextualMessage("quick.entry.received.date")%>
					: <span class="requiredlabel">*</span> <span
					style="font-size: xx-small;"><%=DateUtil.getDateUserPrompt()%>
				</span></td>
				<td colspan="2"><form:input path="receivedDate"
						onchange="checkValidEntryDate(this, 'past');setOrderModified();"
						onkeyup="addDateSlashes(this, event);" maxlength="10"
						cssClass="text required" id="receivedDate" /></td>
			</tr>
			<tr>
				<td><%=MessageUtil.getContextualMessage("sample.bacterio.reference.unit")%>
					: <span class="requiredlabel">*</span></td>
				<td colspan="2"><form:select path="referringSiteCode"
						id="referringSiteCode" cssClass="centerCodeClass" onchange="setOrderModified();">
						<option value=" "></option>
						<form:options items="${form.referralOrganizations}"
							itemLabel="value" itemValue="id" />
					</form:select></td>
			</tr>
			<tr>
          <td><%=MessageUtil.getContextualMessage("sample.tbclassic.service.unit")%>: </td>
        <td colspan="2">
        <form:select path="orginalservice" id="orginalservice" cssClass="centerCodeClass" onchange="setOrderModified(); checkOtherSelection();">
            <option value=" "></option>
            <form:options items="${form.orginalservices}" itemLabel="value" itemValue="id" />
        </form:select>
       </td>
    </tr>
<tr id="otherInputRow" style="display: none;">
    <td><label for="otherService"><%=MessageUtil.getContextualMessage("sample.entry.provider.precision")%>:</label></td>
    <td colspan="2">
        <input type="text" id="otherService" name="otherService" path="otherOrginalservice" />
    </td>

</tr>
			
            	<tr>
				<td><%=MessageUtil.getContextualMessage("sample.entry.prescription.type")%>
					: </td>
				<td colspan="2"><form:select path="prescriptionType"
						id="prescriptionType" cssClass="centerCodeClass" onchange="setOrderModified();">
						<option value="prescriptionType"></option>
						<form:options items="${form.prescriptionTypes}"
							itemLabel="value" itemValue="id" />
					</form:select></td>
			    </tr>
            
          <tr>	
    <td><%=MessageUtil.getContextualMessage("sample.entry.urgent")%>: <span class="requiredlabel">*</span></td>	
    <td colspan="2">
        <c:forEach var="info" items="${form.urgents}">
            <form:radiobutton path="urgent" id="${info.id}" value="${info.id}" />
            <spring:message code="${info.value}" />
        </c:forEach>
    </td>
</tr>	
			
			<tr class="provider-info-row provider-extra-info-row">
				<td><%=MessageUtil.getContextualMessage("sample.entry.provider.bacterio.name")%>:
				</td>
				<td><form:input path="providerLastName" id="providerLastName"  onkeyup="this.value = this.value.toUpperCase();"
						onchange="" /></td>

			</tr>
			<tr class="provider-info-row provider-extra-info-row">
				<td><%=MessageUtil.getContextualMessage("sample.entry.provider.bacterio.firstName")%>:
				</td>
				<td><form:input path="providerFirstName" id="providerFirstName"  onkeyup="this.value = this.value.toUpperCase();"
						onchange="" /></td>
			</tr>
			
			<!-- nouveau  -->
			<tr class="provider-info-row provider-extra-info-row">
    <td><%=MessageUtil.getContextualMessage("humansampleone.provider.bacterio.workPhone")%>: </td>
    <td>
        <form:input path="providerWorkPhone" id="providerWorkPhone"
            onkeyup="validatePhoneNumber();" 
            cssClass="required"
        />
        <span id="phoneError" style="color:red; display:none;"><%=MessageUtil.getContextualMessage("humansampleone.bacterio.workPhone")%></span>
    </td>
</tr>
			
				
			
			
			<tr class="provider-info-row provider-extra-info-row">
				<td style="width:15%" ><%=MessageUtil.getContextualMessage("sample.entry.project.bacterio.email")%>:
				</td>
				<td style="width:15%"><form:input path="providerEmail" id="providerEmail"
						onchange="" /></td>
			</tr>
			
 <tr>
        <td>
            <%= MessageUtil.getContextualMessage("sample.entry.epidemiologique") %> : 
          
        </td>
        <td colspan="2">
            <form:select path="epidemiologique"   id="epidemiologique" cssClass="tbSpecimenNatureClass" style="min-width:200px" onclick="showAdditionalFields();">
                        <option value="">&nbsp;</option>
                
                <form:option value="S1"><%= MessageUtil.getContextualMessage("S1") %></form:option>
                <form:option value="S2"><%= MessageUtil.getContextualMessage("S2") %></form:option>
                <form:option value="S3"><%= MessageUtil.getContextualMessage("S3") %></form:option>
                <form:option value="S4"><%= MessageUtil.getContextualMessage("S4") %></form:option>
                <form:option value="S5"><%= MessageUtil.getContextualMessage("S5") %></form:option>
                <form:option value="S6"><%= MessageUtil.getContextualMessage("S6") %></form:option>
                <form:option value="S7"><%= MessageUtil.getContextualMessage("S7") %></form:option>
                <form:option value="S8"><%= MessageUtil.getContextualMessage("S8") %></form:option>
                 <form:option value="S9"><%= MessageUtil.getContextualMessage("S9") %></form:option>
                <form:option value="S10"><%= MessageUtil.getContextualMessage("S10") %></form:option>
                <form:option value="S11"><%= MessageUtil.getContextualMessage("S11") %></form:option>
                 <form:option value="S12"><%= MessageUtil.getContextualMessage("S12") %></form:option>
                <form:option value="S13"><%= MessageUtil.getContextualMessage("S14") %></form:option>
                <form:option value="S14"><%= MessageUtil.getContextualMessage("S15") %></form:option>
                 <form:option value="S16"><%= MessageUtil.getContextualMessage("S16") %></form:option>
                <form:option value="S17"><%= MessageUtil.getContextualMessage("S17") %></form:option>
                <form:option value="S18"><%= MessageUtil.getContextualMessage("S18") %></form:option>
                 <form:option value="S19"><%= MessageUtil.getContextualMessage("S19") %></form:option>
                <form:option value="S20"><%= MessageUtil.getContextualMessage("S20") %></form:option>
                <form:option value="S21"><%= MessageUtil.getContextualMessage("S21") %></form:option>
                <form:option value="S22"><%= MessageUtil.getContextualMessage("S22") %></form:option>
                <form:option value="S23"><%= MessageUtil.getContextualMessage("S23") %></form:option>
                <form:option value="S24"><%= MessageUtil.getContextualMessage("S24") %></form:option>
                <form:option value="S25"><%= MessageUtil.getContextualMessage("S25") %></form:option>
                <form:option value="S26"><%= MessageUtil.getContextualMessage("S26") %></form:option>
                <form:option value="S27"><%= MessageUtil.getContextualMessage("S27") %></form:option>
                 <form:option value="S28"><%= MessageUtil.getContextualMessage("S28") %></form:option>
                <form:option value="S29"><%= MessageUtil.getContextualMessage("S29") %></form:option>
                <form:option value="S30"><%= MessageUtil.getContextualMessage("S30") %></form:option>
                <form:option value="S31"><%= MessageUtil.getContextualMessage("S31") %></form:option>
                <form:option value="S32"><%= MessageUtil.getContextualMessage("S32") %></form:option>
                <form:option value="S34"><%= MessageUtil.getContextualMessage("S34") %></form:option>
                 <form:option value="S35"><%= MessageUtil.getContextualMessage("S35") %></form:option>
                <form:option value="S36"><%= MessageUtil.getContextualMessage("S36") %></form:option>
                <form:option value="S37"><%= MessageUtil.getContextualMessage("S37") %></form:option>
                 <form:option value="38"><%= MessageUtil.getContextualMessage("S38") %></form:option>
                <form:option value="S39"><%= MessageUtil.getContextualMessage("S39") %></form:option>
                <form:option value="S40"><%= MessageUtil.getContextualMessage("S40") %></form:option>
                 <form:option value="S41"><%= MessageUtil.getContextualMessage("S41") %></form:option>
                <form:option value="S42"><%= MessageUtil.getContextualMessage("S42") %></form:option>
                <form:option value="S43"><%= MessageUtil.getContextualMessage("S43") %></form:option>
                <form:option value="S44"><%= MessageUtil.getContextualMessage("S44") %></form:option>
                <form:option value="S45"><%= MessageUtil.getContextualMessage("S45") %></form:option>
                <form:option value="S46"><%= MessageUtil.getContextualMessage("S46") %></form:option>
                <form:option value="S47"><%= MessageUtil.getContextualMessage("S47") %></form:option>
                <form:option value="S48"><%= MessageUtil.getContextualMessage("S48") %></form:option>
                <form:option value="S49"><%= MessageUtil.getContextualMessage("S49") %></form:option>
                <form:option value="S50"><%= MessageUtil.getContextualMessage("S50") %></form:option>
                <form:option value="S51"><%= MessageUtil.getContextualMessage("S51") %></form:option>
                <form:option value="S52"><%= MessageUtil.getContextualMessage("S52") %></form:option>
            </form:select>
        </td>
        <td></td>
    </tr>	
			<tr>
				<td><%=MessageUtil.getContextualMessage("sample.entry.patientPayment")%>:
					
				<td colspan="2"><form:select path="providerpayment"
						id="providerpayment" cssClass="centerCodeClass" onchange="setOrderModified();">
						<option value=" "></option>
						<form:options items="${form.providerpayments}"
							itemLabel="value" itemValue="id" />
					</form:select></td>
			 </tr>
			
			<tr class="spacerRow">
				<td>&nbsp;</td>
			</tr>
		</table>
		<hr style="width: 100%; height: 2px" />
	</div>
	<br />
	
	
	<div id=patientEntrySection>
		<input type="button" name="showHide" value='-'
			onclick="showHideSection(this, 'patientDisplay');"
			id="patientSectionId">
		<%=MessageUtil.getContextualMessage("sample.entry.patient")%>
		<span class="requiredlabel">*</span>
		<table id="patientDisplayshowHide">
			<tr>
				<td style=""><spring:message code="patient.epiLastName" /> : <span
					class="requiredlabel">*</span></td>
				<td><form:input path="patientLastName" id="lastNameID"
						onchange="setOrderModified();"  onkeyup="this.value = this.value.toUpperCase();"/></td>
				<td style=""><spring:message code="patient.epiFirstName" /> :<span
					class="requiredlabel"></span></td>
				<td><form:input path="patientFirstName" id="firstNameID"
					 onkeyup="this.value = this.value.toUpperCase();"	onchange="" size="25" /></td>
			</tr>
			<tr>
				
						
	<td style=""><spring:message code="person.phone" />: <span class="requiredlabel">*</span></td>
<td>
    <form:input path="patientPhone"  id="patientPhone" 
        onkeyup="validatePhoneNumberPatient();" />
    <span id="phoneErrorPatient" style="color:red; display:none;"> <%=MessageUtil.getContextualMessage("humansampleone.bacterio.workPhonePatient")%>
    
    </span>
</td>		
		
				  <td style=""><spring:message code="person.streetAddress" />:</td>
             <td><form:input path="personndomicile" cssClass="text" size="25" id="personndomicile" 
                   onkeyup="this.value = this.value.toUpperCase();" /></td>

			</tr>
		
		
	<tr>
				<td style=""><spring:message code="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>:
					<span class="requiredlabel">*</span></td>
				<td><form:input path="patientBirthDate"
						onkeyup="addDateSlashes(this,event);"
						onchange="checkValidEntryDate(this, 'past');convertToAge(this,'ageYears');"
						id="dateOfBirthID" cssClass="text" size="20" maxlength="10"/>
					<div id="patientbirthDateMessage" class="blank"></div></td>
				<td style=""><spring:message code="patient.age" />:</td>
				<td><form:input path="patientAge"
						onchange="handleAgeChange();" id="ageYears" cssClass="text"
						size="3" maxlength="3" placeholder="years" />
					<div class="blank">
						<spring:message code="years.label" />
					</div>
					<div id="ageYearsMessage" class="blank"></div></td>
				<td style="padding: 0; margin: 0;"><spring:message code="patient.gender" />: <span
					class="requiredlabel">*</span></td>
				<td style="padding: 0; margin: 0;"><form:select path="patientGender" id="genderID" onchange="setOrderModified();checkPregnancyField();">
						<option value=" "></option>
						<form:options items="${form.genders}" itemLabel="value"
							itemValue="id" />
					</form:select></td>
					
					
					<td id="pregnancyStatusContainer" style="display:none;">
           <span style="margin-right: 10px;"><%=MessageUtil.getContextualMessage("patient.enceinte")%>:</span>
           <form:select path="pregnancyStatus" id="pregnancyStatus" >
            <option value="non">Non</option>
        <option value="oui">Oui</option>
       </form:select>
</td>
			</tr>
	
		
		
		
				
		<tr>
				<td style=""><spring:message code="patient.NationalID" />:</td>
				<td><form:input path="patientNationalityId" cssClass="text" onchange=""
						id="patientNationalityId" /></td>
						
						
						   <td><spring:message code="region.browse.title" />:</td>
                <td>
                    <form:select path="patientRegion" id="patientRegion" onchange="loadDistricts(this.value);">
                        <option value=" "></option>
                        <form:options items="${form.patientRegions}" itemLabel="value" itemValue="id" />
                    </form:select>
                </td>
         </tr>
 	
	  <tr>
	      
                <td style=""><spring:message code="city.add.bacterio.title" />:</td>
                 <td><form:input path="patientVilleVillage" cssClass="text" size="25" id="patientVilleVillage" 
                onkeyup="this.value = this.value.toUpperCase();" /></td>
	  
	  
            <td style=""><spring:message code="person.bacterio.commune" />:</td>
               <td><form:input path="personcommune" cssClass="text" size="25" id="personcommune" 
                 onkeyup="this.value = this.value.toUpperCase();" /></td>
         
    </tr>
      <tr>
           
					<td><%=MessageUtil.getContextualMessage("patient.education")%>
						:</td>
					<td><form:select path="education"
							id="education" cssClass="tbOrderReasonsClass"
							style="min-width:200px" onchange="">
							<option value="">&nbsp;</option>
							<form:options items="${form.educations}" itemLabel="value"
								itemValue="id" />
						</form:select></td>
					
					   <td style=""><spring:message code="occupation.add.bacterio.title" />:</td>
             <td><form:input path="patientProfession" cssClass="text" size="25" id="patientProfession" 
        onkeyup="this.value = this.value.toUpperCase();" /></td>
		
</tr>

<tr>
    <td><%= MessageUtil.getContextualMessage("sample.entry.hospitalisation") %>:</td>
    <td >
        <form:radiobutton path="hospitalisationEnCour" id="hospitalisationYes" value="oui" onclick="toggleChambreField(true)" />
        <label for="hospitalisationYes"><%= MessageUtil.getContextualMessage("label.yes") %></label>

        <form:radiobutton path="hospitalisationEnCour" id="hospitalisationNo" value="non" onclick="toggleChambreField(false)" />
        <label for="hospitalisationNo"><%= MessageUtil.getContextualMessage("label.no") %></label>
    </td>


    <td><spring:message code="numero.de.chambre" />:</td>
    <td>
        <form:input path="numeroDeChambre" cssClass="text" size="25" id="numeroDeChambre" disabled="true" />
    </td>
</tr>
	
		<tr>
    <td><spring:message code="clinical.information" />:</td>
    <td colspan="2">
        <c:forEach var="info" items="${form.microbioclinicalinfos}">
            <form:checkbox path="clinicalInformation" id="${info.id}" value="${info.id}" onclick="toggleOtherField(this, '${info.value}');" />
            <spring:message code="${info.value}" />
        </c:forEach>
        <!-- Champ texte caché -->
        <div id="otherInputRowClinical" style="display: none;">
            <label for="otherInfo"><%=MessageUtil.getContextualMessage("sample.entry.provider.precision")%>:</label>
            <input type="text" id="otherInfo" name="otherInfo" path="otherInfoClinical"/>
        </div>
    </td>
</tr>
         
         
    
         
         
         

		<!-- Antécédents d’antibiothérapies -->
		<tr>
    <!-- Label général pour Antécédents d’antibiothérapies -->
    <td colspan="4"><strong><%= MessageUtil.getContextualMessage("sample.entry.antibiotherapie") %></strong></td>
</tr>
<tr>
    <td><%= MessageUtil.getContextualMessage("sample.entry.antibiotherapiederniermois") %>: <span class="requiredlabel">*</span></td>
    <td colspan="2">
        <form:radiobutton path="antibiotherapieRecent" id="antibiotherapieYes" value="oui" onclick="toggleAntibiotherapieFields(true)" />
        <label for="antibiotherapieYes"><%= MessageUtil.getContextualMessage("label.yes") %></label>

        <form:radiobutton path="antibiotherapieRecent" id="antibiotherapieNo" value="non" onclick="toggleAntibiotherapieFields(false)" />
        <label for="antibiotherapieNo"><%= MessageUtil.getContextualMessage("label.no") %></label>
    </td>
</tr>

<tr id="antibiotherapieFields" style="display: none;">
    <td><%= MessageUtil.getContextualMessage("sample.entry.antibiotiques") %>:</td>
    <td colspan="2" id="antibiotiqueFields">
        <form:input path="antibiotique1" cssClass="text" size="25" placeholder="Antibiotique 1" />
        <form:input path="antibiotique2" cssClass="text" size="25" placeholder="Antibiotique 2" />
        <form:input path="antibiotique3" cssClass="text" size="25" placeholder="Antibiotique 3" />
    </td>
    
     <td>
        <button type="button" onclick="addAntibiotiqueField()"><%= MessageUtil.getContextualMessage("label.plus") %></button>
    </td>
</tr>


<tr id="dureeFields" style="display: none;">
    <td><%= MessageUtil.getContextualMessage("sample.entry.duree") %>:</td>
    <td>
        <form:input path="dureeTraitement" cssClass="text" size="25" placeholder="Durée (jours)" />
    </td>
</tr>
<tr id="encoursFields" style="display: none;">
    <td><%= MessageUtil.getContextualMessage("sample.entry.traitementEncours") %>:</td>
    <td colspan="2">
        <form:radiobutton path="traitementEncours" id="encoursYes" value="oui" />
        <label for="encoursYes"><%= MessageUtil.getContextualMessage("label.yes") %></label>

        <form:radiobutton path="traitementEncours" id="encoursNo" value="non" />
        <label for="encoursNo"><%= MessageUtil.getContextualMessage("label.no") %></label>
    </td>
</tr>


	<tr>
    <!-- Label général pour Antecedent Hospitalisation-->
    <td colspan="4"><strong><%= MessageUtil.getContextualMessage("sample.entry.hospitalisationRecent") %></strong></td>
</tr>


<tr>
    <td><%= MessageUtil.getContextualMessage("sample.entry.nombreHospitalisations") %>:</td>
    <td >
        <form:radiobutton path="hospitalisationRecent" id="hospitalisationRecentYes" value="oui" onclick="toggleHospitalisationFields(true)" />
        <label for="hospitalisationRecentYes"><%= MessageUtil.getContextualMessage("label.yes") %></label>

        <form:radiobutton path="hospitalisationRecent" id="hospitalisationRecentNo" value="non" onclick="toggleHospitalisationFields(false)" />
        <label for="hospitalisationRecentNo"><%= MessageUtil.getContextualMessage("label.no") %></label>
    </td>
</tr>
<tr id="hospitalisationFields" style="display: none;">

					
		<td><%=MessageUtil.getContextualMessage("sample.tbclassic.service.unit")%>:
       
        <form:select path="orginalservice" id="orginalservice" cssClass="centerCodeClass" onchange="setOrderModified(); checkOtherSelection();">
            <option value=" "></option>
            <form:options items="${form.orginalservices}" itemLabel="value" itemValue="id" />
        </form:select>
       </td>
       
      
        <td><%= MessageUtil.getContextualMessage("sample.entry.nombredefoishospitalisation") %>:

        <form:input path="nombreHospitalisations" cssClass="text" size="25" placeholder="Nombre de fois" />
    </td>

    
    
</tr>



<!-- Bouton pour afficher le champ -->







    	<tr id="gestesInvasif">
					<td><%=MessageUtil.getContextualMessage("sample.entry.gestesInvasifs")%>
						:</td>
					<td colspan="2"><form:select path="gestesInvasif"
							id="gestesInvasif" cssClass="tbOrderReasonsClass"
							style="min-width:200px" onchange="">
							<option value="">&nbsp;</option>
							<form:options items="${form.gestesInvasifs}" itemLabel="value"
								itemValue="id" />
						</form:select></td>
					<td></td>
				</tr>
						
				
    	<tr id="dispositif">
					<td><%=MessageUtil.getContextualMessage("sample.entry.dispositif")%>
						:</td>
					<td colspan="2"><form:select path="dispositif"
							id="dispositif" cssClass="tbOrderReasonsClass"
							style="min-width:200px" onchange="">
							<option value="">&nbsp;</option>
							<form:options items="${form.dispositifs}" itemLabel="value"
								itemValue="id" />
						</form:select></td>
					<td></td>
				</tr>


			<tr class="spacerRow">
				<td>&nbsp;</td>
			</tr>
			
			
		</table>
		<hr style="width: 100%; height: 2px" />		
		
	</div>
	
	<br />
	<div id=sampleEntrySection>
		<input type="button" name="showHide" value='-'
			onclick="showHideSection(this, 'sampleDisplay');"
			id="sampleSectionId">
		<%=MessageUtil.getContextualMessage("sample.entry.sampleList.label")%>
		<span class="requiredlabel">*</span>
		<div id="sampleDisplayshowHide">
			<table>
  <!-- Sélection de la nature du spécimen -->

<tr>
    <td>
        <%= MessageUtil.getContextualMessage("sample.tb.specimen.nature") %> : 
    </td>
    <td colspan="2">
        <form:select path="tbSpecimen"
                     id="tbSpecimenNaturer"
                     cssClass="tbSpecimenNatureClass"
                     style="min-width:200px"
                     onclick="toggleTBSampleAspects();showPanelAndTests(this);toggleAdditionalField(this); ">
            <option value="">&nbsp;</option>
            <form:options items="${form.microbioSpecimenNatures}" itemLabel="value" itemValue="id" />
        </form:select>
    </td>
</tr>



<!-- Champs additionnels -->
<!-- Champs additionnels -->
<tr id="nonConformityField" style="display: none;">
    <td>
        <label for="nonConformityNature"><%= MessageUtil.getContextualMessage("sample.tb.specimen.nonconformity.nature") %> :</label>
    </td>
    <td colspan="2">
        <form:select path="nonConformityNature" id="" style="display: inline-block;">
            <option value="">&nbsp;</option>
            <form:options items="${form.noConformitynatures}" itemLabel="value" itemValue="id" />
        </form:select>
    </td>
</tr>

<tr id="collectionDateField" style="display: none;">
    <td>
        <spring:message code="sample.tb.specimen.collectionDate" />: 
        <span class="requiredlabel">*</span>
        <span style="font-size: xx-small;"><%= DateUtil.getDateUserPrompt() %></span>
    </td>
    <td>
        <form:input path="tbCollectionDate" id="tbCollectionDate"
                    cssClass="required"
                    onchange="setOrderModified(); checkValidEntryDate(this, 'past')"
                    onkeyup="addDateSlashes(this, event);" maxlength="10" />
    </td>
</tr>

<tr id="collectionTimeField" style="display: none;">
    <td>
        <label for="tbCollectionTime"><%= MessageUtil.getContextualMessage("sample.tb.specimen.collectionTime") %> :</label>
    </td>
    <td colspan="2">
        <form:input path="tbCollectionTime" id="tbCollectionTime" type="time" style="display: inline-block;" />
    </td>
</tr>

<tr id="collecteurNameField" style="display: none;">
    <td>
        <label for="collecteurname"><spring:message code="person.collecteurname" />:</label>
    </td>
    <td colspan="2">
        <form:input path="collecteurname" cssClass="text" size="25" id="collecteurname" 
                    onkeyup="this.value = this.value.toUpperCase();" />
    </td>
</tr>


				
				
			
				
				
			</table>
			<br />
			<div id="testSelections" class="testSelections">
				<table style="margin-left: 1%; width: 60%;" id="addTables">
					<tr>
						<td style="width: 30%; vertical-align: top;"><span
							class="caption"> <spring:message
									code="sample.entry.panels" />
						</span></td>
						<td style="width: 70%; vertical-align: top; margin-left: 3%;">
							<span class="caption"> <spring:message
									code="sample.entry.available.tests" />
						</span>
						</td>
					</tr>
					<tr>
						<td style="width: 30%; vertical-align: top;">
							<table style="width: 97%" id="addPanelTableContainer"
								class="table addPanelTableContainer">
								<thead><tr>
									<th style="width: 20%">&nbsp;</th>
									<th style="width: 80%"><spring:message
											code="sample.entry.panel.name" /></th>
								</tr>
								</thead>
								<tbody id="addPanelTable">
								
								</tbody>

							</table>
						</td>
						<td style="width: 70%; vertical-align: top; margin-left: 3%;">
							<table style="width: 97%" id="addTestTableContainer" class="table addTestTableContainer">
								<tr>
									<th style="width: 5%">&nbsp;</th>
									<th style="width: 50%"><spring:message
											code="sample.entry.available.test.names" /></th>
									<th style="width: 40%; display: none;" id="sectionHead">
										Section</th>
									<th style="width: 20%">&nbsp;</th>
								</tr>
								<tbody id="addTestTable"></tbody>

							</table>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<hr style="width: 100%; height: 2px" />
	</div>
</div>



<br />









<script type="text/javascript">

function toggleChambreField(enable) {
    document.getElementById('numeroDeChambre').disabled = !enable;
}

// Initial check to disable or enable the field based on the initial value
window.onload = function() {
    var yesChecked = document.getElementById('hospitalisationYes').checked;
    toggleChambreField(yesChecked);
}




function toggleAntibiotherapieFields(enable) {
    document.getElementById('antibiotherapieFields').style.display = enable ? '' : 'none';
    document.getElementById('dureeFields').style.display = enable ? '' : 'none';
    document.getElementById('encoursFields').style.display = enable ? '' : 'none';
}

function toggleHospitalisationFields(enable) {
    document.getElementById('hospitalisationFields').style.display = enable ? '' : 'none';
}

// Initial check to disable or enable the fields based on the initial values
window.onload = function() {
    var antibiotherapieChecked = document.getElementById('antibiotherapieYes').checked;
    toggleAntibiotherapieFields(antibiotherapieChecked);

    var hospitalisationChecked = document.getElementById('hospitalisationRecentYes').checked;
    toggleHospitalisationFields(hospitalisationChecked);
}










// Helper function to add a test row
function addTestRow(testName) {
    const testTable = document.getElementById('addTestTable');
    const newRow = document.createElement('tr');

    const checkboxCell = document.createElement('td');
    const testCheckbox = document.createElement('input');
    testCheckbox.type = 'checkbox';
    testCheckbox.checked = true; // Automatically select the test
    testCheckbox.value = testName;
    checkboxCell.appendChild(testCheckbox);

    const nameCell = document.createElement('td');
    nameCell.textContent = testName;

    newRow.appendChild(checkboxCell);
    newRow.appendChild(nameCell);
    testTable.appendChild(newRow);
}

// Handling minor and multiple errors
function handleMinorError(element) {
    element.style.backgroundColor = '#ffcccc'; // Highlight error
    element.addEventListener('mouseout', () => {
        element.style.backgroundColor = ''; // Remove highlight on mouse out
    });
}

function purgeAllSamples() {
    if (confirm('Are you sure you want to delete all samples?')) {
        document.getElementById('addTestTable').innerHTML = ''; // Clear the test table
        document.getElementById('addPanelTableContainer').reset(); // Reset the panel selections
    }
}

// Saving the selections
function saveSelections() {
    // Add logic here to save the selected tests
    alert('Selections have been saved.');
}


function addTestRow(testName) {
    const testTable = document.getElementById('addTestTable');
    const row = testTable.insertRow();

    const checkboxCell = row.insertCell(0);
    const nameCell = row.insertCell(1);
    const sectionCell = row.insertCell(2);
    const emptyCell = row.insertCell(3);

    checkboxCell.innerHTML = '<input type="checkbox" checked>';
    nameCell.textContent = testName;
    sectionCell.textContent = ''; // Vous pouvez ajouter du contenu si nécessaire
    emptyCell.innerHTML = '&nbsp;';
}







let antibiotiquesCount = 3;

function addAntibiotiqueField() {
    antibiotiquesCount++;
    const antibiotiqueFields = document.getElementById('antibiotiqueFields');

    const newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.name = 'antibiotique' + antibiotiquesCount;
    newInput.className = 'text';
    newInput.size = 25;
    newInput.placeholder = 'Antibiotique ' + antibiotiquesCount;

    antibiotiqueFields.appendChild(newInput);
}



//gerer autre de service d origine 
function checkOtherSelection() {
        var select = document.getElementById('orginalservice');
        var otherInputRow = document.getElementById('otherInputRow');
        
        // Vérifiez si "Autres" est sélectionné
        if (select.options[select.selectedIndex].text === "Autres") {
            otherInputRow.style.display = "table-row"; // Affiche la ligne de l'input
        } else {
            otherInputRow.style.display = "none"; // Masque la ligne de l'input
        }
    }


//gerer autres pour clinicalInformation

  function toggleOtherField(checkbox, infoValue) {
        var otherInputRow = document.getElementById('otherInputRowClinical');

        // Vérifiez si "Autres" est sélectionné
       if ((infoValue === "Autres" || infoValue === "Others") && checkbox.checked){
            otherInputRow.style.display = "block"; // Affiche le champ texte
        } else {
            // Masque le champ texte si la case est décochée ou si ce n'est pas "Autres"
            otherInputRow.style.display = "none";
        }
    }




  function toggleAdditionalField(select) {
      var selectedValue = select.value;
      
      // Liste des champs à afficher ou masquer
      var fieldsToShow = [
          'nonConformityField',
          'collectionDateField',
          'collectionTimeField',
          'collecteurNameField'
      ];

      // Vérifiez si une valeur valide est sélectionnée
      if (selectedValue) {
          // Affichez tous les champs supplémentaires
          fieldsToShow.forEach(function(fieldId) {
              document.getElementById(fieldId).style.display = "table-row"; // ou "block" selon votre disposition
          });
      } else {
          // Masquez tous les champs supplémentaires si rien n'est sélectionné
          fieldsToShow.forEach(function(fieldId) {
              document.getElementById(fieldId).style.display = "none";
          });
      }
  }









	function pageOnLoad() {
		jQuery('.centerCodeClass').select2();
		jQuery('.tbSpecimenNatureClass').select2();
		jQuery('.tbOrderReasonsClass').select2();
		jQuery('.tbDiagnosticReasonsClass').select2();
		jQuery('.tbFollowupReasonsClass').select2();
		jQuery('.tbFollowupPeriodLine1Class').select2();
		jQuery('.tbFollowupPeriodLine2Class').select2();
		jQuery('.tbDiagnosticMethodsClass').select2();
		jQuery('.tbAspectsClass').select2();
		toggleOrderReasons();
		toggleTBFollowupPeriodLine();
		toggleTBSampleAspects();
		jQuery("#requestDate").datepicker({
			dateFormat: 'dd/mm/yy',
			yearRange: "-1:+00"
		});
		jQuery("#receivedDate").datepicker({
			dateFormat: 'dd/mm/yy',
		     changeMonth: true,
		     changeYear: true,
		     yearRange: "-1:+00"
		});
		jQuery("#dateOfBirthID").datepicker({
			dateFormat: 'dd/mm/yy',
		     changeMonth: true,
		     changeYear: true,
		     yearRange: "-120:+00",
		     maxDate: new Date(),
		});
		
		showPanelAndTests($('tbDiagnosticMethods'));
		
		hideSection(document.getElementById('orderSearchId') ,'orderSearch');
		
		setSaveButton();
	}

	

	
	
	
	
	function showPanelAndTests(input) {
	    if (!input) {
	        return;
	    }
	    if (input.value) {
	        let selectedSampleType = input.value;
	        let testHtml = '';
	        let panelHtml = '';
	        
	        // Utiliser le bon paramètre dans l'URL
	        jQuery.get("MicrobiologyClassic/panel_test?sampleType=" + selectedSampleType, function(data) {
	        	 console.log(data); 
	            if (data) {
	                // Vider les tables existantes
	                jQuery("#addPanelTable").html('');
	                jQuery("#addTestTable").html('');
	                
	                // Afficher les tests
	                for (const [key, value] of Object.entries(data.tests)) {
	                    let d = '<tr>';
	                    d += '<td><input type="checkbox" value="' + value.id + '" name="newSelectedTests" id="test_' + value.id + '" class="tb_test" /></td>';
	                    d += '<td><label for="test_' + value.id + '">' + value.name + '</label></td>';
	                    d += '</tr>';
	                    testHtml += d;
	                }
	                jQuery("#addTestTable").append(testHtml);

	                // Afficher les panels
	                for (const [key, value] of Object.entries(data.panels)) {
	                    let d = '<tr>';
	                    d += '<td><input type="checkbox" value="' + value.id + '" name="testSelected" id="panel_' + value.id + '" onclick="togglePanelSelected(this,\'' + value.test_ids + '\')" /></td>';
	                    d += '<td><label for="panel_' + value.id + '">' + value.name + '</label></td>';
	                    d += '</tr>';
	                    panelHtml += d;
	                }
	                jQuery("#addPanelTable").append(panelHtml);
	            } else {
	                // Si aucune donnée, vider les tables
	                jQuery("#addPanelTable").html('');
	                jQuery("#addTestTable").html('');
	            }
	        });
	    } else {
	        // Si aucune valeur dans l'input, vider les tables
	        jQuery("#addPanelTable").html('');
	        jQuery("#addTestTable").html('');
	    }
	}

	// Fonction pour sélectionner/désélectionner les tests d'un panel
	function togglePanelSelected(panel, testIds) {
	    var testList = testIds.split(',');
	    for (let test of testList) {
	        let checkbox = document.getElementById("test_" + test);
	        if (checkbox) {
	            checkbox.click();  // Activer/désactiver le test
	        }
	    }
	}
	
	
	//afficher la date par defaut 
	 function setTodayDate() {
	        var today = new Date();
	        var day = String(today.getDate()).padStart(2, '0');
	        var month = String(today.getMonth() + 1).padStart(2, '0'); // Les mois commencent à 0
	        var year = today.getFullYear();

	        var formattedDate = day + '/' + month + '/' + year;

	        document.getElementById('requestDate').value = formattedDate;
	        document.getElementById('receivedDate').value = formattedDate;
	    }


	// Sélectionner "Ordonnance externe" par défaut
	  function setDefaultPrescription() {
        var defaultPrescription = "1474"; // Remplacez par la valeur réelle de "Ordonnance interne"
        
        // Récupérer l'élément <select> par son ID
        var selectElement = document.getElementById("prescriptionType");
        
        // Vérifier si l'élément <select> existe
        if (selectElement) {
            // Afficher les options du select dans la console
            console.log("Options disponibles dans le select:");
            for (var i = 0; i < selectElement.options.length; i++) {
                console.log("Option " + i + ": " + selectElement.options[i].value + " - " + selectElement.options[i].text);
            }
            
            // Essayer de définir la valeur par défaut
            selectElement.value = defaultPrescription;
            console.log("Valeur sélectionnée: " + selectElement.value);
        } else {
            console.log("Élément <select> prescriptionType non trouvé.");
        }
    }

	// Exécuter les fonctions au chargement de la page
	window.addEventListener('load', setTodayDate);
	window.addEventListener('load', setDefaultPrescription);
	    
	
	
    function setDefaultUrgent() {
        var radioButtons = document.getElementsByName('urgent'); // Récupère les boutons radio avec le nom 'urgent'
        
        // Remplacez 'nonValue' par la valeur réelle correspondant à "Non"
        var nonValue = '1477'; // Exemple : la valeur exacte associée à "Non"
        
        // Parcourir les boutons radio et cocher celui dont la valeur correspond à "Non"
        for (var i = 0; i < radioButtons.length; i++) {
        	
           // console.log("Valeur du bouton radio: ", radioButtons[i].value);
            if (radioButtons[i].value === nonValue) {
                radioButtons[i].checked = true;
                break;
            }
        }
    }
    // Exécuter la fonction au chargement de la page
    window.addEventListener('load', setDefaultUrgent);
    
    
    //validation fone for prescripteur
    
     function validatePhoneNumber() {
        var phoneInput = document.getElementById('providerWorkPhone').value;
        var phoneError = document.getElementById('phoneError');

        // Check if the phone number is exactly 10 digits long and contains only numbers
        if (phoneInput.length === 10 && /^\d+$/.test(phoneInput)) {
            phoneError.style.display = 'none'; // Hide error if the phone number is valid
        } else {
            phoneError.style.display = 'inline'; // Show error if the phone number is invalid
        }
    }

    // Optionally, validate the phone number when the page loads
    window.addEventListener('load', function() {
        validatePhoneNumber(); // Initial validation if needed
    });
	
    
    
    //Validation numero de Patient 
  function validatePhoneNumberPatient() {
    var phoneInput = document.getElementById("patientPhone");
    var phoneError = document.getElementById("phoneErrorPatient");
    var phoneValue = phoneInput.value;

    // Remove non-digit characters
    phoneValue = phoneValue.replace(/\D/g, '');

    // Check if the phone number is exactly 10 digits
    if (phoneValue.length !== 10) {
        phoneError.style.display = "inline"; // Show error message
        phoneInput.classList.add("error"); // Optionally, add a class for styling
    } else {
        phoneError.style.display = "none"; // Hide error message
        phoneInput.classList.remove("error"); // Remove error class if valid
    }
}
	//age mois annee 
	function handleAgeChange(){
		var ageYears = jQuery("#ageYears").val();

		if( ageYears.blank()){
			$("dateOfBirthID").value = null;
		} else {
			
			var date = new Date();
			if ( !ageYears.blank() ) {
				date.setFullYear( date.getFullYear() - parseInt(ageYears));
			}
			
			var day = "01";
			var month = "01";
			var year = "xxxx";

			year = date.getFullYear();

			var datePattern = '<%=SystemConfiguration.getInstance().getPatternForDateLocale() %>';
			var splitPattern = datePattern.split("/");

			var DOB = "";

			for( var i = 0; i < 3; i++ ){
				if(splitPattern[i] == "DD"){
					DOB = DOB + day.toLocaleString('en', {minimumIntegerDigits:2}) + "/";
				}else if(splitPattern[i] == "MM" ){
					DOB = DOB + month.toLocaleString('en', {minimumIntegerDigits:2}) + "/";
				}else if(splitPattern[i] == "YYYY" ){
					DOB = DOB + year + "/";
				}
			}
			$("dateOfBirthID").value = DOB.substring(0, DOB.length - 1 );
		}
		jQuery("#dateOfBirthID").trigger('change');
	}
	
	//si Sexe =Feminin
	function checkPregnancyField() {
    var genderSelect = document.getElementById("genderID");
    var gender = genderSelect.value;
    var pregnancyStatusContainer = document.getElementById("pregnancyStatusContainer");

    if (gender === "F") { // Assuming "F" is the value for female
        pregnancyStatusContainer.style.display = "table-cell"; // Show the pregnancy status field
        console.log("Selected gender ID (F):", gender); // Log the ID for female to the console
    } else {
        pregnancyStatusContainer.style.display = "none"; // Hide the pregnancy status field
    }
}
	
	


		
	
</script>






























