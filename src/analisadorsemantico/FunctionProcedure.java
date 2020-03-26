/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorsemantico;

import java.util.ArrayList;

/**
 *
 * @author Aurelio
 */
public class FunctionProcedure {
    private String id;
    private String type;
    private String escopo;
    private ArrayList<Param> listParams;
    private ArrayList<Object> listVars; 
    
    public FunctionProcedure(){
        
    }
    
    public FunctionProcedure(String type, String id, ArrayList<Param> listParams,ArrayList<Object> listVars){
     this.type = type;
     this.id = id;
     this.listParams = listParams;
     this.listVars = listVars;
     this.escopo = "global";
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the escopo
     */
    public String getEscopo() {
        return escopo;
    }

    /**
     * @param escopo the escopo to set
     */
    public void setEscopo(String escopo) {
        this.escopo = escopo;
    }

    /**
     * @return the listParams
     */
    public ArrayList<Param> getListParams() {
        return listParams;
    }

    /**
     * @param listParams the listParams to set
     */
    public void setListParams(ArrayList<Param> listParams) {
        this.listParams = listParams;
    }

    /**
     * @return the listVars
     */
    public ArrayList<Object> getListVars() {
        return listVars;
    }

    /**
     * @param listVars the listVars to set
     */
    public void setListVars(ArrayList<Object> listVars) {
        this.listVars = listVars;
    }
}
