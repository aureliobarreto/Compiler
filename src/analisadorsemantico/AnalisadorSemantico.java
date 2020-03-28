/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorsemantico;

import analisadorlexico.Token;
import analisadorsintatico.AnalisadorSintatico;
import analisadorsintatico.ItensSemantico;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Aurelio
 * @author Werisson
 */
public class AnalisadorSemantico {

    public void analiseSemantica() throws IOException {
        AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico();
        ItensSemantico Is = analisadorSintatico.analiseSintatica();
        ArrayList<HashMap<String, Object>> arraysDeTabelaDeSimbolos = Is.getTabelasDeSimbolos();
        ArrayList<ArrayList<Token>> arrayDeArrays = Is.getArryDeArrays();
        ArrayList<Token> arrayDeTokens;
        HashMap<String, Object> tabSimbolo;
        AcoesSemanticas acoesSemanticas;
        int cont = 1;
        Iterator it = arrayDeArrays.iterator(), it1 = arraysDeTabelaDeSimbolos.iterator();

        while (it.hasNext()) {
            arrayDeTokens = (ArrayList) it.next();
            tabSimbolo = (HashMap) it1.next();
            acoesSemanticas = new AcoesSemanticas(arrayDeTokens, cont, tabSimbolo);
            acoesSemanticas.run();
            cont++;
        }

    }

    public static void main(String args[]) throws IOException {
        AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
        analisadorSemantico.analiseSemantica();
    }

}
