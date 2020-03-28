/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorsemantico;

/**
 *
 * @author Aurelio
 */
public class Var {

    private String id;
    private String type;
    private String escopo;
    private String value;
    private boolean wasDeclared;

    public Var() {
        this.wasDeclared = false;
    }

    public Var(String type, String id, String escopo) {
        this.id = id;
        this.escopo = escopo;
        this.type = type;
        this.wasDeclared = false;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the wasDeclared
     * 
     */
    public boolean wasDeclared() {
        return wasDeclared;
    }

    /**
     * @param wasDeclared the wasDeclared to set
     */
    public void setWasDeclared(boolean wasDeclared) {
        this.wasDeclared = wasDeclared;
    }

}
