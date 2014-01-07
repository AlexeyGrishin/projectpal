package io.github.alexeygrishin.pal.tools;

import pal.Pal;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents single Pal module (class, module or something else, depending on language)
 * as a set of named sections - import section, class start, function A body, function B body, etc.
 *
 * May be used on client side for parsing the server response and merging with existent class
 */
public class Module {
    private String asString;
    private List<Section> listOfSections = new LinkedList<Section>();
    private Map<Key, Section> dictionary = new HashMap<Key, Section>();
    public static final String ENDL = System.getProperty("line.separator");

    public Module(String body) {
        parseSections(body);
        asString = body;
    }

    private void parseSections(String body) {
        Matcher matcher = LINE_WITH_ID.matcher(body);
        if (!matcher.find()) throw new IllegalArgumentException("Cannot find any section in body: '" + body + "'");
        int copyFrom = matcher.start();
        int searchFrom = matcher.end();
        while (matcher.find(searchFrom)) {
            int copyTo = matcher.start();
            listOfSections.add(new Section(body.substring(copyFrom, copyTo)));
            copyFrom = copyTo;
            searchFrom = matcher.end();
        }
        listOfSections.add(new Section(body.substring(copyFrom)));
        for (Section s: listOfSections) {
            if (dictionary.containsKey(s.key))
                throw new IllegalArgumentException("Section with key " + s.key + " met twice");
            dictionary.put(s.key, s);
        }
    }

    private String joinSections() {
        return Pal.join(listOfSections, "");
    }

    //mostly for tests. returns section body or null if section with that type/id not found
    public String getSection(String type, String id) {
        Section s = dictionary.get(new Key(type, id));
        return s == null ? null : s.body;
    }

    public List<String> getIdsForType(String type) {
        List<String> ids = new ArrayList<String>(listOfSections.size());
        for (Section s: listOfSections) {
            if (s.key.type.equals(type))
                ids.add(s.key.id);
        }
        return ids;
    }


    public String toString() {
        return asString;
    }

    class Key {
        public final String type;
        public final String id;
        public final String fullId;

        Key(String type, String id) {
            this.type = type.trim();
            this.id = trimOrNull(id);
            fullId = initFullId(type, id);
        }

        private String initFullId(String type, String id) {
            if (id == null)
                return type;
            else
                return type + " " + id;
        }

        Key(String body) {
            int lineEnd = body.indexOf("\n");
            String firstLine = lineEnd != -1 ? body.substring(0, lineEnd) : body;
            Matcher m = ID_IN_LINE.matcher(firstLine);
            if (!m.matches()) throw new IllegalArgumentException("First line shall have ID of section in form ':section [idWithSpace]', but here is '" + firstLine + "'");
            type = m.group(1).trim();
            String idWithSpace = m.groupCount() > 1 ? m.group(2) : null;
            id = trimOrNull(idWithSpace);
            fullId = initFullId(type, id);
        }

        private String trimOrNull(String id) {
            String trimmed = id != null ? id.trim() : null;
            if (trimmed != null && trimmed.length() > 0) return trimmed;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!fullId.equals(key.fullId)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return fullId.hashCode();
        }

        public String toString() {
            return fullId;
        }

    }

    class Section {
        public final Key key;
        public final String body;

        Section(Key key, String body) {
            this.key = key;
            this.body = body;
        }

        Section(String body) {
            this.body = body;
            key = new Key(body);
        }

        public String toString() {
            return body;
        }
    }

    private final static String ID_DEF = ":([_a-zA-Z0-9]+)(\\s+[^\\s]*)?";
    private final static Pattern ID_IN_LINE = Pattern.compile("^.*" + ID_DEF + "$");
    private final static Pattern LINE_WITH_ID = Pattern.compile("^.*:[_a-zA-Z0-9]+([ \\t]+[^\\s]*)?.*$", Pattern.MULTILINE);
}
