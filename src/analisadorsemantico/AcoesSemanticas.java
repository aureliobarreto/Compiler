/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorsemantico;

import analisadorlexico.Token;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author Aurelio
 * @autor Werisson
 */
public class AcoesSemanticas {

    File arquivoSaida;
    ArrayList<Token> tokens;
    Stack<Token> pilhaTokens;
    String nomeArquivo;
    FileOutputStream fos;
    Token token;
    HashMap<String, Object> tabSimbolos;
    Stack<String> escopo;
    String escopoTemp, typeConstTemp, idConstTemp, idStructTemp, typeFunctionTemp, modifierTemp, typeCallVar,
            nameVarTemp;
    ArrayList paramsStrTemporarios;
    boolean inReturn, inCommands, isIndexArrayCheck;

    public AcoesSemanticas(ArrayList<Token> arrayDeTokens, int num, HashMap<String, Object> tabSimbolos) throws FileNotFoundException {
        pilhaTokens = new <Token>Stack();
        this.tabSimbolos = tabSimbolos;
        this.tokens = arrayDeTokens;
        this.arquivoSaida = new File("output semantico" + "/" + "saida" + num + ".txt");
        this.fos = new FileOutputStream(arquivoSaida);
        this.escopo = new <String>Stack();
        this.escopo.push("global");
        this.inReturn = false;
        this.inCommands = false;
        this.isIndexArrayCheck = false;

        /*
        Iterator it = tabSimbolos.keySet().iterator();
        while(it.hasNext()){
            String d;
            d = (String) it.next();
            System.out.println(d);
        } */
    }

    public void run() throws IOException {
        // passar array de tokens para a pilha       
        Iterator it = tokens.iterator();
        while (it.hasNext()) {
            Token t = (Token) it.next();
            pilhaTokens.push((Token) t);
        }
        token = proximoToken();
        start();
        if (arquivoSaida.length() == 0) {
            semErrosSemanticos();
        }
        fechaArquivos();
    }

    /**
     * Método que devolve o proximo token a ser verificado
     *
     * @return Token a ser verificado
     */
    private Token proximoToken() {
        return (pilhaTokens.isEmpty()) ? null : pilhaTokens.pop();
    }

    /**
     * Método que escreve no arquivo caso não ocorra erros semânticos
     *
     * @throws IOException
     */
    private void semErrosSemanticos() throws IOException {
        fos.write("Sucess! No semantic errors".getBytes());
    }

    /**
     * Método que fecha o arquivo de saida
     *
     * @throws IOException
     */
    private void fechaArquivos() throws IOException {
        fos.close();
    }

    /**
     * Método que escreve o erro no arquivo de saída
     *
     * @param linha linha do respectivo erro
     * @param erro mensaem de erro
     * @throws IOException
     */
    private void setErro(int linha, String erro) throws IOException {
        fos.write("Line ".getBytes());
        fos.write(String.valueOf(linha).getBytes());
        fos.write(" ".getBytes());
        fos.write("semantic error: ".getBytes());
        fos.write(erro.getBytes());
        fos.write("\n".getBytes());
    }

    /**
     * Método que escreve o erro no arquivo de saída
     *
     * @param erro mensagem de erro a ser escrita
     * @throws IOException
     */
    private void setErro(String erro) throws IOException {
        fos.write("Semantic error: ".getBytes());
        fos.write(erro.getBytes());
        fos.write("\n".getBytes());
    }

    /**
     * Método que verfica se um token é um tipo primitivo
     *
     * @param t token a ser analisado
     * @return true se for primitivo e falso caso contrário
     */
    private boolean isType(Token t) {
        return (t.getLexema().equals("int") || t.getLexema().equals("real") || t.getLexema().equals("boolean") || t.getLexema().equals("string")) ? true : false;

    }

    /**
     * Método que verifica se uma variável ja foi declarada
     *
     * @param id identificador da variavel
     * @param escopo escopo em que esta contida
     * @return true: declarada, false: não declarada
     */
    private boolean Exist(String id, String escopo) {
        //System.out.println(id + " - "+ escopo);
        Object o = tabSimbolos.get(escopo);
        // System.out.println(o.toString());
        if (o instanceof GlobalValues) {
            GlobalValues x = (GlobalValues) o;
            Iterator it = x.getVariaveis().iterator();

            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Const) {
                    Const constante = (Const) tmp;
                    if (constante.getId().equals(id)) {
                        if (constante.wasDeclared()) {
                            return true;
                        } else {
                            constante.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        if (var.wasDeclared()) {
                            return true;
                        } else {
                            var.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        if (array.wasDeclared()) {
                            return true;
                        } else {
                            array.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        if (struct.wasDeclared()) {
                            return true;
                        } else {
                            struct.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                }
            }
            return false;

        } else if (o instanceof Composta) {
            Composta composta = (Composta) o;
            Iterator it = composta.getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Const) {
                    Const constante = (Const) tmp;
                    if (constante.getId().equals(id)) {
                        if (constante.wasDeclared()) {
                            return true;
                        } else {
                            constante.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        if (var.wasDeclared()) {
                            return true;
                        } else {
                            var.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        if (array.wasDeclared()) {
                            return true;
                        } else {
                            array.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        if (struct.wasDeclared()) {
                            return true;
                        } else {
                            struct.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                }
            }
            return false;
        } else if (o instanceof FunctionProcedure) {
            FunctionProcedure fpTemp = (FunctionProcedure) o;
            Iterator it = fpTemp.getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Const) {
                    Const constante = (Const) tmp;
                    if (constante.getId().equals(id)) {
                        if (constante.wasDeclared()) {
                            return true;
                        } else {
                            constante.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        if (var.wasDeclared()) {
                            return true;
                        } else {
                            var.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        if (array.wasDeclared()) {
                            return true;
                        } else {
                            array.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        if (struct.wasDeclared()) {
                            return true;
                        } else {
                            struct.setWasDeclared(true); // só um teste! não é definitivo!
                            return false;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    public boolean funcProcExists(String id, String params) {
        Object o = tabSimbolos.get("global");
        if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof FunctionProcedure) {
                    if (((FunctionProcedure) tmp).getId().equals(id)) {
                        if (((FunctionProcedure) tmp).getTypesParams().equals(params)) {
                            if (!((FunctionProcedure) tmp).wasDeclared()) {
                                ((FunctionProcedure) tmp).setWasDeclared(true);
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean checkReturnType(String type, String escopo, String idVar) {
        Object o = tabSimbolos.get(escopo);
        if (o instanceof FunctionProcedure) {
            Iterator it = ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(idVar)) {
                        return var.getType().equals(type);
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(idVar)) {
                        return array.getType().equals(type);
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(idVar)) {
                        return true;
                    }
                }
            }

        } else if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(idVar)) {
                        return var.getType().equals(type);
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(idVar)) {
                        return array.getType().equals(type);
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(idVar)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public boolean checkType(String type, Token t) {
        if (type.equals("string")) {
            return t.getTipo().equals("CDC");

        } else if (type.equals("boolean")) {
            return t.getLexema().equals("true") || t.getLexema().equals("false");
        } else if (type.equals("int")) {

            if (t.getTipo().equals("NRO")) {

                String[] aux = t.getLexema().split("[.]");
                return aux.length == 1;
            }
            return false;
        } else if (type.equals("real")) {
            if (t.getTipo().equals("NRO")) {
                String lexema = t.getLexema();
                String aux[] = lexema.split("[.]");
                return aux.length > 1;
            }
            return false;
        }
        return false;
    }

    public boolean parentExist(String id, String escopo) {
        boolean achouLocal = false;
        Object o = tabSimbolos.get(escopo);
        if (o instanceof GlobalValues) {
            GlobalValues x = (GlobalValues) o;
            Iterator it = x.getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        return true;
                    }
                }
            }
        } else if (o instanceof Composta) {
            Composta x = (Composta) o;
            Iterator it = x.getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        return true;
                    }
                }
            }
        } else if (o instanceof FunctionProcedure) {
            FunctionProcedure x = (FunctionProcedure) o;
            Iterator it = x.getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        achouLocal = true;
                        return true;
                    }
                }
            }

        }
        if (!achouLocal) {
            o = tabSimbolos.get("global");
            GlobalValues x = (GlobalValues) o;
            Iterator it = x.getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean existInStruct(String idVar, String idStruct, String escopo) {
        Object o = tabSimbolos.get(escopo);
        if (o instanceof FunctionProcedure) {
            Iterator it = ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idStruct)) {
                        Iterator it1 = ((Composta) obj).getListVars().iterator();
                        while (it1.hasNext()) {
                            Object ob = it1.next();
                            if (ob instanceof Var) {
                                if (((Var) ob).getId().equals(idVar)) {
                                    return true;
                                }
                            } else if (ob instanceof Array) {
                                if (((Array) ob).getId().equals(idVar)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } else if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idStruct)) {
                        Iterator it1 = ((Composta) obj).getListVars().iterator();
                        while (it1.hasNext()) {
                            Object ob = it.next();
                            if (ob instanceof Var) {
                                if (((Var) ob).getId().equals(idVar)) {
                                    return true;
                                }
                            } else if (ob instanceof Array) {
                                if (((Array) ob).getId().equals(idVar)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getTypeVarInStruct(String idVar, String idStruct, String escopo) {
        Object o = tabSimbolos.get(escopo);
        if (o instanceof FunctionProcedure) {
            Iterator it = ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idStruct)) {
                        Iterator it1 = ((Composta) obj).getListVars().iterator();
                        while (it1.hasNext()) {
                            Object ob = it1.next();
                            if (ob instanceof Var) {
                                if (((Var) ob).getId().equals(idVar)) {
                                    return ((Var) ob).getType();
                                }
                            } else if (ob instanceof Array) {
                                if (((Array) ob).getId().equals(idVar)) {
                                    return ((Array) ob).getType();
                                }
                            }
                        }
                    }
                }
            }
        } else if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idStruct)) {
                        Iterator it1 = ((Composta) obj).getListVars().iterator();
                        while (it1.hasNext()) {
                            Object ob = it.next();
                            if (ob instanceof Var) {
                                if (((Var) ob).getId().equals(idVar)) {
                                    return ((Var) ob).getType();
                                }
                            } else if (ob instanceof Array) {
                                if (((Array) ob).getId().equals(idVar)) {
                                    return ((Array) ob).getType();
                                }
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public void conflitParentVerification(String id, String escopo, int linha) throws IOException {
        Object o = tabSimbolos.get(escopo);
        if (o instanceof Composta) {
            if (!((Composta) o).getParent().equals("")) {
                Object obj = tabSimbolos.get(((Composta) o).getEscopo());
                if (obj instanceof FunctionProcedure) {
                    Iterator it = ((FunctionProcedure) obj).getListVars().iterator();
                    while (it.hasNext()) {
                        Object tmp = it.next();
                        if (tmp instanceof Composta) {
                            //System.out.println(((Composta) tmp).getId() + "-" + ((Composta) o).getParent());
                            if (((Composta) tmp).getId().equals(((Composta) o).getParent())) {

                                Iterator i = ((Composta) tmp).getListVars().iterator();
                                while (i.hasNext()) {
                                    Object tmp1 = i.next();
                                    if (tmp1 instanceof Var) {
                                        Var var = (Var) tmp1;
                                        if (var.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    } else if (tmp1 instanceof Array) {
                                        Array array = (Array) tmp1;
                                        if (array.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    } else if (tmp1 instanceof Composta) {
                                        Composta struct = (Composta) tmp1;
                                        if (struct.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    }
                                }
                            }
                        }

                    }
                } else if (obj instanceof GlobalValues) {
                    Iterator it = ((GlobalValues) obj).getVariaveis().iterator();
                    while (it.hasNext()) {
                        Object tmp = it.next();
                        if (tmp instanceof Composta) {
                            if (((Composta) tmp).getId().equals(((Composta) o).getParent())) {
                                Iterator i = ((Composta) tmp).getListVars().iterator();
                                while (i.hasNext()) {
                                    Object tmp1 = i.next();
                                    if (tmp1 instanceof Var) {
                                        Var var = (Var) tmp1;
                                        if (var.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    } else if (tmp1 instanceof Array) {
                                        Array array = (Array) tmp1;
                                        if (array.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    } else if (tmp1 instanceof Composta) {
                                        Composta struct = (Composta) tmp1;
                                        if (struct.getId().equals(id)) {
                                            setErro(linha, "In the parent struct there is already a variable with the name \"" + id + "\"");
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }

    }

    public String getTypeVar(String id, String escopo) {
        Object o = tabSimbolos.get(escopo);
        if (o instanceof FunctionProcedure) {
            Iterator it = ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Var) {
                    if (((Var) obj).getId().equals(id)) {
                        return ((Var) obj).getType();
                    }
                } else if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(id)) {
                        return ((Composta) obj).getType();
                    }
                } else if (obj instanceof Array) {

                    if (((Array) obj).getId().equals(id)) {
                        return ((Array) obj).getType();
                    }
                }
            }
        } else if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Var) {
                    if (((Var) obj).getId().equals(id)) {
                        return ((Var) obj).getType();
                    }
                } else if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(id)) {
                        return ((Composta) obj).getType();
                    }
                } else if (obj instanceof Array) {
                    if (((Array) obj).getId().equals(id)) {
                        return ((Array) obj).getType();
                    }
                }
            }
        }
        return "";
    }

    public boolean intVerification(String indice) {
        char[] arrayString;
        arrayString = indice.toCharArray();
        for (int i = 0; i < arrayString.length - 1; i++) {
            if (arrayString[i] == '-' || arrayString[i] == '.') {
                return false;
            }
        }

        return true;
    }

    /**
     * Método que transforma o array de parametros em string
     *
     * @param e Array a ser transformado
     * @return String contendo tipos de paremetros
     */
    private String parametros(ArrayList e) {
        String aux = "";
        Iterator it = e.iterator();
        while (it.hasNext()) {
            aux = aux + (String) it.next();
        }
        return aux;
    }
//********************** INICIO DOS PROCEDIMENTOS ***************************************

    private void start() throws IOException {
        globalValues();
        functionsProcedures();
    }
//********************** GLOBAL VALUES *****************************************************
//                  DECLARAÇÃO DE VARIÁVEIS 

    private void globalValues() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("var")) {
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("const")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                constValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

        } else if (token.getLexema().equals("const")) {
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                constValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("var")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

        } else if (token.getLexema().equals("function") || token.getLexema().equals("procedures")) {
            // vazio
        } else {
            //erro sintatico
        }

    }

//********** CONST VALUES DECLARATION **********************************************************************
    private void constValuesDeclaration() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (isType(token)) {
            typeConstTemp = token.getLexema();
            token = proximoToken();
            constValuesAtribuition();
            constMoreAtribuition();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(";")) {
                token = proximoToken();
                constValuesDeclaration();

            } else {
                //erro sintatico
            }
        } else if (token.getLexema().equals("}")) {
            //vazio
        } else {
            //erro sintatico
        }

    }
//********** CONST VALUES ATRIBUITION *****************************************************************

    private void constValuesAtribuition() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("IDE")) {
            idConstTemp = token.getLexema();
            if (Exist(token.getLexema(), escopo.peek())) {
                setErro(token.getLinha(), "The Const \"" + token.getLexema() + "\" was already been declared in the scope: " + escopo.peek());
            }
            token = proximoToken();
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
        } else if (token.getLexema().equals("=")) {
            token = proximoToken();
            valueConst();
        } else {
            //erro sintatico
        }
    }

//********** VALUE CONST ******************************************************************************
    private void valueConst() throws IOException {
        if (token == null) {
            //erro sintatico
        } else if (token.getTipo().equals("NRO")) {
            if (checkType(typeConstTemp, token)) {

            } else {
                setErro(token.getLinha(), "The value of const \"" + idConstTemp + "\" does not match the type");
            }
            token = proximoToken();
        } else if (token.getTipo().equals("CDC")) {
            if (checkType(typeConstTemp, token)) {

            } else {
                setErro(token.getLinha(), "The value of const \"" + idConstTemp + "\" does not match the type");
            }
            token = proximoToken();
        } else if (token.getLexema().equals("true") || token.getLexema().equals("false")) {
            if (checkType(typeConstTemp, token)) {

            } else {
                setErro(token.getLinha(), "The value of const \"" + idConstTemp + "\" does not match the type");
            }
            token = proximoToken();
        } else {
            //erro sintatico
        }
    }
//********** CONST MORE ATRIBUITION ********************************************************************

    private void constMoreAtribuition() throws IOException {
        if (token == null) {
            //erro sintatico
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            constValuesAtribuition();
            constMoreAtribuition();
        } else if (token.getLexema().equals(";")) {
            //vazio
        } else {
            //erro sintatico
        }
    }
//********** VAR VALUES DECLARATION *********************************************************************

    private void varValuesDeclaration() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (isType(token)) {
            token = proximoToken();
            varValuesAtribuition();
            varMoreAtribuition();

            if (token == null) {
                //erro sintatico
            } else if (token.getLexema().equals(";")) {
                token = proximoToken();
                varValuesDeclaration();

            } else {
                //erro sintatico
            }
        } else if (token.getLexema().equals("typedef")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("struct")) {
                token = proximoToken();
                ideStruct();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

        } else if (token.getLexema().equals("struct")) {
            token = proximoToken();
            ideStruct();
            varValuesDeclaration();
        } else if (token.getLexema().equals("}")) {
            // vazio  
        } else {
            //erro sintatico

        }

    }

//********** VAR VALUES ATRIBUITION *********************************************************************
    private void varValuesAtribuition() throws IOException {
        if (token == null) {
            //erro sintatico
        } else if (token.getTipo().equals("IDE")) {
            conflitParentVerification(token.getLexema(), escopo.peek(), token.getLinha());
            if (Exist(token.getLexema(), escopo.peek())) {
                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was already been declared in the scope: " + escopo.peek());
            }
            token = proximoToken();
            arrayVerification();
        } else {
            //erro sintatico            
        }
    }
//********** VAR MORE ATRIBUITION ***********************************************************************

    private void varMoreAtribuition() throws IOException {
        if (token == null) {
            //erro sintatico
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            varValuesAtribuition();
            varMoreAtribuition();
        } else if (token.getLexema().equals(";")) {
            // vazio           
        } else {
            //erro sintatico
        }
    }
//********** VAR ARRAY VERIFICATION *********************************************************************     

    private void arrayVerification() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("[")) {
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("NRO")) {
                if (!intVerification(token.getLexema())) {
                    setErro(token.getLinha(), "The array index is not an integer type");
                }

                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
            } else if (token.getLexema().equals("]")) {
                token = proximoToken();
                arrayVerification();
            } else {
                //erro sintatico
            }
        } else if (token.getLexema().equals(",") || token.getLexema().equals(";")) {
            // vazio
        } else {
            //erro sintatico
        }

    }
//********** IDE STRUCT *********************************************************************************

    private void ideStruct() throws IOException {
        if (token == null) {
            //erro sintatico
        } else if (token.getTipo().equals("IDE")) {
            if (Exist(token.getLexema(), escopo.peek())) {
                setErro(token.getLinha(), "The Struct \"" + token.getLexema() + "\" was already been declared in the scope: " + escopo.peek());
            }
            idStructTemp = token.getLexema();
            token = proximoToken();
            ideStruct2();
            escopo.pop();
        } else {
            //erro sintatico
        }
    }
//********** IDE STRUCT 2 *******************************************************************************

    private void ideStruct2() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("{")) {
            escopo.push(idStructTemp + "@" + escopo.peek());
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("var")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else if (token.getLexema().equals("extends")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("IDE")) {
                if (!parentExist(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The struct \"" + token.getLexema() + "\" was not declared");
                }
                escopo.push(idStructTemp + "@" + escopo.peek());
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("var")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        }
    }

//********** FUNCTIONS PROCEDURES ***********************************************************************
    private void functionsProcedures() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("function")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (isType(token) || token.getTipo().equals("IDE")) {
                typeFunctionTemp = token.getLexema();
                token = proximoToken();
            } else {
                //erro sintatico
            }
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("IDE")) {
                escopoTemp = token.getLexema();
                token = proximoToken();
            } else {
                //erro sintatico
            }
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                paramsStrTemporarios = new ArrayList();
                paramList();
                if (funcProcExists(escopoTemp, parametros(paramsStrTemporarios))) {
                    setErro(token.getLinha(), "The Function \"" + escopoTemp + "\" was already been declared in the Scope: " + escopo.peek());
                }
                escopo.push(escopoTemp + parametros(paramsStrTemporarios));

            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("var")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
                commands();
                returns();

            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
                escopo.pop();
                functionsProcedures();

            } else {
                //erro sintatico
            }
        } else if (token.getLexema().equals("procedure")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("IDE") || token.getLexema().equals("start")) {
                escopoTemp = token.getLexema();
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                paramsStrTemporarios = new ArrayList();
                paramList();
                if (funcProcExists(escopoTemp, parametros(paramsStrTemporarios))) {
                    setErro(token.getLinha(), "The Procedure \"" + escopoTemp + "\" was already been declared in the Scope: " + escopo.peek());
                }
                escopo.push(escopoTemp + parametros(paramsStrTemporarios));
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("var")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                varValuesDeclaration();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
                commands();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
                escopo.pop();
                functionsProcedures();
            } else {
                //erro sintatico
            }
        } else {
            //erro sintatico
        }
    }
//*************** PARAM LIST **********************************************************************

    private void paramList() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (isType(token)) {
            paramsStrTemporarios.add("@" + token.getLexema());
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("IDE")) {
                token = proximoToken();
                moreParam();
            } else {
                //erro sintatico   
            }
        } else if (token.getLexema().equals(")")) {
            // vazio
        } else {
            //erro sintatico
        }

    }
//*************** MORE PARAM **********************************************************************

    private void moreParam() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            paramList();
        } else if (token.getLexema().equals(")")) {
            // vazio
        } else {
            //erro sintatico
        }
    }
//*************** COMMANDS **********************************************************************

    private void commands() throws IOException {
        if (token == null) {
            setErro("Commands expected");
            return;
        } else if (token.getLexema().equals("if")) {
            ifStatemant();
            commands();
        } else if (token.getLexema().equals("while")) {
            whileStatemant();
            commands();
        } else if (token.getLexema().equals("read")) {
            readStatemant();
            commands();
        } else if (token.getLexema().equals("print")) {
            printStatemant();
            commands();
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("local") || token.getLexema().equals("global")) {
            assignment();
            commands();
        } else if (token.getLexema().equals("}") || token.getLexema().equals("return")) {
            //vazio
        }
    }
//*************** COMMANDS EXPRESSIONS  **********************************************************************

    private void commandsExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else {
            relationalExp();
            optLogicalExp();
        }
    }
//*************** RETURNS **********************************************************************

    private void returns() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("return")) {
            inReturn = true;
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("NRO")
                    || token.getTipo().equals("CDC")) {
                if (!checkType(typeFunctionTemp, token)) {
                    setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                }
                token = proximoToken();
            } else if (token.getTipo().equals("IDE")) {
                if (Exist(token.getLexema(), escopo.peek())) {
                    if (!checkReturnType(typeFunctionTemp, escopo.peek(), token.getLexema())) {
                        setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                    }
                } else {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                }
                callVariable();

            } else if (token.getLexema().equals("global") || token.getLexema().equals("local")) {
                callVariable();

            } else {
                relationalExp();
            }
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
        } else if (token.getLexema().equals(";")) {
            token = proximoToken();
        } else {
            //erro sintatico
        }

    }

//*************** IF STATEMANT **********************************************************************
    private void ifStatemant() throws IOException {

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("if")) {
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                commandsExp();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            }
            if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("then")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                commands();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
                elseStatemant();
            } else {
                //erro sintatico
            }

        }
    }
//*************** ELSE STATEMANT ***********************************************************

    private void elseStatemant() throws IOException {

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("else")) {
            token = proximoToken();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                commands();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

        } else if (token.getLexema().equals("return") || token.getLexema().equals("}")) {
            //vazio  
        } else {
            //erro sintatico
        }
    }
//*************** WHILE STATEMANT ***********************************************************

    private void whileStatemant() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("while")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                commandsExp();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("{")) {
                token = proximoToken();
                commands();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("}")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        }
    }
//*************** READ STATEMANT **********************************************************************

    private void readStatemant() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("read")) {
            token = proximoToken();
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("(")) {
            token = proximoToken();
            readParams();
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(")")) {
            token = proximoToken();

        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(";")) {
            token = proximoToken();

        } else {
            //erro sintatico
        }

    }

    private void readParams() throws IOException {
        callVariable();
        moreReadParams();
    }

    private void moreReadParams() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            readParams();
        } else if (token.getLexema().equals(")")) {
            //vazio
        } else {
            //erro sintatico
        }

    }

//*************** PRINT STATEMANT **********************************************************
    private void printStatemant() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("print")) {
            token = proximoToken();
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("(")) {
            token = proximoToken();
            printParams();
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(")")) {
            token = proximoToken();

        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(";")) {
            token = proximoToken();

        } else {
            //erro sintatico
        }

    }

    private void printParams() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        }
        printParam();
        morePrintParams();

    }

    private void printParam() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("CDC")) {
            token = proximoToken();
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("local") || token.getLexema().equals("global")) {
            callVariable();
        } else {
            //erro sintatico
        }
    }

    private void morePrintParams() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            printParams();
        } else if (token.getLexema().equals(")")) {
            //vazio
        } else {
            //erro sintatico
        }
    }
//*************** ASSIGNMENT **********************************************************************

    private void assignment() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("local") || token.getLexema().equals("global")) {
            inCommands = true;
            callVariable();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("=")) {
                token = proximoToken();
                assign2();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
            } else if (token.getLexema().equals(";")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

        } else if (token.getLexema().equals("global") || token.getLexema().equals("local")
                || token.getTipo().equals("IDE") || token.getLexema().equals("++") || token.getLexema().equals("--")
                || token.getTipo().equals("NRO") || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getLexema().equals("!")) {
            unaryOp();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(";")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else {

        }

    }

    private void assign2() throws IOException {

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("global") || token.getLexema().equals("local")) {
            if (token.getTipo().equals("IDE")) {
                if (Exist(token.getLexema(), escopo.peek())) {
                    if (!getTypeVar(token.getLexema(), escopo.peek()).equals(typeCallVar)) {
                        setErro(token.getLinha(), "The types atribuition not match");
                    }
                } else {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                }
            }
            token = proximoToken();
        } else if (token.getTipo().equals("CDC")) {
            if (typeCallVar == null) {

            } else {
                if (!typeCallVar.equals("string")) {
                    setErro(token.getLinha(), "The types atribuition not match");
                }
            }
            token = proximoToken();
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("++") || token.getLexema().equals("--")
                || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getLexema().equals("!")) {
            expression();
        } else if (token.getTipo().equals("NRO")) {
            if (typeCallVar == null) {

            } else {
                if (!checkType(typeCallVar, token)) {
                    setErro(token.getLinha(), "The types atribuition not match");
                }
            }
            token = proximoToken();
        } else {
            //erro sintatico
        }
    }

//*************** EXPRESSION **********************************************************************
    private void expression() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
            // logical ou aritmetic
        }
        logicalExp();

    }

//*************** RELATIONAL EXPRESSION ***********************************************************    
    private void relationalExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("(")) {
            token = proximoToken();
            logicalExp();

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else {
            aritmeticExp();
            possRelExp();
        }
    }

//*************** OPT LOGICAL EXPRESSION ***********************************************************   
    private void optLogicalExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("&&") || token.getLexema().equals("||")) {
            token = proximoToken();
            logicalExp();
        } else if (token.getLexema().equals(")")) {
            //vazio  
        } else {
            //erro sintatico
        }
    }

//*************** LOGICAL EXPRESSION ***************************************************************     
    private void logicalExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        }
        relationalExp();
        optLogicalExp();
    }

//*************** POSS REL EXPRESSION ***************************************************************
    private void possRelExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(">") || token.getLexema().equals("<") || token.getLexema().equals(">=")
                || token.getLexema().equals("<=")) {
            token = proximoToken();
            aritmeticExp();
            inequalityExp();

        } else if (token.getLexema().equals("!=") || token.getLexema().equals("==")) {
            token = proximoToken();
            aritmeticExp();
            inequalityExp();
            equalityExp();
 
        } else {
            //erro sintatico
        }
    }

//*************** EQUALITY EXPRESSION ***************************************************************
    private void equalityExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("!=") || token.getLexema().equals("==")) {
            token = proximoToken();
            aritmeticExp();
            inequalityExp();
            equalityExp();
        } else if (token.getLexema().equals(")") || token.getLexema().equals("&&") || token.getLexema().equals("||")) {
            // vazio
        } else {
            //erro sintatico
        }

    }

//*************** INEQUALITY EXPRESSION ***************************************************************
    private void inequalityExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(">") || token.getLexema().equals("<") || token.getLexema().equals(">=")
                || token.getLexema().equals("<=")) {
            token = proximoToken();
            aritmeticExp();
            inequalityExp();
            equalityExp();
        } else if (token.getLexema().equals(")") || token.getLexema().equals("&&") || token.getLexema().equals("||")
                || token.getLexema().equals("==") || token.getLexema().equals("!=")) {
            // vazio
        } else {
            //erro sintatico
        }
    }

//*************** ARITIMETIC EXPRESSION ***************************************************************
    private void aritmeticExp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
            //primeiro unary Op
        } else if (token.getLexema().equals("(")) {
            token = proximoToken();
            relationalExp();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }

        } else {
            operation();
            opSum();
        }
    }

//*************** OPERATION ***************************************************************************
    private void operation() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        }
        opUnary();
        opMultiplication();
    }

//*************** OP SUM  *****************************************************************************
    private void opSum() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("+") || token.getLexema().equals("-")) {
            token = proximoToken();
            operation();
            opSum();
        } else if (token.getLexema().equals("global") || token.getLexema().equals("local")
                || token.getTipo().equals("IDE") || token.getLexema().equals("++") || token.getLexema().equals("--")
                || token.getTipo().equals("NRO") || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getLexema().equals("!") || token.getLexema().equals("==") || token.getLexema().equals("!=")
                || token.getLexema().equals(">") || token.getLexema().equals("<") || token.getLexema().equals(">=")
                || token.getLexema().equals("<=") || token.getLexema().equals(")") || token.getLexema().equals(";")) {

            //vazio           
        } else {
            //erro sintatico
        }
    }

//*************** OP MULTIPLICATION *******************************************************************
    private void opMultiplication() throws IOException {
        if (token == null) {
            //erro sintatico
            return;

        } else if (token.getLexema().equals("/") || token.getLexema().equals("*")) {
            token = proximoToken();
            opUnary();
            opMultiplication();
        } else if (token.getLexema().equals("+") || token.getLexema().equals("-")
                || token.getLexema().equals("==") || token.getLexema().equals("!=")
                || token.getLexema().equals("/") || token.getLexema().equals("*")
                || token.getLexema().equals(")")) {
            //vazio
        } else {
            //erro sintatico
        }
    }

//*************** OP UNARY ***************************************************************************
    private void opUnary() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("global") || token.getLexema().equals("local")
                || token.getTipo().equals("IDE") || token.getLexema().equals("++") || token.getLexema().equals("--")
                || token.getTipo().equals("NRO") || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getLexema().equals("!")) {

            if (token.getLexema().equals("global") || token.getLexema().equals("local") || token.getTipo().equals("NRO")
                    || token.getLexema().equals("true") || token.getLexema().equals("false")) {
                finalValue();
                if (token.getLexema().equals("++") || token.getLexema().equals("--")) {
                    token = proximoToken();
                }
            } else {
                unaryOp();
            }

        } else if (token.getLexema().equals("(")) {
            aritmeticExp();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else {
            //erro sintatico
        }
    }

//*************** UNARY OP ***************************************************************************
    private void unaryOp() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("++") || token.getLexema().equals("--")) {
            token = proximoToken();
            finalValue();
        } else if (token.getLexema().equals("global") || token.getLexema().equals("local")
                || token.getTipo().equals("IDE") || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getTipo().equals("NRO")) {
            finalValue();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("++") || token.getLexema().equals("--")) {
                token = proximoToken();
            }
        } else if (token.getLexema().equals("!")) {
            token = proximoToken();
            callVariable();
        } else {
            //erro sintatico
        }

    }

//*************** FINAL VALUE ************************************************************************
    private void finalValue() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        }
        if (token.getLexema().equals("global") || token.getLexema().equals("local") || token.getTipo().equals("IDE")) {
            modifier();
        } else if (token.getTipo().equals("NRO") || token.getLexema().equals("true") || token.getLexema().equals("false")) {
            token = proximoToken();
        } else {
            //erro sintatico
        }
    }

//*************** CALL VARIABLE **********************************************************************
    private void callVariable() throws IOException {
        if (token == null) {
            //erro sintatico
        }
        //System.out.println(token.getLinha() + " - " + token.getLexema() + " - " + escopo.peek() + " - " + inCommands);
        modifier();
        paths();
    }
//*************** MODIFIER ***************************************************************************

    private void modifier() throws IOException {
        modifierTemp = token.getLexema();
        if (token.getLexema().equals("global") || token.getLexema().equals("local")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(".")) {
                token = proximoToken();
                if (token == null) {
                    //erro sintatico
                    return;
                } else if (token.getTipo().equals("IDE")) {
                    if (inReturn) {
                        if (modifierTemp.equals("global")) {
                            if (Exist(token.getLexema(), "global")) {
                                if (!checkReturnType(typeFunctionTemp, "global", token.getLexema())) {
                                    setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                                }
                            } else {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (Exist(token.getLexema(), escopo.peek())) {
                                if (!checkReturnType(typeFunctionTemp, escopo.peek(), token.getLexema())) {
                                    setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                                }
                            } else {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            }
                        }
                        inReturn = false;
                    }

                    if (inCommands) {
                        if (modifierTemp.equals("global")) {
                            if (!Exist(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), "global");
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Exist(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                            }
                        }
                        inCommands = false;
                    }

                    if (isIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (!Exist(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), "global");
                                if (typeCallVar.equals("int")) {
                                    setErro(token.getLinha(), "The array index is not an integer type");
                                }
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Exist(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                                if (typeCallVar.equals("int")) {
                                    setErro(token.getLinha(), "The array index is not an integer type");
                                }
                            }
                        }
                        //isIndexArrayCheck = false;
                    }

                    token = proximoToken();
                }
            }

        } else if (token.getTipo().equals("IDE")) {
            if (inCommands) {
                if (!Exist(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                } else {
                    typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                }
                inCommands = false;
            }

            if (isIndexArrayCheck) {
                typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                if (typeCallVar.equals("int")) {
                    setErro(token.getLinha(), "The array index is not an integer type");
                }

            }
            nameVarTemp = token.getLexema();
            token = proximoToken();
        }
    }

    private void paths() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(".")) {
            struct();
        } else if (token.getLexema().equals("[")) {
            matrAssign();
        } else if (token.getLexema().equals(",") || token.getLexema().equals("=") || token.getLexema().equals("++")
                || token.getLexema().equals("--") || token.getLexema().equals(";") || token.getLexema().equals("]")
                || token.getLexema().equals(")") || token.getLexema().equals("*") || token.getLexema().equals("/")) {
            //vazio
        } else {
            //erro sintatico
        }

    }

    private void struct() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(".")) {
            token = proximoToken();
        } else {
            //erro sintatico
        }

        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("IDE")) {
            if (!existInStruct(token.getLexema(), nameVarTemp, escopo.peek())) {
                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
            } else {
                typeCallVar = getTypeVarInStruct(token.getLexema(), nameVarTemp, escopo.peek());
                if (isIndexArrayCheck) {
                    if (!typeCallVar.equals("int")) {
                        setErro(token.getLinha(), "The array index is not an integer type");
                    }
                    isIndexArrayCheck = false;
                }
            }
            token = proximoToken();
            paths();
        } else {
            //erro sintatico
        }
    }

    private void matrAssign() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("[")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getTipo().equals("NRO")) {
                if (!intVerification(token.getLexema())) {
                    setErro(token.getLinha(), "The array index is not an integer type");
                }
                token = proximoToken();
            } else if (token.getLexema().equals("global") || token.getLexema().equals("local") || token.getTipo().equals("IDE")) {
                isIndexArrayCheck = true;
                callVariable();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("]")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else {
            //erro sintatico
        }
        paths();
    }
//************** CALL FUNCTIONS PROCEDURES ***************************************************

    private void callProcedureFunction() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("IDE")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                realParamList();
            } else {
                //erro sintatico
            }

            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals(")")) {
                token = proximoToken();
            } else {
                //erro sintatico
            }
        } else {
            //erro sintatico
        }
    }

    private void realParamList() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("NRO")
                || token.getTipo().equals("CDC") || token.getTipo().equals("IDE") || token.getLexema().equals("global")
                || token.getLexema().equals("local")) {
            realParam();
            moreRealParam();

        } else if (token.getLexema().equals(")")) {
            //vazio           
        } else {
            //erro sintatico
        }
    }

    private void realParam() throws IOException {
        if (token == null) {
            setErro("( expected");
            return;
        } else if (token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("NRO")
                || token.getTipo().equals("CDC")) {
            valueParam();
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("global") || token.getLexema().equals("local")) {
            callVariable();
        } else {
            setErro("");
            //erro();
        }
    }

    private void moreRealParam() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals(",")) {
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("NRO")
                    || token.getTipo().equals("CDC") || token.getTipo().equals("IDE") || token.getLexema().equals("global")
                    || token.getLexema().equals("local")) {
                realParam();
                moreRealParam();
            }
        } else if (token.getLexema().equals(")")) {
            //vazio
        } else {
            //erro sintatico
        }
    }

    private void valueParam() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getTipo().equals("NRO")) {
            token = proximoToken();
        } else if (token.getTipo().equals("CDC")) {
            token = proximoToken();
        } else if (token.getLexema().equals("true") || token.getLexema().equals("false")) {
            token = proximoToken();
        } else {
            //erro sintatico
        }
    }
}
