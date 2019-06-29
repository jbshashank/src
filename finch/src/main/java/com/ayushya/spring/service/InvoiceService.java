package com.ayushya.spring.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InvoiceService {

	public String generateInvoice(String ticket_id,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) ;
}
