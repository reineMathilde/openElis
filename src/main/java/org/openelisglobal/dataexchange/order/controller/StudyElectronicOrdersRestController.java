package org.openelisglobal.dataexchange.order.controller;

import org.apache.commons.lang3.ObjectUtils;
import org.openelisglobal.dataexchange.service.order.ElectronicOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest_eorder")
public class StudyElectronicOrdersRestController {

	@Autowired
	private ElectronicOrderService electronicOrderService;

	@GetMapping(value = "/external_id")
	public ResponseEntity<String> getEOrderByPatient(@RequestParam String patientCode) {
		String externalId = electronicOrderService.getEnteredElectronicOrderByPatient(patientCode);
		if (ObjectUtils.isNotEmpty(externalId))
			return new ResponseEntity<String>(externalId, HttpStatus.OK);
		else
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
	}
}
