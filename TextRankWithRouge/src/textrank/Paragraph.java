package textrank;

import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MC48
 */
public class Paragraph{
    int number;
    ArrayList<Sentence> sentences;

    Paragraph(int number){
	this.number = number;
	sentences = new ArrayList<Sentence>();
    }
}
