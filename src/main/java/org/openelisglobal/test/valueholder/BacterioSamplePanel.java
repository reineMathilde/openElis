package org.openelisglobal.test.valueholder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.openelisglobal.common.valueholder.EnumValueItemImpl;
@Entity
@Table(name = "bacterio_sample_panel")
public class BacterioSamplePanel extends EnumValueItemImpl{

	
	
	private static final long serialVersionUID = -1574344492809195601L;

	@Id
	@SequenceGenerator(name="bacterio_sample_panel_seq",allocationSize = 1,initialValue = 1	)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="bacterio_sample_panel_seq")
	@Column
	private String id;

	@Column(name = "panel_id",nullable = false)
	private String panelId;

	@Column(name = "sample_type_id",nullable = false)
	private String sampleType;

	@Column(name="is_active")
	private String isActive;
	
	
	public BacterioSamplePanel() {
		super();
	}
	
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPanelId() {
		return panelId;
	}
	
	public void setPanelId(String panelId) {
		this.panelId = panelId;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}
	@Override
	public String getIsActive() {
		return isActive;
	}
	@Override
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
}
