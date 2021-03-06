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
            nameAssign1Temp, nameAssign2Temp, nameCallProcCommandsTemp, typeIndexArrayTemp, nameIndexArrayTemp, paramFuncProcTemp,
            nameProcFunctTemp, typeAssign1, typeAssign2, nameStructReturnTemp, typeCallProcCommands, parentStruct, nameCallProcReturnTemp,
            nameCallProcFunctTemp;
    ArrayList paramsStrTemporarios;
    boolean inReturn, inCommands, inIndexArrayCheck, inCallProcFunct, inAssign1, inAssign2, inCallProcCommands, isVarGlobal, inCallProcReturn;

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
        this.inIndexArrayCheck = false;
        this.inCallProcFunct = false;
        this.inAssign1 = false;
        this.inAssign2 = false;
        this.inCallProcCommands = false;
        this.isVarGlobal = false;
        this.inCallProcReturn = false;

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
     * @return Token a ser verificado
     */
    private Token proximoToken() {
        return (pilhaTokens.isEmpty()) ? null : pilhaTokens.pop();
    }
    /**
     * Método que olha o próximo token
     * @return Proximo Token
     */    
    private Token lookAHead() {
        Token tokenAux;
        if (!pilhaTokens.isEmpty()) {
            tokenAux = proximoToken();
            pilhaTokens.push(tokenAux);
            return tokenAux;
        } else {
            return null;
        }

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

        Object o = tabSimbolos.get(escopo);
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

                if (tmp instanceof Var) {
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
    /**
     * Método que verifica se uma função ou procedimento existe
     * @param id nome da função
     * @param params string concatenada com seus parâmetros
     * @return true: existe, false; não existe
     */
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
    /**
     * Método que pega o tipo de uma funçao
     * @param id nome da função
     * @param params  string concatenada com seus parâmetros
     * @return String contendo o tipo da função caso não exista retorna string vazia ""
     */
    public String getTypeFunctProc(String id, String params) {
        Object o = tabSimbolos.get("global");
        if (o instanceof GlobalValues) {
            Iterator it = ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();
                if (tmp instanceof FunctionProcedure) {
                    if (((FunctionProcedure) tmp).getId().equals(id)) {
                        if (((FunctionProcedure) tmp).getTypesParams().equals(params)) {
                            return ((FunctionProcedure) tmp).getType();

                        }
                    }
                }
            }
        }
        return "";
    }
    /**
     * Método que passa as informações de uma struct para outra
     * @param idParent nome da struct pai
     * @param escopoParent escopo da struct pai
     * @param idChild nome da struct filho
     * @param escopoChild escopo da struct filho
     */
    public void cloneStruct(String idParent, String escopoParent, String idChild, String escopoChild) {
        Object o = tabSimbolos.get(escopoParent);
        Composta parentTemp = null;
        if (o instanceof GlobalValues) {
            Iterator it = (Iterator) ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idParent)) {
                        parentTemp = (Composta) obj;
                    }
                }
            }

            Object o2 = tabSimbolos.get(escopoChild);
            if (o2 instanceof GlobalValues) {
                Iterator it2 = (Iterator) ((GlobalValues) o2).getVariaveis().iterator();
                while (it2.hasNext()) {
                    Object obj = it2.next();
                    if (obj instanceof Composta) {
                        if (((Composta) obj).getId().equals(idChild)) {
                            ((Composta) obj).setParent(parentTemp.getParent());
                            ((Composta) obj).setListVars(parentTemp.getListVars());
                            //teste para ver se tá fazendo a transferência correta
                            /* Iterator itTeste = (Iterator) ((GlobalValues) o2).getVariaveis().iterator();
                            while (itTeste.hasNext()) {
                                Object ob = itTeste.next();
                                if (ob instanceof Composta) {
                                    if (((Composta) ob).getId().equals(idChild)) {
                                        System.out.println(((Composta) ob).getParent());
                                        System.out.println(((Composta) ob).getListVars().size());
                                        Iterator itTeste2 = (Iterator) ((Composta) ob).getListVars().iterator();
                                        while (itTeste2.hasNext()) {
                                            Object gg = itTeste2.next();
                                            if (gg instanceof Var) {
                                                System.out.println(((Var) gg).getId());
                                            }
                                        }
                                    }
                                }

                            } */

                        }
                    }
                }
            } else if (o2 instanceof FunctionProcedure) {
                Iterator it2 = (Iterator) ((FunctionProcedure) o2).getListVars().iterator();
                while (it2.hasNext()) {
                    Object obj = it2.next();
                    if (obj instanceof Composta) {
                        if (((Composta) obj).getId().equals(idChild)) {
                            ((Composta) obj).setParent(parentTemp.getParent());
                            ((Composta) obj).setListVars(parentTemp.getListVars());
                        }
                    }
                }
            }
            
        } else if (o instanceof FunctionProcedure) {
            Iterator it = (Iterator) ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Composta) {
                    if (((Composta) obj).getId().equals(idParent)) {
                        parentTemp = (Composta) obj;
                    }
                }
            }

            Object o2 = tabSimbolos.get(escopoChild);
            if (o2 instanceof GlobalValues) {
                Iterator it2 = (Iterator) ((GlobalValues) o2).getVariaveis().iterator();
                while (it2.hasNext()) {
                    Object obj = it2.next();
                    if (obj instanceof Composta) {
                        if (((Composta) obj).getId().equals(idChild)) {
                            ((Composta) obj).setParent(parentTemp.getParent());
                            ((Composta) obj).setListVars(parentTemp.getListVars());
                        }
                    }
                }
            } else if (o2 instanceof FunctionProcedure) {
                Iterator it2 = (Iterator) ((FunctionProcedure) o2).getListVars().iterator();
                while (it2.hasNext()) {
                    Object obj = it2.next();
                    if (obj instanceof Composta) {
                        if (((Composta) obj).getId().equals(idChild)) {
                            ((Composta) obj).setParent(parentTemp.getParent());
                            ((Composta) obj).setListVars(parentTemp.getListVars());
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Método que verifica se uma variável é constante
     * @param id nome da variável
     * @return true: é constantte, false: não é
     */
    public boolean isConst(String id) {
        Object o = tabSimbolos.get("global");
        if (o instanceof GlobalValues) {
            GlobalValues gv = (GlobalValues) o;
            Iterator it = gv.getVariaveis().iterator();

            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Const) {
                    Const constante = (Const) tmp;
                    if (constante.getId().equals(id)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Verifica se uma variavel esta contida em um determinado escopo
     *
     * @param id id da variavel a ser buscada
     * @param escopo escopo a ser feito a varredura
     * @return true para está contida e false para não esta contida
     */
    private boolean Contains(String id, String escopo) {

        Object o = tabSimbolos.get(escopo);
        if (o instanceof GlobalValues) {
            GlobalValues x = (GlobalValues) o;
            Iterator it = x.getVariaveis().iterator();

            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Const) {
                    Const constante = (Const) tmp;
                    if (constante.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        return true;
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
                        return true;
                    }
                } else if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        return true;
                    }
                }
            }
            return false;
        } else if (o instanceof FunctionProcedure) {
            FunctionProcedure fpTemp = (FunctionProcedure) o;
            Iterator it = fpTemp.getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = it.next();

                if (tmp instanceof Var) {
                    Var var = (Var) tmp;
                    if (var.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Array) {
                    Array array = (Array) tmp;
                    if (array.getId().equals(id)) {
                        return true;
                    }
                } else if (tmp instanceof Composta) {
                    Composta struct = (Composta) tmp;
                    if (struct.getId().equals(id)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    /**
     * Método que verifica se o tipo bate com o tipo da variável
     * @param type tipo a ser analisado
     * @param escopo escopo 
     * @param idVar nome da variável a ser comparada
     * @return true: tipos correspondem, false: não correspondem
     */
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
    /**
     * Método que checa o tipo do valor de uma constante
     * @param type tipo esperado
     * @param t token a ser analisado
     * @return  true: corresponde ao parameto type, false: não corresponde
     */
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
    /**
     * Método que verifica se uma varíavel é do tipo struct
     * @param id nome da variável
     * @param escopo escopo a realizar a busca
     * @return true: variável é do tipo struct, false: não é
     */
    public boolean isStruct(String id, String escopo) {
        Object o = tabSimbolos.get(escopo);

        if (o instanceof GlobalValues) {
            Iterator it = (Iterator) ((GlobalValues) o).getVariaveis().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        return true;
                    }
                }
            }
            return false;
        } else if (o instanceof FunctionProcedure) {
            Iterator it = (Iterator) ((FunctionProcedure) o).getListVars().iterator();
            while (it.hasNext()) {
                Object tmp = (Object) it.next();
                if (tmp instanceof Composta) {
                    if (((Composta) tmp).getId().equals(id)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    /**
     * Método que verifica se a struct pai existe
     * @param id nome da struct pai
     * @param escopo escopo a se buscar
     * @return true: existe, false: não existe
     */
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
    /**
     * Método que verifica se existe um determinado atributo em uma struct
     * @param idVar nome do atributo
     * @param idStruct nome da struct a ser feita a busca
     * @param escopo escopo da struct
     * @return true: existe, false: não existe.
     */
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
        }
        return false;
    }
    /**
     * Método que busca o tipo de um atributo de uma struct
     * @param idVar nome do atributo
     * @param idStruct nome da struct 
     * @param escopo escopo da struct
     * @return string com o tipo do atributo, caso não exista retorna string vazia ""
     */
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
        }
        return "";
    }
    /**
     * Método que verifica se um atributo da struct filho ja foi declarado na struct pai
     * @param id nome da variável
     * @param escopo escopo
     * @param linha linha do código
     * @throws IOException 
     */
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
    /**
     * Método que busca o tipo de uma varíavel num determinado escopo
     * @param id nome da variável
     * @param escopo escopo a ser feita a busca
     * @return string com o tipo da variável, caso variável não exista retorna null
     */
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
                } else if (obj instanceof Const) {
                    if (((Const) obj).getId().equals(id)) {
                        return ((Const) obj).getType();
                    }
                }
            }
        }
        return null;
    }
    /**
     * Método que verifica se um número é inteiro ou real
     * @param indice lexema do numero
     * @return true: é inteiro, false: é real
     */
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
    // Abaixo o nome dos métodos seguem os nomes dos Síbolos NÃO terminais da gramática
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
        } else if (isType(token) || token.getTipo().equals("IDE")) {
            if (token.getTipo().equals("IDE")) {
                parentStruct = token.getLexema();
            }
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

            if (isStruct(token.getLexema(), escopo.peek())) {
                if (isStruct(parentStruct, escopo.peek()) || isStruct(parentStruct, "global")) {
                    if (isStruct(parentStruct, escopo.peek())) {
                        cloneStruct(parentStruct, escopo.peek(), token.getLexema(), escopo.peek());
                    } else {
                        cloneStruct(parentStruct, "global", token.getLexema(), escopo.peek());
                    }

                } else {
                    setErro(token.getLinha(), "The Struct \"" + parentStruct + "\" was not declared");
                }

            }
            // aqui deve ser feita a verificação se é struct para daí 
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
            } else if (token.getTipo().equals("IDE") || token.getLexema().equals("start")) {
                if (token.getLexema().equals("start")) {
                    setErro(token.getLinha(), "The function cannot contain the id start");
                }
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
                inReturn = false;

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
                escopo.pop();
                token = proximoToken();
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
            Token aux = lookAHead();
            if (aux.getLexema().equals("(")) {
                inCallProcCommands = true;
                callProcedureFunction();
                inCallProcCommands = false;
                if (token == null) {
                    //erro sintático
                    return;
                } else if (token.getLexema().equals(";")) {
                    token = proximoToken();
                } else {
                    //erro sintático
                }
                commands();
            } else {
                assignment();
                commands();
            }

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
                String ret = "";
                if (token.getLexema().equals("true") || token.getLexema().equals("false")) {
                    ret = "boolean";
                } else if (token.getTipo().equals("CDC")) {
                    ret = "string";
                } else if (token.getTipo().equals("NRO")) {
                    if (intVerification(token.getLexema())) {
                        ret = "int";
                    } else {
                        ret = "real";
                    }
                }

                if (!ret.equals(typeFunctionTemp)) {
                    setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                }
                token = proximoToken();

            } else if (token.getTipo().equals("IDE")) {

                Token aux = lookAHead();
                if (aux.getLexema().equals("(")) {

                    callProcedureFunction();

                } else {
                    callVariable();
                }

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
            inReturn = false;
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
            inCommands = true;
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
            inCommands = true;
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
            inAssign1 = true;
            callVariable();
            inAssign1 = false;
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("=")) {
                token = proximoToken();
                inAssign2 = true;
                assign2();
                if (typeAssign1 != null) {
                    if (typeAssign2 != null) {
                        if (!typeAssign1.equals(typeAssign2)) {
                            setErro(token.getLinha(), "The types atribuition not match");
                        }
                    }
                }
                inAssign2 = false;
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
//*********** Assign2 ********************************

    private void assign2() throws IOException {
        if (token == null) {
            //erro sintatico
            return;
        } else if (token.getLexema().equals("global") || token.getLexema().equals("local")) {
            callVariable();
        } else if (token.getTipo().equals("CDC") || token.getLexema().equals("true") || token.getLexema().equals("false")
                || token.getTipo().equals("NRO")) {

            if (token.getTipo().equals("NRO")) {

                if (checkType("int", token)) {
                    typeAssign2 = "int";
                } else {
                    typeAssign2 = "real";
                }

            } else if (token.getTipo().equals("CDC")) {
                typeAssign2 = "string";
            } else if (token.getLexema().equals("true") || token.getLexema().equals("false")) {
                typeAssign2 = "boolean";
            }
            token = proximoToken();
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("start")) {
            Token aux = lookAHead();
            if (aux.getLexema().equals("(")) {
                inCallProcFunct = true;
                callProcedureFunction();
            } else {
                callVariable();
            }

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
                    if (inAssign1 && !inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {

                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameAssign1Temp = token.getLexema();
                                    isVarGlobal = true;
                                } else {
                                    if (isConst(token.getLexema())) {
                                        setErro(token.getLinha(), "It is not possible to assign value to a CONSTANT");

                                    } else {
                                        typeAssign1 = getTypeVar(token.getLexema(), "global");
                                    }

                                }
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                                typeAssign1 = null;
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameAssign1Temp = token.getLexema();
                                } else {
                                    typeAssign1 = getTypeVar(token.getLexema(), escopo.peek());
                                }
                            }
                        }
                    }

                    if (inAssign2 && !inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                                typeAssign2 = null;
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameAssign2Temp = token.getLexema();
                                    isVarGlobal = true;
                                } else {
                                    if (!inIndexArrayCheck) {
                                        typeAssign2 = getTypeVar(token.getLexema(), "global");
                                    }

                                }
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                                typeAssign2 = null;
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameAssign2Temp = token.getLexema();
                                } else {
                                    if (!inIndexArrayCheck) {
                                        typeAssign2 = getTypeVar(token.getLexema(), escopo.peek());
                                    }
                                }
                            }
                        }
                    }

                    if (inReturn && !inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (Contains(token.getLexema(), "global")) {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    isVarGlobal = true;
                                    nameStructReturnTemp = token.getLexema();
                                } else {
                                    if (!inIndexArrayCheck) {
                                        if (!typeFunctionTemp.equals(getTypeVar(token.getLexema(), "global"))) {
                                            setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                                        }
                                    }
                                }

                            } else {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (Contains(token.getLexema(), escopo.peek())) {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameStructReturnTemp = token.getLexema();
                                } else {
                                    if (!inIndexArrayCheck) {
                                        if (!typeFunctionTemp.equals(getTypeVar(token.getLexema(), escopo.peek()))) {
                                            setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                                        }
                                    }
                                }

                            } else {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            }
                        }

                    }

                    if (inCallProcCommands && !inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    isVarGlobal = true;
                                    nameCallProcCommandsTemp = token.getLexema();
                                } else {
                                    if (!inIndexArrayCheck) {
                                        typeCallProcCommands = getTypeVar(token.getLexema(), escopo.peek());
                                        paramsStrTemporarios.add("@" + typeCallProcCommands);
                                    }
                                }
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameCallProcCommandsTemp = token.getLexema();
                                } else {
                                    if (!inIndexArrayCheck) {
                                        typeCallProcCommands = getTypeVar(token.getLexema(), escopo.peek());
                                        paramsStrTemporarios.add("@" + typeCallProcCommands);
                                    }
                                }
                            }
                        }
                    }

                    if (inCommands && !inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), "global");
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                            }
                        }
                        inCommands = false;
                    }
                    // verificação de índice de array
                    if (inIndexArrayCheck) {
                        if (modifierTemp.equals("global")) {
                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameIndexArrayTemp = token.getLexema();
                                    isVarGlobal = true;
                                } else {
                                    typeIndexArrayTemp = getTypeVar(token.getLexema(), "global");
                                    if (!typeIndexArrayTemp.equals("int")) {
                                        setErro(token.getLinha(), "The array index is not an integer type");
                                    }
                                }

                            }
                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameIndexArrayTemp = token.getLexema();
                                } else {
                                    typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                                    if (!typeCallVar.equals("int")) {
                                        setErro(token.getLinha(), "The array index is not an integer type");
                                    }
                                }

                            }
                        }

                    }

                    if (inCallProcFunct && !inIndexArrayCheck) {

                        if (modifierTemp.equals("global")) {
                            if (Contains(token.getLexema(), "global")) {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameCallProcFunctTemp = token.getLexema();
                                    isVarGlobal = true;
                                } else {
                                    paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), "global"));
                                }
                            } else {
                                setErro(token.getLinha(), "The var \"" + token.getLexema() + "\" was not declared");
                            }
                        } else if (modifierTemp.equals("local")) {
                            if (Contains(token.getLexema(), escopo.peek())) {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameCallProcFunctTemp = token.getLexema();
                                } else {

                                    paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), escopo.peek()));

                                }
                            } else {
                                setErro(token.getLinha(), "The var \"" + token.getLexema() + "\" was not declared");
                            }
                        }

                    }

                    if (inCallProcReturn && !inIndexArrayCheck) {

                        if (modifierTemp.equals("global")) {
                            if (!Contains(token.getLexema(), "global")) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameCallProcReturnTemp = token.getLexema();
                                    isVarGlobal = true;
                                } else {
                                    paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), "global"));

                                }
                            }

                        } else if (modifierTemp.equals("local")) {
                            if (!Contains(token.getLexema(), escopo.peek())) {
                                setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                            } else {
                                Token aux = lookAHead();
                                if (aux.getLexema().equals(".")) {
                                    nameCallProcReturnTemp = token.getLexema();
                                } else {
                                    paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), escopo.peek()));

                                }
                            }
                        }

                    }

                    token = proximoToken();
                }
            }
            //****** IDE PURO SEM MODIFICADOR ************
        } else if (token.getTipo().equals("IDE")) {
            if (inAssign1 && !inIndexArrayCheck) {
                if (!Contains(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    typeAssign1 = null;
                } else {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameAssign1Temp = token.getLexema();
                    } else {
                        typeAssign1 = getTypeVar(token.getLexema(), escopo.peek());
                    }

                }
            }

            if (inAssign2 && !inIndexArrayCheck) {
                if (!Contains(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                } else {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameAssign2Temp = token.getLexema();
                    } else {
                        typeAssign2 = getTypeVar(token.getLexema(), escopo.peek());
                        //OBS MAROTA
                    }
                }
            }

            if (inReturn && !inIndexArrayCheck) {
                if (Contains(token.getLexema(), escopo.peek())) {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameStructReturnTemp = token.getLexema();
                    } else {

                        if (!typeFunctionTemp.equals(getTypeVar(token.getLexema(), escopo.peek()))) {
                            setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                        }

                    }

                } else {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                }

            }

            // chamada de funções e procedimentos em comandos
            if (inCallProcCommands && !inIndexArrayCheck) {
                if (!Contains(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                } else {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameCallProcCommandsTemp = token.getLexema();
                    } else {

                        typeCallProcCommands = getTypeVar(token.getLexema(), escopo.peek());
                        paramsStrTemporarios.add("@" + typeCallProcCommands);

                    }
                }
            }

            // print e read
            if (inCommands && !inIndexArrayCheck) {
                if (!Contains(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                } else {
                    typeCallVar = getTypeVar(token.getLexema(), escopo.peek());
                    typeAssign1 = typeCallVar;
                }
                inCommands = false;
            }

            if (inIndexArrayCheck) {
                if (Contains(token.getLexema(), escopo.peek())) {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameIndexArrayTemp = token.getLexema();
                    } else {
                        typeIndexArrayTemp = getTypeVar(token.getLexema(), escopo.peek());
                        //inIndexArrayCheck = false;
                        if (!typeIndexArrayTemp.equals("int")) {
                            setErro(token.getLinha(), "The array index is not an integer type");
                        }
                    }
                } else {
                    setErro(token.getLinha(), "The var \"" + token.getLexema() + "\" was not declared");
                }

            }
            // chamada de funções em atribuição
            if (inCallProcFunct && !inIndexArrayCheck) {

                if (Contains(token.getLexema(), escopo.peek())) {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameCallProcFunctTemp = token.getLexema();
                    } else {
                        if (!inIndexArrayCheck) {
                            paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), escopo.peek()));
                        }
                    }
                } else {
                    setErro(token.getLinha(), "The var \"" + token.getLexema() + "\" was not declared");
                }
            }
            //chamada de funções e procedimentos em retorno
            if (inCallProcReturn && !inIndexArrayCheck) {
                if (!Contains(token.getLexema(), escopo.peek())) {
                    setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                } else {
                    Token aux = lookAHead();
                    if (aux.getLexema().equals(".")) {
                        nameCallProcReturnTemp = token.getLexema();
                    } else {
                        paramsStrTemporarios.add("@" + getTypeVar(token.getLexema(), escopo.peek()));

                    }
                }
            }

            nameAssign1Temp = token.getLexema();
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
            if (inAssign1 && !inIndexArrayCheck) {

                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameAssign1Temp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                        typeAssign1 = null;
                    } else {
                        typeAssign1 = getTypeVarInStruct(token.getLexema(), nameAssign1Temp, "global");
                    }
                    //isVarGlobal = false;
                } else {
                    if (!existInStruct(token.getLexema(), nameAssign1Temp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                        typeAssign1 = null;
                    } else {
                        typeAssign1 = getTypeVarInStruct(token.getLexema(), nameAssign1Temp, escopo.peek());
                    }
                }

            }

            if (inAssign2 && !inIndexArrayCheck) {

                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameAssign2Temp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        typeAssign2 = getTypeVarInStruct(token.getLexema(), nameAssign2Temp, "global");

                        if (inCallProcFunct && !inIndexArrayCheck) {
                            paramsStrTemporarios.add("@" + typeAssign2);
                        }
                    }

                    //isVarGlobal = false;
                } else {
                    if (!existInStruct(token.getLexema(), nameAssign2Temp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        typeAssign2 = getTypeVarInStruct(token.getLexema(), nameAssign2Temp, escopo.peek());

                        if (inCallProcFunct && !inIndexArrayCheck) {
                            paramsStrTemporarios.add("@" + typeAssign2);
                        }

                    }
                }

            }

            if (inCallProcFunct && !inIndexArrayCheck) {
                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameCallProcFunctTemp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        paramsStrTemporarios.add("@" + getTypeVarInStruct(token.getLexema(), nameCallProcFunctTemp, "global"));
                    }
                } else {
                    if (!existInStruct(token.getLexema(), nameCallProcFunctTemp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        paramsStrTemporarios.add("@" + getTypeVarInStruct(token.getLexema(), nameCallProcFunctTemp, escopo.peek()));
                    }
                }
            }

            if (inCallProcCommands && !inIndexArrayCheck) {
                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameCallProcCommandsTemp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {

                        if (!inIndexArrayCheck) {
                            typeCallProcCommands = getTypeVarInStruct(token.getLexema(), nameCallProcCommandsTemp, "global");
                            paramsStrTemporarios.add("@" + typeCallProcCommands);
                        }
                    }
                    //isVarGlobal = false;
                } else {
                    if (!existInStruct(token.getLexema(), nameCallProcCommandsTemp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {

                        if (!inIndexArrayCheck) {
                            typeCallProcCommands = getTypeVarInStruct(token.getLexema(), nameCallProcCommandsTemp, escopo.peek());
                            paramsStrTemporarios.add("@" + typeCallProcCommands);
                        }
                    }
                }

            }
            if (inReturn && !inIndexArrayCheck) {

                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameStructReturnTemp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {

                        if (!inIndexArrayCheck) {
                            if (!typeFunctionTemp.equals(getTypeVarInStruct(token.getLexema(), nameStructReturnTemp, "global"))) {
                                setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                            }
                        }
                    }
                    //isVarGlobal = false;
                } else {
                    if (!existInStruct(token.getLexema(), nameStructReturnTemp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        if (!inIndexArrayCheck) {
                            if (!typeFunctionTemp.equals(getTypeVarInStruct(token.getLexema(), nameStructReturnTemp, escopo.peek()))) {
                                setErro(token.getLinha(), "Return type of Function \"" + escopoTemp + "\" does not match");
                            }
                        }

                    }
                }

            }

            if (inIndexArrayCheck) {
                //System.out.println(token.getLexema()+" "+isVarGlobal);

                if (isVarGlobal) {
                    if (!existInStruct(token.getLexema(), nameIndexArrayTemp, "global")) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        if (!getTypeVarInStruct(token.getLexema(), nameIndexArrayTemp, "global").equals("int")) {
                            setErro(token.getLinha(), "The array index is not an integer type");
                        }
                    }
                } else {
                    if (!existInStruct(token.getLexema(), nameIndexArrayTemp, escopo.peek())) {
                        setErro(token.getLinha(), "The Var \"" + token.getLexema() + "\" was not declared");
                    } else {
                        if (!getTypeVarInStruct(token.getLexema(), nameIndexArrayTemp, escopo.peek()).equals("int")) {
                            setErro(token.getLinha(), "The array index is not an integer type");
                        }
                    }
                }
            }
            isVarGlobal = false;
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
                inIndexArrayCheck = true;
                callVariable();
                inIndexArrayCheck = false;
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
        } else if (token.getTipo().equals("IDE") || token.getLexema().equals("start")) {
            nameProcFunctTemp = token.getLexema();
            token = proximoToken();
            if (token == null) {
                //erro sintatico
                return;
            } else if (token.getLexema().equals("(")) {
                token = proximoToken();
                paramsStrTemporarios = new ArrayList();
                realParamList();
                if (inCallProcFunct) {
                    if (!tabSimbolos.containsKey(nameProcFunctTemp + parametros(paramsStrTemporarios))) {
                        setErro(token.getLinha(), "Function \"" + nameProcFunctTemp + parametros(paramsStrTemporarios) + "\" was not declared");
                    } else {
                        String typeF = getTypeFunctProc(nameProcFunctTemp, parametros(paramsStrTemporarios));
                        if (typeF.equals("")) {
                            setErro(token.getLinha(), "Procedure cannot be called in atribuition");
                            inCallProcFunct = false;
                        } else {
                            if (typeAssign1 != null) {
                                if (!typeAssign1.equals(typeF)) {
                                    setErro(token.getLinha(), "The function return is not of the type " + typeAssign1);
                                    inCallProcFunct = false;
                                } else {
                                    typeAssign2 = typeF;
                                }
                            }
                        }
                    }
                }

                if (inCallProcCommands) {
                    if (!tabSimbolos.containsKey(nameProcFunctTemp + parametros(paramsStrTemporarios))) {
                        setErro(token.getLinha(), "Function \"" + nameProcFunctTemp + parametros(paramsStrTemporarios) + "\" was not declared");
                    }
                }

                if (inCallProcReturn) {
                    if (!tabSimbolos.containsKey(nameProcFunctTemp + parametros(paramsStrTemporarios))) {
                        setErro(token.getLinha(), "Function \"" + nameProcFunctTemp + parametros(paramsStrTemporarios) + "\" was not declared");
                    } else {
                        String typeF = getTypeFunctProc(nameProcFunctTemp, parametros(paramsStrTemporarios));
                        if (typeF.equals("")) {
                            setErro(token.getLinha(), "Procedure cannot be called in return");

                        } else {
                            if (!typeFunctionTemp.equals(typeF)) {
                                setErro(token.getLinha(), "The function return is not of the type " + typeFunctionTemp);
                            }
                        }
                    }
                }
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
            //erro sintático
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
            if (intVerification(token.getLexema())) {
                paramsStrTemporarios.add("@int");
            } else {
                paramsStrTemporarios.add("@real");
            }
            token = proximoToken();
        } else if (token.getTipo().equals("CDC")) {
            paramsStrTemporarios.add("@string");
            token = proximoToken();
        } else if (token.getLexema().equals("true") || token.getLexema().equals("false")) {
            paramsStrTemporarios.add("@boolean");
            token = proximoToken();
        } else {
            //erro sintatico
        }
    }
}
