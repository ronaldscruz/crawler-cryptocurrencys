package br.com.cryptocurrency;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg
{
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    /**
     * This performs all the work. It makes an HTTP request, checks the response, and then gathers
     * up all the links on the page. Perform a searchForWord after the successful crawl
     * 
     * @param url
     *            - The URL to visit
     * @return whether or not the crawl was successful
     */
    public boolean crawl(String url)
    {
        try
        {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visitando** Página detectada em " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Falha** Algo diferente de HTML detectado");
                return false;
            }
            
            //linksOnPage recebe tudo que contenha a[href] no documento
            Elements linksOnPage = htmlDocument.select("a[href]");
            
            //Mostra a quantidade de links encontrados
            System.out.println("Encontrou (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage)
            {
                this.links.add(link.absUrl("href"));
            }
            
            return true;
        }
        catch(IOException ioe)
        {
            // We were not successful in our HTTP request
            return false;
        }
    }


    /**
     * Performs a search on the body of on the HTML document that is retrieved. This method should
     * only be called after a successful crawl.
     * 
     * @param searchWord
     *            - The word or string to look for
     * @return whether or not the word was found
     * @throws IOException 
     */
    public boolean searchForWord(String searchWord) throws IOException
    {
    	FileWriter cod = new FileWriter("codigo.html");
    	FileWriter txt = new FileWriter("texto.txt");
    	PrintWriter gravarCod = new PrintWriter(cod);
    	PrintWriter gravarTxt = new PrintWriter(txt);
    	
        // Defensive coding. This method should only be used after a successful crawl.
        if(this.htmlDocument == null)
        {
            System.out.println("ERRO! HTML não detectado (endereço inválido)");
            return false;
        }
        System.out.println("Procurando pela palavra " + searchWord + "...");
        
        Document bodyCode = this.htmlDocument;
        String bodyText = this.htmlDocument.body().text();
        
        gravarCod.println(bodyCode);
        gravarTxt.println(bodyText);
        cod.close();
        txt.close();
        
        //System.out.println(htmlDocument);
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }


    public List<String> getLinks()
    {
        return this.links;
    }

}