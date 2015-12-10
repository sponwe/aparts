package br.com.tomvieira.aparts.ejb;

/**
 *
 * @author eder
 */
public class BusinessException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 8785882959773143099L;

	public BusinessException(String message, Throwable cause){
        super(message, cause);
    }

    public BusinessException(String message){
        super(message);
    }
}