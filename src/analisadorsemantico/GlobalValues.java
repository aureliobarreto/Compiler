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
public class GlobalValues {
  private ArrayList<Object> variaveis;
  public GlobalValues(ArrayList<Object> variaveis){
      this.variaveis = variaveis;
  }
  
  public void addVar(Object var){
        variaveis.add(var);
  }

    /**
     * @return the variaveis
     */
    public ArrayList<Object> getVariaveis() {
        return variaveis;
    }

    /**
     * @param variaveis the variaveis to set
     */
    public void setVariaveis(ArrayList<Object> variaveis) {
        this.variaveis = variaveis;
    }
}
