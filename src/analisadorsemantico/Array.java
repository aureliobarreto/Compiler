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
public class Array extends Var{
  private ArrayList dimensao;
  
    public Array(String type, String id, String escopo, ArrayList dimensao){
       super.setType(type);
       super.setId(id);
       super.setEscopo(escopo);
       this.dimensao = dimensao;
    }

    /**
     * @return the dimensao
     */
    public ArrayList getDimensao() {
        return dimensao;
    }

    /**
     * @param dimensao the dimensao to set
     */
    public void setDimensao(ArrayList dimensao) {
        this.dimensao = dimensao;
    }
    
}
