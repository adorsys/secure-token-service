package de.adorsys.sts.resourceserver;

import java.util.ArrayList;
import java.util.List;

public class ResourceServerErrors {
	
	private List<ResourceServerError> erros = new ArrayList<>();

	public List<ResourceServerError> getErros() {
		return erros;
	}

	public void setErros(List<ResourceServerError> erros) {
		this.erros = erros;
	}
	
}
