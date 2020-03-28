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
 * @author Werisson
 */
public class Composta extends Var{
    private String parent;
    private ArrayList<Object> listVars;
    
    public Composta(String type, String id, String escopo,String parent,ArrayList listVars){
      super.setType(type);
      super.setId(id);
      super.setEscopo(escopo);
      this.parent = parent;
      this.listVars = listVars;
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
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
