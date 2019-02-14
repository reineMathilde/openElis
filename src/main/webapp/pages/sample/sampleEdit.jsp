<%@ page language="java" contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.action.IActionConstants,
				us.mn.state.health.lims.common.formfields.FormFields,
				us.mn.state.health.lims.common.util.*,
	            us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
		        us.mn.state.health.lims.sample.util.AccessionNumberUtil,
		        us.mn.state.health.lims.sample.bean.SampleEditItem" %>

<%@ page isELIgnored="false" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="app" uri="/tags/labdev-view" %>
<%@ taglib prefix="ajax" uri="/tags/ajaxtags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>	 

<c:set var="cancelableResults" value="${form.ableToCancelResults}"/>

<%!
	String basePath = "";
	String accessionNumber = "";
    boolean useCollectionDate = true;
%>
<%
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
    useCollectionDate = FormFields.getInstance().useField( FormFields.Field.CollectionDate);
%>

<script src="scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.menu.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.autocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/customAutocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script type="text/javascript" >

var checkedCount = 0;
var currentSampleType;
var sampleIdStart = 0;
var orderChanged = false;

//This handles the case where sampleAdd.jsp tile is not used.  Will be overridden in sampleAdd.jsp
function samplesHaveBeenAdded(){ return false;}

$jq(document).ready( function() {
    if( !${form.isEditable}) {
        $jq(":input").prop("readOnly", true);
        $jq(".patientSearch").prop("readOnly", false);
    }
});

$jq(function() {
   	var maxAccessionNumber = $("maxAccessionNumber").value;
	var lastDash = maxAccessionNumber.lastIndexOf('-');
   	sampleIdStart = maxAccessionNumber.substring(lastDash + 1);
});

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function /*void*/ addRemoveRequest( checkbox ){
	checkedCount = Math.max(checkedCount + (checkbox.checked ? 1 : -1), 0 );

	if( typeof(showSuccessMessage) != 'undefinded' ){
		showSuccessMessage(false); //refers to last save
	}

	setSaveButton();

}


// Adds warning when leaving page if tests are checked
function formWarning(){
	var newAccession = $("newAccessionNumber").value;
	var accessionChanged = newAccession.length > 1 && newAccession != "${form.accessionNumber}"; 

  	if ( checkedCount > 0 || accessionChanged || samplesHaveBeenAdded()) {
    	return "<spring:message code="banner.menu.dataLossWarning"/>";
	}
}
window.onbeforeunload = formWarning;

function /*void*/ savePage(){
	if( samplesHaveBeenAdded() && !sampleAddValid( false )){
		alert("<%= StringUtil.getMessageForKey("warning.sample.missing.test")%>");
		return;
	}


    if( $jq(".testWithResult:checked").size() > 0 &&
        !confirm("<%= StringUtil.getMessageForKey("test.modify.save.warning")%>") ) {
            return;
    }
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	loadSamples();
	
	var form = document.forms[0];
	form.action = "SampleEditUpdate.do";
	form.submit();
}

function checkEditedAccessionNumber(changeElement){
	var accessionNumber;
	clearFieldErrorDisplay( changeElement );

	$("newAccessionNumber").value = "";
	
	if( changeElement.value.length == 0){
		updateSampleItemNumbers( "${form.accessionNumber}" );
		setSaveButton();
		return;
	}
	
	if( changeElement.value.length != ${form.editableAccession}){
		setFieldErrorDisplay( changeElement );
		setSaveButton();
		alert("<%=StringUtil.getMessageForKey("sample.entry.invalid.accession.number.length")%>");
		return;
	}
	
	accessionNumber = "<c:out value='${fn:substring(form.accessionNumber, 0, form.nonEditableAccession)}'/>" + changeElement.value;
	
	if( accessionNumber == "${form.accessionNumber}"){
		updateSampleItemNumbers( accessionNumber );
		setSaveButton();
		return;
	}
	
	validateAccessionNumberOnServer(true, false, changeElement.id, accessionNumber, processEditAccessionSuccess, null);
}

function processEditAccessionSuccess(xhr)
{
	//alert( xhr.responseText );
	var accessionNumberUpdate;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;

	if (message == "SAMPLE_FOUND"){
		setFieldErrorDisplay( $(formField) );
		setSaveButton();
		alert("<%=StringUtil.getMessageForKey("errors.may_not_reuse_accession_number")%>");
		return;
	}
	
	if( message == "SAMPLE_NOT_FOUND"){
		accessionNumberUpdate = "<c:out value="${fn:substring(form.accessionNumber, 0, form.nonEditableAccession)}"/>" + $(formField).value;
		updateSampleItemNumbers( accessionNumberUpdate );
		$("newAccessionNumber").value = accessionNumberUpdate;
		setSaveButton();
		return;
	}
	
	setFieldErrorDisplay( $(formField) );
	setSaveButton();
	alert(message);
}

function updateSampleItemNumbers(newAccessionNumber){
		var i, itemNumbers, currentValue, lastDash = 0;
		itemNumbers = $$('span.itemNumber');
		
		for( i = 0; i < itemNumbers.length; i++){
			if(itemNumbers[i].firstChild != undefined){
				currentValue = itemNumbers[i].firstChild.nodeValue;
				lastDash = currentValue.lastIndexOf('-');
				itemNumbers[i].firstChild.nodeValue = newAccessionNumber + currentValue.substring(lastDash);
			}
		}
}

function checkValidEntryDate(date, dateRange, blankAllowed)
{
    $jq("#sampleItemChanged_" + date.id.split("_")[1]).val(true);
    if((!date.value || date.value == "") && !blankAllowed){
        setSaveButton();
        return;
    } else if ((!date.value || date.value == "") && blankAllowed) {
        setValidIndicaterOnField(true, date.id);
        setSaveButton();
        return;
    }


    if( !dateRange || dateRange == ""){
        dateRange = 'past';
    }

    //ajax call from utilites.js
    isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
}

function  /*void*/ processValidateEntryDateSuccess(xhr){

    //alert(xhr.responseText);

    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

    var isValid = message == "<%=IActionConstants.VALID%>";

    //utilites.js
    selectFieldErrorDisplay( isValid, $(formField));
    setSaveButton();

    if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
        alert( "<spring:message code="error.date.inFuture"/>" );
    }else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
        alert( "<spring:message code="error.date.inPast"/>" );
    }
}


function checkValidTime(time, blankAllowed)
{
    var lowRangeRegEx = new RegExp("^[0-1]{0,1}\\d:[0-5]\\d$");
    var highRangeRegEx = new RegExp("^2[0-3]:[0-5]\\d$");

    $jq("#sampleItemChanged_" + time.id.split("_")[1]).val(true);
    if (time.value.blank() && blankAllowed == true) {
        clearFieldErrorDisplay(time);
        setSaveButton();
        return;
    }

    if( lowRangeRegEx.test(time.value) ||
            highRangeRegEx.test(time.value) )
    {
        if( time.value.length == 4 )
        {
            time.value = "0" + time.value;
        }
        clearFieldErrorDisplay(time);
    }
    else
    {
        setFieldErrorDisplay(time);
 //       setSampleFieldInvalid(time.name);
    }

    setSaveButton();
}

//all methods here either overwrite methods in tiles or all called after they are loaded
var dirty=false;
function makeDirty(){
    dirty=true;
    if( typeof(showSuccessMessage) != 'undefinded' ){
        showSuccessMessage(false); //refers to last save
    }
    // Adds warning when leaving page if content has been entered into makeDirty form fields
    function formWarning(){
        return "<spring:message code="banner.menu.dataLossWarning"/>";
    }
    window.onbeforeunload = formWarning;
}

</script>

<hr/>

<c:if test="${not form.noSampleFound}">
<DIV  id="patientInfo" class='textcontent'>
<spring:message code="sample.entry.patient"/>:&nbsp;
<c:out value="${form.patientName}"/>&nbsp;
<c:out value="${form.dob}"/>&nbsp;
<c:out value="${form.gender}"/>&nbsp;
<c:out value="${form.nationalId}"/>
</DIV>
<hr/>
<br/>
<form:hidden path="accessionNumber"/>
<form:hidden path="newAccessionNumber" id="newAccessionNumber"/>
<form:hidden path="isEditable"/>
<form:hidden path="maxAccessionNumber" id="maxAccessionNumber"/>
<c:if test="${form.isEditable}">
	<h1><%=StringUtil.getContextualMessageForKey("sample.edit.accessionNumber") %></h1>  
	<div id="accessionEditDiv" class="TableMatch">
		<b><%=StringUtil.getContextualMessageForKey("sample.edit.change.from") %>:</b> <c:out value="${form.accessionNumber}"/>  
		<b><%=StringUtil.getContextualMessageForKey("sample.edit.change.to") %>:</b> <c:out value="${fn:substring(form.accessionNumber, 0, form.nonEditableAccession)}"/>
		<input type="text"
				<c:if test="${fn:length(form.newAccessionNumber) == form.maxAccessionLength}">
		       		value='<c:out value="${fn:substring(newAccessionNumber, nonEditableAccession, maxAccessionLength) }"/>' 
		       	</c:if>
		       maxlength="${form.editableAccession}"
		       size="${form.editableAccession}"
		       onchange="checkEditedAccessionNumber(this);"
		       id="accessionEdit">
		       
	<br/><br/><hr/>
	</div>
</c:if>
    <div id="sampleOrder" class="colorFill" >
    	<c:if test="${form.isConfirmationSample}">
        	<tiles:insertAttribute name="sampleConfirmationOrder" />
        </c:if>
    	<c:if test="${not form.isConfirmationSample}">
        	<tiles:insertAttribute name="sampleOrder" />
        </c:if>
    </div>

<c:if test="${form.isEditable}">
	<h1><%=StringUtil.getContextualMessageForKey("sample.edit.tests") %></h1>
</c:if>
<table style="width:60%">
<caption>
	<spring:message code="sample.edit.existing.tests"/><br>
    <span style="color: red"><small><small>
    <c:if test="${cancelableResults}">
    	<spring:message code="test.modify.static.warning" />
    </c:if>
    </small></small>
    </span>
</caption>
<tr>
<th><%= StringUtil.getContextualMessageForKey("quick.entry.accession.number") %></th>
<th><spring:message code="sample.entry.sample.type"/></th>
<% if( useCollectionDate ){ %>
<th >
    <spring:message code="sample.collectionDate"/>&nbsp;<%=DateUtil.getDateUserPrompt()%>
</th>
<th >
    <spring:message code="sample.collectionTime"/>
</th>
<% } %>
<c:if test="${form.isEditable}">
	<th style="width:16px"><spring:message code="sample.edit.remove.sample" /></th>
</c:if>
<th><spring:message code="test.testName"/></th>
<th><spring:message code="test.has.result"/></th>
<c:if test="${form.isEditable}">
	<th style="width:16px"><spring:message code="sample.edit.remove.tests" /></th>
</c:if>
<c:if test="${not form.isEditable}">
	<th><spring:message code="analysis.status" /></th>
</c:if>

</tr>
	<c:forEach items="${form.existingTests}" var="existingTests" varStatus="iter">
        <form:hidden path="existingTests[${iter.index}].sampleItemChanged" id='sampleItemChanged_${iter.index}' cssClass="sampleItemChanged" indexed="true"/>
		<form:hidden path="existingTests[${iter.index}].sampleItemId"/>
	<tr>
		<td>
			<form:hidden path="existingTests[${iter.index}].analysisId"/>
			<span class="itemNumber" ><c:out value="${existingTests.accessionNumber}"/></span>
		</td>
		<td>
			<c:out value="${existingTests.sampleType}"/>
		</td>
        <% if( useCollectionDate ){ %>
        <td >
        	<c:if test="${existingTests.collectionDate != null}">
	        	<c:set var="readOnly" value=""/>
	        	<c:if test="${not form.isEditable}"> 
	        		<c:set var="readOnly" value="readonly"/>
	        	</c:if>
				<form:input path='existingTests[${iter.index}].collectionDate'
						   maxlength='10'
						   size ='12'
						   onchange="checkValidEntryDate(this, 'past', true);"
						   id='collectionDate_${iter.index}'
						   cssClass='text ${readOnly}'
						   indexed="true" />
	        </c:if>
	        </td>
        <td >
        	<c:if test="${existingTests.collectionDate != null}">
	            <form:input path='existingTests[${iter.index}].collectionTime'
	                       maxlength='10'
	                       size ='12'
	                       onkeyup='filterTimeKeys(this, event);'
	                       onblur='checkValidTime(this, true);'
						   id='collectionDate_${iter.index}'
	                       cssClass='text'
	                       indexed="true"/>
	        </c:if>
        </td>
        <% } %>
		<c:if test="${form.isEditable}">
            <td>
            	<c:if test="${existingTests.accessionNumber != null}">
            		<c:if test="${existingTests.canRemoveSample}">
                		<form:checkbox path="existingTests[${iter.index}].removeSample" onchange="addRemoveRequest(this);"/>
                	</c:if>
                	<c:if test="${not existingTests.canRemoveSample}">
                		<form:checkbox path='existingTests[${iter.index}].removeSample' disabled="true"/>
               	 	</c:if>
                </c:if>
            </td>
		</c:if>
		<td>
			<c:out value="${existingTests.testName}"/>
		</td>
            <td style="text-align: center">
            	<c:if test="${existingTests.hasResults}"> X </c:if>
            </td>
		<c:if test="${form.isEditable}">
			<td>
            	<c:if test="${existingTests.canCancel}">
                	<input type="checkbox" 
                		   name='existingTests[${iter.index}].canceled' 
                		   value="on" 
                		   onchange="addRemoveRequest(this);" <c:if test="${existingTests.hasResults}">class='testWithResult'</c:if>
                		   />
				</c:if>
            	<c:if test="${not existingTests.canCancel}">
					<form:checkbox path='existingTests[${iter.index}].canceled' disabled="true" />
				</c:if>
			</td>
		</c:if>
		<c:if test="${not form.isEditable}">
			<td>
				<c:out value="${existingTests.status}"/>
			</td>
		</c:if>
	</tr>
	</c:forEach>
</table>
<hr/>
<br/>
<c:if test="${form.isEditable}">
<table id="availableTestTable" style="width:80%">
<caption><spring:message code="sample.edit.available.tests"/></caption>
<tr>
<th><%= StringUtil.getContextualMessageForKey("quick.entry.accession.number") %></th>
<th><spring:message code="sample.entry.sample.type"/></th>
<th><spring:message code="sample.entry.assignTests"/></th>
<th><spring:message code="test.testName"/></th>
</tr>
	<c:forEach var="possibleTests" varStatus="iter" items="${form.possibleTests}">
	<tr>
		<td>
		    <form:hidden path="possibleTests[${iter.index}].testId"/>
		    <form:hidden path="possibleTests[${iter.index}].sampleItemId"/>
			<span class="itemNumber" ><c:out value="${possibleTests.accessionNumber}"/></span>
		</td>
		<td>
			<c:out value="${possibleTests.sampleType}"/>
		</td>
		<td>
			<form:checkbox path="possibleTests[${iter.index}].add" onchange="addRemoveRequest(this);" />
		</td>
		<td>&nbsp;
			<c:out value="${possibleTests.testName}"/>
		</td>
	</tr>
	</c:forEach>
</table>

<hr>
<h1><spring:message code="sample.entry.addSample" /></h1>

<div id="samplesDisplay" class="colorFill" >
	<tiles:insertAttribute name="addSample"/>
</div>
</c:if>
</c:if>
<c:if test="${form.noSampleFound}">
	<spring:message code="sample.edit.sample.notFound"/>
</c:if>

<script type="text/javascript" >

    function setSaveButton(){
        var newAccession = $("newAccessionNumber").value;
        var accessionChanged = newAccession.length > 1 && newAccession != "${form.accessionNumber}";
        var sampleItemChanged = $jq(".sampleItemChanged[value='true']").length > 0;
        var sampleAddIsValid = typeof(sampleAddValid) != 'undefined' ?  sampleAddValid(false) : true;
        var sampleConfirmationIsValid = typeof(sampleConfirmationValid) != 'undefined' ?  sampleConfirmationValid() : true;

        $("saveButtonId").disabled = errorsOnForm() || !sampleAddIsValid || !sampleConfirmationIsValid || (checkedCount == 0  && !accessionChanged && !samplesHaveBeenAdded() && !orderChanged && !sampleItemChanged );
    }

</script>