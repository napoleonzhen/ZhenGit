package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer.Stemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.WHITESPACE)
public class WhitespaceRule implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		if (stream != null) {
			String token;
			Stemmer s;
			while (stream.hasNext()) { 
				token = stream.next();
				if (token != null) {
					token = token.toLowerCase();
					if (isLettersOnly(token)) {
						s = new Stemmer();
						for (char c: token.toCharArray()) {
							s.add(c);
						}
						
						s.stem();
						stream.set(s.toString());
					}
				}
				
			}
			
			stream.reset();
		}
	}

}
