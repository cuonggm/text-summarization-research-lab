package textrank;

import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MC48
 */
public class SentenceComparatorForSummary  implements Comparator<Sentence>{
	@Override
	public int compare(Sentence obj1, Sentence obj2) {
		if(obj1.number > obj2.number){
			return 1;
		}else if(obj1.number < obj2.number){
			return -1;
		}else{
			return 0;
		}
	}
}