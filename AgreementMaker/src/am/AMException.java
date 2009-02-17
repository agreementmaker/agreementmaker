package am;

/**
 * This Exception is used by the AgreementMaker to handle Expected Exception
 * Usually Unexepected exception are not catched, in fact the system can't do anything in that case more then ex.printStackTrace() to leave the notification to the developer
 * But sometimes some Exception are expected because can be caused by user input.
 * A developer should catch the Java Exception and throw a new AMException with a message that will be printed to user, so that the user will understand his mystake.
 * To standardize messages we can use some final string, but a developer can always use a personal message
 * Example:
 * 
 * USER INTERFACE CLASS
 * userinterfacemethod() throws all other exception{
 * 		try{
 * 			controlClass.methodWithExpectedExpcetion()
 *			//continue with operations	 
 * 		}
 * 		catch(AMException e){
 * 			JOPtionPane.showMessageDialog(frame, e.getMessage());
 * 		}
 * }
 * 
 *  CONTROL CLASS
 *  methodWithExpectedExpcetion() throws AMException, all other exception{
 *  	try{
 *  		openFile(f)
 *  	}
 *     catch(fileNotFoundException){
 *     		throw new AMException(AMException.FILE_NOT_FOUND+"\n"+filename);
 *     }
 *     //other methods with other unexpected exception
 *  
 *  }
 * 
 * 
 */
public class AMException extends Exception {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869591038480687754L;
	public static final String FILE_NOT_FOUND = "The system couldn't find the selected file";
	
	
	public AMException() {
		super();
	}
	
	public AMException(String message) {
		super(message);
	}
	

}
