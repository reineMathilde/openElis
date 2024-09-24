package org.openelisglobal.samplemicrobioclassik.valueholder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.hibernate.StaleObjectStateException;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.openelisglobal.common.action.IActionConstants;
import org.openelisglobal.common.constants.Constants;
import org.openelisglobal.common.exception.LIMSRuntimeException;
import org.openelisglobal.common.formfields.FormFields;
import org.openelisglobal.common.log.LogEvent;
import org.openelisglobal.common.services.DisplayListService;
import org.openelisglobal.common.services.SampleOrderService;
import org.openelisglobal.common.services.DisplayListService.ListType;
import org.openelisglobal.common.util.ConfigurationProperties;
import org.openelisglobal.common.util.DateUtil;
import org.openelisglobal.common.util.IdValuePair;
import org.openelisglobal.common.util.ConfigurationProperties.Property;
import org.openelisglobal.common.validator.BaseErrors;
import org.openelisglobal.dataexchange.fhir.FhirConfig;
import org.openelisglobal.dataexchange.fhir.FhirUtil;
import org.openelisglobal.dataexchange.fhir.exception.FhirPersistanceException;
import org.openelisglobal.dataexchange.fhir.exception.FhirTransformationException;
import org.openelisglobal.dataexchange.fhir.service.FhirPersistanceService;
import org.openelisglobal.dataexchange.fhir.service.FhirTransformService;
import org.openelisglobal.dataexchange.order.valueholder.ElectronicOrder;
import org.openelisglobal.dataexchange.service.order.ElectronicOrderService;
import org.openelisglobal.dictionary.valueholder.Dictionary;
import org.openelisglobal.internationalization.MessageUtil;
import org.openelisglobal.organization.service.OrganizationService;
import org.openelisglobal.organization.valueholder.Organization;
import org.openelisglobal.panel.service.PanelService;
import org.openelisglobal.panel.valueholder.Panel;
import org.openelisglobal.panelitem.service.PanelItemService;
import org.openelisglobal.panelitem.valueholder.PanelItem;
import org.openelisglobal.patient.action.IPatientUpdate;
import org.openelisglobal.patient.action.IPatientUpdate.PatientUpdateStatus;
import org.openelisglobal.patient.action.bean.PatientManagementInfo;
import org.openelisglobal.patient.action.bean.PatientSearch;
import org.openelisglobal.provider.service.ProviderService;
import org.openelisglobal.provider.valueholder.Provider;
import org.openelisglobal.sample.action.util.SamplePatientUpdateData;
import org.openelisglobal.sample.bean.SampleOrderItem;
import org.openelisglobal.sample.controller.BaseSampleEntryController;
import org.openelisglobal.sample.form.SamplePatientEntryForm;
import org.openelisglobal.sample.form.SampleTbEntryForm;
import org.openelisglobal.sample.form.SampleTbclassicForm;
import org.openelisglobal.sample.service.PatientManagementUpdate;
import org.openelisglobal.sample.service.SampleBacterioService;
import org.openelisglobal.sample.service.SamplePatientEntryService;

import org.openelisglobal.sample.service.TbSampleService;
import org.openelisglobal.sample.validator.SamplePatientEntryFormValidator;
import org.openelisglobal.sample.valueholder.SampleAdditionalField;
import org.openelisglobal.sample.valueholder.SampleAdditionalField.AdditionalFieldName;
import org.openelisglobal.spring.util.SpringContext;
import org.openelisglobal.systemuser.service.UserService;
import org.openelisglobal.test.service.TestService;
import org.openelisglobal.test.valueholder.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class SampleMicrobioEntryController extends BaseSampleEntryController {

//  @Value("${org.openelisglobal.requester.lastName:}")
//  private String requesterLastName;
//  @Value("${org.openelisglobal.requester.firstName:}")
//  private String requesterFirstName;
//@Value("${org.openelisglobal.requester.phone:}")
//private String requesterPhone;@Autowired
	@Autowired
    private SampleBacterioService samplebacterioService;
	@Autowired
	private OrganizationService organizationService;
    @Autowired
	private UserService userService;
    @Autowired
	private TestService testService;
	@Autowired
	private PanelService panelService;
	@Autowired
	private TbSampleService tbSampleService;

	@Autowired
	private PanelItemService panelItemService;

    private static final String[] ALLOWED_FIELDS = new String[] {};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(ALLOWED_FIELDS);
    }

    @RequestMapping(value = "/MicrobiologyClassic", method = RequestMethod.GET)
    public ModelAndView showSampleTbclassicEntry(HttpServletRequest request) {
        SampleTbclassicForm form = new SampleTbclassicForm();   
        String title = MessageUtil.getMessage("sample.microbiology.classic.title");
        //System.out.println("Title: " + title); 
        request.setAttribute(IActionConstants.PAGE_SUBTITLE_KEY, title);
        
       


		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);
		form.setReceivedDate(dateAsText);

		setDisplayLists(form);
		addFlashMsgsToRequest(request);

		return findForward(FWD_SUCCESS, form);
    }

    @RequestMapping(value = "/MicrobiologyClassic", method = RequestMethod.POST)
    public ModelAndView postSampleTbclassicEntry(HttpServletRequest request,
            @ModelAttribute("form") @Valid SampleTbclassicForm form, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            saveErrors(result);
            setDisplayLists(form);
            return findForward(FWD_FAIL_INSERT, form);
        }
        form.setSysUserId(this.getSysUserId(request));
        if (samplebacterioService.persistBacterioData(form, request)) {
            redirectAttributes.addFlashAttribute(FWD_SUCCESS, true);
            setDisplayLists(form);
            return findForward(FWD_SUCCESS_INSERT, form);
        }
        logAndAddMessage(request, "postSampleTbclassicEntry", "errors.UpdateException");

        return findForward(FWD_FAIL_INSERT, form);
    }
    
    

    private void setDisplayLists(SampleTbclassicForm form) {
    	
    	
        // Récupérer la liste des régions sanitaires
        List<IdValuePair> regions = createPatientHealthRegions();
        
        // Définir la liste des régions dans le formulaire
        form.setPatientRegions(regions);  
        
        
    	
        // Récupérer la liste des régions sanitaires
       // List<IdValuePair> districts = getOrganizationsByRegionId();
        
        // Définir la liste des régions dans le formulaire
       // form.setPatientDistricts(districts);  
        
        
        
        // Implémentez cette méthode pour définir les listes déroulantes nécessaires
    	
    	
    	List<Dictionary> listOfDictionary = new ArrayList<>();
		List<IdValuePair> genders = DisplayListService.getInstance().getList(ListType.GENDERS);

		for (IdValuePair i : genders) {
			Dictionary dictionary = new Dictionary();
			dictionary.setId(i.getId());
			dictionary.setDictEntry(i.getValue());
			listOfDictionary.add(dictionary);
		}

		form.setGenders(genders);
		form.setReferralOrganizations(
				DisplayListService.getInstance().getList(ListType.SAMPLE_PATIENT_REFERRING_CLINIC));
		form.setMicrobioSpecimenNatures(
				userService.getUserSampleTypes(getSysUserId(request), Constants.ROLE_RECEPTION, "Bacterio"));
		form.setTestSectionList(DisplayListService.getInstance().getList(ListType.TEST_SECTION_ACTIVE));
		form.setCurrentDate(DateUtil.getCurrentDateAsText());
		form.setMicrobioclinicalinfos(DisplayListService.getInstance().getList(ListType.MICROBIO_CLINICAL_INFO));
		form.setGestesInvasifs(DisplayListService.getInstance().getList(ListType.MICROBIO_GESTES_INVASIF));
		form.setEducations(DisplayListService.getInstance().getList(ListType.EDUCATION_LIST));
		form.setOrginalservices(DisplayListService.getInstance().getList(ListType.SERVICE_LIST));
		form.setProviderpayments(DisplayListService.getInstance().getList(ListType.PROVIDER_PAYMENT));
		
       form.setPrescriptionTypes(DisplayListService.getInstance().getList(ListType.ORDER_TYPE));
       form.setUrgents(DisplayListService.getInstance().getList(ListType.URGENT));
       form.setDispositifs(DisplayListService.getInstance().getList(ListType.DISPOSITIF_DEMEURRE));
       form.setTbSpecimenStates(DisplayListService.getInstance().getList(ListType.ETAT_SPECIMEN));
       form.setNoConformitynatures(DisplayListService.getInstance().getList(ListType.NO_CONFORMITY));
   
		//form.setEpidemiologiques(DisplayListService.getInstance().getList(ListType.EPIDEMIOLOGIQUE_LIST));
		
		 // Récupérer la liste des informations cliniques pour les cases à cocher
        List<IdValuePair> microbioclinicalinfos = DisplayListService.getInstance().getList(ListType.MICROBIO_CLINICAL_INFO);
        form.setMicrobioclinicalinfos(microbioclinicalinfos);
		//form.setTbFollowupReasons(DisplayListService.getInstance().getList(ListType.TB_FOLLOWUP_REASONS));
		form.setTbTypeExamens(DisplayListService.getInstance().getList(ListType.TYPE_DEMANDE));
		form.setTbAspects(DisplayListService.getInstance().getList(ListType.TB_SAMPLE_ASPECTS));
		//form.setTbFollowupPeriodsLine1(DisplayListService.getInstance().getList(ListType.TB_FOLLOWUP_LINE1));
		//form.setTbFollowupPeriodsLine2(DisplayListService.getInstance().getList(ListType.TB_FOLLOWUP_LINE2));
    }
    
    
    

    private List<IdValuePair> createPatientHealthRegions() {
		// TODO Auto-generated method stub
    	 List<IdValuePair> regionList = new ArrayList<>();
    	    List<Organization> orgList = organizationService.getOrganizationsByTypeName("id", "Health Region");
    	    
    	    // Assurez-vous que la liste n'est pas vide
    	    if (orgList != null && !orgList.isEmpty()) {
    	        orgList.sort((e, f) -> e.getOrganizationName().compareTo(f.getOrganizationName()));

    	        for (Organization org : orgList) {
    	            regionList.add(new IdValuePair(org.getId(), org.getOrganizationName()));
    	        }
    	    }

    	    return regionList;
	}
    
    
    
 // Méthode pour obtenir les districts selon l'ID de région
  //  public List<IdValuePair> getOrganizationsByRegionId(String healthRegion) {
       // List<IdValuePair> districtsList = new ArrayList<>();
       // if (!GenericValidator.isBlankOrNull(healthRegion)) {
           // List<Organization> districts = SpringContext.getBean(OrganizationService.class)
            //        .getOrganizationsByParentId(healthRegion);
          //  for (Organization district : districts) {
          //      districtsList.add(new IdValuePair(district.getId(), district.getOrganizationName()));
          //  }
       // }
     //   return districtsList;
    //}
    
    
    
    

    @GetMapping(value = "MicrobiologyClassic/panel_test")
    public ResponseEntity<Map<String, Object>> getPanelTestsElement(@RequestParam("sampleType") String sampleType) {
        Map<String, Object> response = new HashMap<String, Object>();
        try {
         
            List<Test> tests = testService.getbacterioTestBySampleType(sampleType);
            List<Panel> panels = testService.getbacterioPanelsBySampleType(sampleType);
         
            
            List<Map<String, Object>> testsList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> panelsList = new ArrayList<Map<String, Object>>();
            tests.forEach(test -> {
                Map<String, Object> el = new HashMap<String, Object>();
                el.put("id", test.getId());
                el.put("name", test.getLocalizedName());
                List<PanelItem> pItems = panelItemService.getPanelItemByTestId(test.getId());
             
                
                pItems.forEach(item -> {
                    Map<String, Object> sPanel = null;  
                    boolean panelFound = false;

                    // Débogage : afficher le nom du panneau à partir de l'élément de panneau
                    String panelName = item.getPanel().getLocalizedName();
                 

                    for (Map<String, Object> panel : panelsList) {
                        if (panel.get("name").equals(panelName)) {
                            sPanel = panel;
                            panelFound = true;
                            break;
                        }
                    }

                    if (!panelFound) {
                        sPanel = new HashMap<String, Object>();
                        sPanel.put("name", panelName);
                        sPanel.put("id", item.getPanel().getId());
                        sPanel.put("test_ids", "" + test.getId());
                        panelsList.add(sPanel);
                      
                    } else {
                        String existingTestIds = (String) sPanel.get("test_ids");
                        sPanel.put("test_ids", existingTestIds + "," + test.getId());
                        
                    }
                });
                testsList.add(el);
            });

            // Filtrer directement les panels basés sur realPanelIds
            List<String> realPanelIds = panels.stream().map(p -> p.getId()).collect(Collectors.toList());
       

            // Ajout de panels au response si leurs ids sont dans realPanelIds
            List<Map<String, Object>> filteredPanels = panelsList.stream()
                .filter(elm -> realPanelIds.contains(elm.get("id")))
                .collect(Collectors.toList());

            response.put("tests", testsList);
            response.put("panels", filteredPanels);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
    
    
    
    
    
    
    
    
    


	@Override
    protected String findLocalForward(String forward) {
        if (FWD_SUCCESS.equals(forward)) {
            return "sampleMicrobioEntryDefinition";
        } else if (FWD_SUCCESS_INSERT.equals(forward)) {
            return "redirect:/MicrobiologyClassic";
        } else if (FWD_FAIL_INSERT.equals(forward)) {
            return "homePageDefinition";
        } else {
            return "PageNotFound";
        }
    }

	@Override
	protected String getPageTitleKey() {
		return null;
	}

	@Override
	protected String getPageSubtitleKey() {
		return null;
	}
}
