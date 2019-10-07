package org.egov.demand.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillV2 {

	@JsonProperty("id")
	private String id;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("payerName")
	private String payerName;

	@JsonProperty("payerAddress")
	private String payerAddress;

	@JsonProperty("payerEmail")
	private String payerEmail;

	@JsonProperty("status")
	private StatusEnum status;

	@JsonProperty("totalAmount")
	private BigDecimal totalAmount;

	@JsonProperty("businessService")
	private String businessService;

	@JsonProperty("billNumber")
	private String billNumber;
	
	@JsonProperty("billDate")
	private Long billDate;

	@JsonProperty("consumerCode")
	private String consumerCode;

	@JsonProperty("collectionModesNotAllowed")
	@Valid
	private List<String> collectionModesNotAllowed;

	@JsonProperty("partPaymentAllowed")
	private Boolean partPaymentAllowed;
	
	@JsonProperty("isAdvanceAllowed")
	private Boolean isAdvanceAllowed;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;

	@JsonProperty("billDetails")
	@Valid
	private List<BillDetailV2> billDetails;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	/**
	 * status of the bill .
	 */
	public enum StatusEnum {
		
		ACTIVE("ACTIVE"),

		CANCELLED("CANCELLED"),

		PAID("PAID"),

		EXPIRED("EXPIRED");

		private String value;

		StatusEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static StatusEnum fromValue(String text) {
			for (StatusEnum b : StatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	public BillV2 addBillDetailsItem(BillDetailV2 billDetailsItem) {
		if (this.billDetails == null) {
			this.billDetails = new ArrayList<>();
		}
		this.billDetails.add(billDetailsItem);
		return this;
	}

}