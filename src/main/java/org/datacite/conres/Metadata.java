package org.datacite.conres;

import nu.xom.*;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Pair {
    final String key;
    final String value;

    Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (!key.equals(pair.key)) return false;
        if (!value.equals(pair.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}

/**
 * Represents metadata record (parsed from XML)
 */
public class Metadata {
    private String xml;
    private String doi;
    private Map<MediaType, URI> media;
    private Document document;
    private List<String> creators;
    private String publicationYear;
    private List<Pair> titles;
    private String publisher;
    private List<Pair> resourceType;
    private List<Pair> descriptions;
    private List<Pair> subjects;
    private List<String> sizes;
    private String rights;
    private List<Pair> dates;
    private String language;
    private List<String> formats;
    private String version;
    private List<Pair> alternateIdentifiers;
    private List<Pair> relatedIdentifiers;
    private List<Pair> contributors;

    public Metadata(String doi, String xml, Map<MediaType, URI> media) {
        this.doi = doi;
        this.xml = xml;
        this.media = media;

        Builder parser = new Builder();
        try {
            document = parser.build(xml, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.creators = extractList("creatorName");
        this.publicationYear = extractText("publicationYear");
        this.titles = extractPairs("title", "titleType");
        this.publisher = extractText("publisher");
        this.resourceType = extractPairs("resourceType", "resourceTypeGeneral");
        this.descriptions = extractPairs("description", "descriptionType");
        this.subjects = extractPairs("subject", "subjectScheme");
        this.sizes = extractList("size");
        this.rights = extractText("rights");
        this.dates = extractPairs("date", "dateType");
        this.language = extractText("language");
        this.formats = extractList("format");
        this.version = extractText("version");
        this.alternateIdentifiers = extractPairs("alternateIdentifier", "alternateIdentifierType");
        this.relatedIdentifiers = extractRelatedIds();
        this.contributors = extractContributors();
    }

    private List<Pair> extractRelatedIds() {
        Nodes nodes = document.query("//*[local-name() = 'relatedIdentifier']");
        List<Pair> result = new ArrayList<Pair>();
        for(int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            Element el = (Element) node;
            Attribute idType = el.getAttribute("relatedIdentifierType");
            Attribute relType = el.getAttribute("relationType");
            String idTypeStr = "";
            String relTypeStr = "";
            if (relType != null)
                relTypeStr = relType.getValue().trim();
            if (idType != null)
                idTypeStr = idType.getValue().trim();
            result.add(new Pair(relTypeStr, idTypeStr.toLowerCase() + ":" + node.getValue().trim()));
        }

        return result;
    }

    private List<Pair> extractContributors() {
        Nodes nodes = document.query("//*[local-name() = 'contributor']");
        List<Pair> result = new ArrayList<Pair>();
        for(int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            Element el = (Element) node;
            Attribute conType = el.getAttribute("contributorType");
            String conTypeStr = "";
            if (conType != null)
                conTypeStr = conType.getValue().trim();
            result.add(new Pair(conTypeStr, node.getValue().trim()));
        }

        return result;
    }

    private List<Pair> extractPairs(String elementName, String attributeName) {
        List<Pair> result = new ArrayList<Pair>();
        Nodes nodes = document.query("//*[local-name() = '" + elementName + "']");

        for(int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            Element el = (Element) node;
            Attribute attribute = el.getAttribute(attributeName);
            String attr = "";
            if (attribute != null)
                attr = attribute.getValue();
            result.add(new Pair(attr, node.getValue().trim()));
        }

        return result;
    }

    private String extractText(String elementName) {
        StringBuilder sb = new StringBuilder();
        Nodes nodes = document.query("//*[local-name() = '" + elementName + "']");

        for(int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            sb.append(node.getValue().trim());
        }

        return sb.toString();
    }

    private List<String> extractList(String elementName){
        List<String> result = new ArrayList<String>();
        Nodes nodes = document.query("//*[local-name() = '" + elementName + "']");

        for(int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            result.add(node.getValue().trim());
        }

        return result;
    }

    public String getDoi() {
        return doi;
    }

    public String getXml() {
        return xml;
    }

    public List<String> getCreators() {
        return creators;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public List<Pair> getTitles() {
        return titles;
    }

    public String getPublisher() {
        return publisher;
    }

    public List<Pair> getResourceType() {
        return resourceType;
    }

    public List<Pair> getDescriptions() {
        return descriptions;
    }

    public List<Pair> getSubjects() {
        return subjects;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public String getRights() {
        return rights;
    }

    public List<Pair> getDates() {
        return dates;
    }

    public String getLanguage() {
        return language;
    }

    public List<String> getFormats() {
        return formats;
    }

    public String getVersion() {
        return version;
    }

    public List<Pair> getAlternateIdentifiers() {
        return alternateIdentifiers;
    }

    public List<Pair> getRelatedIdentifiers() {
        return relatedIdentifiers;
    }

    public List<Pair> getContributors() {
        return contributors;
    }

    public List<String> getAllMedia(){
        List<String> allSupportedTypes = new ArrayList<String >();
        for (MediaType m : media.keySet()) {
            allSupportedTypes.add(m.toString());
        }
        for(Representation c: Representation.values()) {
            allSupportedTypes.add(c.toString());
        }

        return allSupportedTypes;
    }
}