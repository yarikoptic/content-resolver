package org.datacite.conres;

import org.junit.*;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MetadataTest {
    public String loadData(String fileName){
        InputStream is = getClass().getResourceAsStream(fileName);
        Scanner scanner = new Scanner(is, "UTF-8");
        StringBuilder sb = new StringBuilder();
        while(scanner.hasNextLine())
            sb.append(scanner.nextLine());

        return sb.toString();
    }
    
    @Test
    public void parsingTest1(){
        String xml = loadData("/test1.xml");
        assertNotNull(xml);
        assertTrue(xml.length() != 0);
        Map<MediaType,URI> media = new HashMap<MediaType, URI>();
        Metadata m = new Metadata(MockSearchServiceImpl.TEST_DOI, xml, media);
        assertEquals(3, m.getCreators().size());
        assertEquals(1, m.getTitles().size());
        assertEquals("Radiolaria abundance of Hole 24-232A", m.getTitles().get(0).value);
        assertTrue(m.getPublisher().startsWith("PANGAEA"));
        assertEquals("2005", m.getPublicationYear());
        assertEquals(32, m.getSubjects().size());
        assertEquals("Leg24", m.getSubjects().get(29).value);
        assertEquals("eng", m.getLanguage());
        assertEquals(1, m.getResourceType().size());
        assertEquals("Dataset", m.getResourceType().get(0).key);
        assertEquals("Dataset", m.getResourceType().get(0).value);
        assertEquals("Cites", m.getRelatedIdentifiers().get(0).key);
        assertEquals("doi:10.2973/dsdp.proc.24.1974", m.getRelatedIdentifiers().get(0).value);
        assertTrue(m.getSizes().get(0).startsWith("204"));
        assertEquals("text/tab-separated-values", m.getFormats().get(0));
    }

    @Test
    public void parsingTest2(){
        String xml = loadData("/test2.xml");
        assertNotNull(xml);
        assertTrue(xml.length() != 0);
        Map<MediaType,URI> media = new HashMap<MediaType, URI>();
        Metadata m = new Metadata(MockSearchServiceImpl.TEST_DOI, xml, media);
        assertEquals(1, m.getResourceType().size());
        assertEquals("Text", m.getResourceType().get(0).key);
        assertEquals("Report", m.getResourceType().get(0).value);
        assertEquals("IsPartOf", m.getRelatedIdentifiers().get(0).key);
        assertEquals("doi:10.5284/1000328", m.getRelatedIdentifiers().get(0).value);
        assertEquals("ADS Grey Lit ID", m.getAlternateIdentifiers().get(0).key);
        assertEquals("12824", m.getAlternateIdentifiers().get(0).value);
        assertEquals("OASIS ID", m.getAlternateIdentifiers().get(1).key);
        assertEquals("universi1-17791", m.getAlternateIdentifiers().get(1).value);
        assertTrue(m.getSizes().get(0).startsWith("1"));
        assertEquals("PDF", m.getFormats().get(0));
        assertTrue(m.getRights().startsWith("http"));
        assertEquals("Other", m.getDescriptions().get(0).key);
        assertTrue(m.getDescriptions().get(0).value.startsWith("A4"));
    }

    @Test
    public void parsingTest3(){
        String xml = loadData("/test3.xml");
        assertNotNull(xml);
        assertTrue(xml.length() != 0);
        Map<MediaType,URI> media = new HashMap<MediaType, URI>();
        Metadata m = new Metadata(MockSearchServiceImpl.TEST_DOI, xml, media);
        assertTrue(m.getTitles().get(0).value.startsWith("Diabetes"));
        assertEquals(2, m.getDescriptions().size());
        assertTrue(m.getDescriptions().get(0).value.length() != 0);
        assertTrue(m.getDescriptions().get(1).value.length() != 0);
        assertEquals("Available", m.getDates().get(0).key);
        assertEquals("2011-11-09", m.getDates().get(0).value);
    }
    
    @Test
    public void parsingTest4(){
        String xml = loadData("/test4.xml");
        assertNotNull(xml);
        assertTrue(xml.length() != 0);
        Map<MediaType,URI> media = new HashMap<MediaType, URI>();
        Metadata m = new Metadata(MockSearchServiceImpl.TEST_DOI, xml, media);
        assertEquals("HostingInstitution", m.getContributors().get(0).key);
        assertTrue(m.getContributors().get(0).value.startsWith("Institute"));
        assertEquals("1.0", m.getVersion());
    }
}