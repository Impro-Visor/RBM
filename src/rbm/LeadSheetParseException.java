/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rbm;

/**
 *
 * @author cssummer16
 */
public class LeadSheetParseException extends RuntimeException{
    
    public LeadSheetParseException(String message)
    {
        super(message);
    }
    
    public LeadSheetParseException()
    {
        super("The leadsheet file was not found properly");
    }
    
}
