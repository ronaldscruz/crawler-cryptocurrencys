package br.com.cryptocurrency;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider {
	//Estabelece um limite de páginas para não retornar à pagina inicial por href
	private static final int MAX_PAGES_TO_SEARCH = 10;
	
	//O Set garante que as páginas visitadas sejam únicas e nunca se repitam
	private Set<String> pagesVisited = new HashSet<String>();
	
	//List apenas armazena a lista de urls a serem visitadas (não necessário, porem deixa o crawler consistente)
	private List<String> pagesToVisit = new LinkedList<String>();

	
	
	private String nextUrl() {		//METODO QUE DEFINE A PROXIMA URL A SER VISITADA
		String nextUrl;
		
		//remove a url da lista de pagesToVisit enquanto pagesVisited contenha a nextUrl
		//em outras palavras, caso a página seja visitada, ela sera removida da lista de paginas para serem visitadas, e entrará
		//na lista de pagesVisited
		
		do {
			nextUrl = this.pagesToVisit.remove(0);
		} while (this.pagesVisited.contains(nextUrl));
		
		//Adição da nextUrl ao SET de páginas visitadas
		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	public void search(String url, String searchWord)
    {
        while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
        {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();
            if(this.pagesToVisit.isEmpty())
            {
                currentUrl = url;
                this.pagesVisited.add(url);
            }
            else
            {
                currentUrl = this.nextUrl();
            }
            leg.crawl(currentUrl); // Lots of stuff happening here. Look at the crawl method in
                                   // SpiderLeg
            boolean success = false;
			try {
				success = leg.searchForWord(searchWord);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if(success)
            {
                System.out.println(String.format("**Success** Word %s found at %s", searchWord, currentUrl));
                break;
            }
            this.pagesToVisit.addAll(leg.getLinks());
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
}
