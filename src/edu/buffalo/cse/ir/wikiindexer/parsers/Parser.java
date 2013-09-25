/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 *
 */
public class Parser {
	/* */
	private final Properties props;
	
	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}
	
	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		System.out.println("parse: " + filename + "\n");
		try
		{
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			Document doc = b.parse(new FileInputStream(filename));
			doc.getDocumentElement().normalize();			
			// loop through each item
			// devide the page
			NodeList items = doc.getElementsByTagName("page");
		    for (int i = 0; i < items.getLength(); i++) {
				Node node = items.item(i);
				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) node;
				
                // parse the title
				NodeList titleList = element.getElementsByTagName("title");
				Element titleElem = (Element) titleList.item(0);
				Node titleNode = titleElem.getChildNodes().item(0);
				String title = titleNode.getNodeValue();
				System.out.println(title);
				
                // parse the id
				NodeList idList = element.getElementsByTagName("id");
				Element idElem = (Element) idList.item(0);
				Node idNode = idElem.getChildNodes().item(0);
				int id = Integer.parseInt(idNode.getNodeValue());
				System.out.println(id);
				
				// parse the timestamp
				NodeList timeList = element.getElementsByTagName("timestamp");
				Element timeElem = (Element) timeList.item(0);
				Node timeNode = timeElem.getChildNodes().item(0);
				String time = timeNode.getNodeValue();
				System.out.println(time);
				
				// parse the author, either username or ip
				String user;
				try{
					NodeList userList = element.getElementsByTagName("ip");
					Element userElem = (Element) userList.item(0);
					Node userNode = userElem.getChildNodes().item(0);
					user = userNode.getNodeValue();
					System.out.println(user);
				} catch (NullPointerException e) {
					NodeList userList = element.getElementsByTagName("username");
					Element userElem = (Element) userList.item(0);
					Node userNode = userElem.getChildNodes().item(0);
					user = userNode.getNodeValue();
					System.out.println(user);
				}
				
				// parse the text in order to further process
				NodeList textList = element.getElementsByTagName("text");
				Element textElem = (Element) textList.item(0);
				Node textNode = textElem.getChildNodes().item(0);
				String text = textNode.getNodeValue();
//				System.out.println(text);
				
				WikipediaDocument wd = new WikipediaDocument(id, time, user, title);
				
				// parse the Category
				Matcher m_category = Pattern.compile("\\[\\[Category:(.*)\\]\\]").matcher(text);
				while(m_category.find()){
					String category = m_category.group(1);
					//! here is a problem, change protected to public
					wd.addCategory(category);
					System.out.println(category);
				}
				
				// parse the section, including section title and text
				String[] sec_text = Pattern.compile("(?m)^==(.*[^=])==$").split(text);				
				Matcher m_section = Pattern.compile("(?m)^==(.*[^=])==$").matcher(text);
				int i_sec = 1;
				if (!sec_text[0].isEmpty()){
					wd.addSection("Default", sec_text[0]);
				}
				while(m_section.find()){
					String sec_title = m_section.group(1);
					wd.addSection(sec_title, sec_text[i_sec]);
//					System.out.print("sec_title: " + sec_title + "\n");
//					System.out.print("sec_text: " + sec_text[i_sec] + "\n");
					i_sec = i_sec + 1;
				}
				
				// parse the link
				Matcher m_link = Pattern.compile("(?m)\\[\\[(.*?)\\]\\]").matcher(text);
				while(m_link.find()){
					String link = m_link.group(1);
					// add more rules for other kinds of links
					// multiple process of regular expression
					if (!link.contains("Category")){
//						wd.addLink(link);
//						System.out.println("link: " + link);
						if (link.contains("|")){
							String[] ver_link = Pattern.compile("\\|").split(link);
							wd.addLink(ver_link[1]);
							System.out.println("link: " + ver_link[0]);
						} else {
							wd.addLink(link);
							System.out.println("link: " + link);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
