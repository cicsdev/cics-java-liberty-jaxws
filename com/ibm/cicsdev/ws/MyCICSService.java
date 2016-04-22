/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2016 All Rights Reserved                       */       
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */
package com.ibm.cicsdev.ws;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.ibm.cics.server.CicsConditionException;
import com.ibm.cics.server.Program;
import com.ibm.cics.server.Task;
import com.ibm.cicsdev.bean.CommareaWrapper;
import com.ibm.cicsdev.bean.ResponseWrapper;

@WebService(serviceName="MyCICSService")
public class MyCICSService {
	
	protected static Logger logger = Logger.getLogger(MyCICSService.class.getName());
	
	//Working variables
	Program programToCall = null;

	//CICS program name to call
	String cicsProgram = "EDUPGM";

	@WebMethod
	public ResponseWrapper invokeCICSProgram(
			int binaryDigit,
			String characterString,
			long numericString,
			long packedDecimal,
			long signedDecimal,
			String bool
			){
		
		String result = "KO";

		logger.log(Level.INFO, "Starting");
		
		//Print data received
		logger.log(Level.INFO, "binaryDigit       : " + binaryDigit);
		logger.log(Level.INFO, "characterString   : " + characterString);
		logger.log(Level.INFO, "numericString     : " + numericString);
		logger.log(Level.INFO, "packedDecimal     : " + packedDecimal);
		logger.log(Level.INFO, "signedDecimal     : " + signedDecimal);
		logger.log(Level.INFO, "bool              : " + bool);

		//Instantiate COMMAREA wrapper
		logger.log(Level.INFO, "Creating COMMAREA");
		CommareaWrapper commareaWrapper = new CommareaWrapper();
		commareaWrapper.setBinaryDigit(binaryDigit);
		commareaWrapper.setCharacterString(characterString);
		commareaWrapper.setNumericString(numericString);
		commareaWrapper.setPackedDigit(packedDecimal);
		commareaWrapper.setSignedPacked(signedDecimal);
		commareaWrapper.setBool(bool);

		logger.log(Level.INFO, "About to invoke : " + cicsProgram);

		Task t = Task.getTask();
		if ( t == null ){
			System.err.println("Can't get Task");
			commareaWrapper.setResultText("CICS Program cannot get the task");
			commareaWrapper.setResultCode(-1); 
		}
		else
		{
			Program programToCall = new Program();
			
			programToCall.setName(cicsProgram);
			try {
				programToCall.link(commareaWrapper.getByteBuffer());
				logger.log(Level.INFO, "Returned from CICS program");
				result = "OK";
			} catch (CicsConditionException e) {
				logger.log(Level.SEVERE, "Message " + e.getMessage());			
				logger.log(Level.SEVERE, "RESP2 " + e.getRESP2());
				commareaWrapper.setResultText(e.getMessage());
				commareaWrapper.setResultCode(e.getRESP2()); 
				// e.printStackTrace();
			}
		}
				
		if (result.startsWith("OK")){
			logger.log(Level.INFO, "Result Code : " + commareaWrapper.getResultCode());
			logger.log(Level.INFO, "Result Text : " + commareaWrapper.getResultText());
		}

		
		ResponseWrapper response = new ResponseWrapper(
				commareaWrapper.getResultCode(), 
				commareaWrapper.getResultText());
		logger.log(Level.INFO, "Completed");

		return response;
	}	

}
